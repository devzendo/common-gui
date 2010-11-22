/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.commonapp.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JFrame;
import javax.swing.JRootPane;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.string.StringUtils;


/**
 * Handlings make benefit great user interaction of hourglass/normal cursor.
 * @author borat
 *
 */
public final class CursorManager {
    private static final Logger LOGGER = Logger.getLogger(CursorManager.class);
    private static final Cursor HOURGLASS = new Cursor(Cursor.WAIT_CURSOR);
    private static final Cursor NORMAL = new Cursor(Cursor.DEFAULT_CURSOR);
    private JFrame frame = null;

    private AtomicBoolean hourglass = new AtomicBoolean(false);
    private AtomicLong hourglassSetTime = new AtomicLong(0);
    private Thread stuckDetector;
    private Object lock;
    private List<String> hourglassCallers;
    

    /**
     * Instantiate the CursorManager, which won't be able to effect change
     * of the cursor until a main component has been set.
     */
    public CursorManager() {
        startStuckDetector();
    }

    private void startStuckDetector() {
        lock = new Object();
        hourglassCallers = new ArrayList<String>();
        stuckDetector = new Thread(new StuckHourGlassDetector());
        stuckDetector.setDaemon(true);
        stuckDetector.setName("Stuck Hourglass Detector");
        stuckDetector.start();
    }
    
    /**
     * Set the application's main frame.
     * @param mainFrame the main application's frame
     */
    public void setMainFrame(final JFrame mainFrame) {
        frame = mainFrame;
        LOGGER.debug("CursorManager's main frame has been set to " + mainFrame);
    }
    
    /**
     * Set the hourglass cursor, if a main component has been set.
     * @param caller the name of the caller, for stuck hourglass detection
     */
    public void hourglass(final String caller) {
        LOGGER.debug("Setting hourglass cursor");
        if (frame != null) {
            frame.setCursor(HOURGLASS);
            hourglassCallers.add(caller);
            hourglass.set(true);
            hourglassSetTime.set(System.currentTimeMillis());
            synchronized (lock) {
                lock.notify();
            }
        }
        final Component glassPane = getGlassPane();
        if (glassPane != null) {
            glassPane.setEnabled(false);
            glassPane.setVisible(true);
        }
    }
    
    /**
     * Set the hourglass cursor, if a main component has been set. This
     * always runs on the event thread. If you're sure you're already on
     * the event thread, use hourglass().
     * @param caller the name of the caller, for stuck hourglass detection
     */
    public void hourglassViaEventThread(final String caller) {
        final Runnable r = new Runnable() {
            public void run() {
                hourglass(caller);
            }
        };
        GUIUtils.runOnEventThread(r);
    }

    /**
     * Set the normal cursor, if the main component has been set. 
     * @param caller the name of the caller, for stuck hourglass detection
     */
    public void normal(final String caller) {
        LOGGER.debug("Setting normal cursor");
        if (frame != null) {
            frame.setCursor(NORMAL);
            hourglass.set(false);
            hourglassSetTime.set(0);
            if (hourglassCallers.size() > 0) {
                final int lastIndex = hourglassCallers.size() - 1;
                if (hourglassCallers.get(lastIndex).equals(caller)) {
                    hourglassCallers.remove(lastIndex);
                }
            }
            synchronized (lock) {
                lock.notify();
            }
        }
        final Component glassPane = getGlassPane();
        if (glassPane != null) {
            glassPane.setVisible(false);
            glassPane.setEnabled(true);
            glassPane.setCursor(NORMAL);
        }
    }

    /**
     * Set the normla cursor, if a main component has been set. This
     * always runs on the event thread. If you're sure you're already on
     * the event thread, use normal().
     * @param caller the name of the caller, for stuck hourglass detection
     */
    public void normalViaEventThread(final String caller) {
        final Runnable r = new Runnable() {
            public void run() {
                normal(caller);
            }
        };
        GUIUtils.runOnEventThread(r);
    }

    private Component getGlassPane() {
        if (frame == null) {
            LOGGER.warn("Frame is null");
            return null;
        }
        final JRootPane rootPane = frame.getRootPane();
        if (rootPane == null) {
            LOGGER.warn("JRootPane is null");
            return null;
        }
        final Component glassPane = rootPane.getGlassPane();
        if (glassPane == null) {
            LOGGER.warn("GlassPane is null");
        }
        return glassPane;
    }
    
    /**
     * If the hourglass is present for more than 30s, it's most likely a
     * problem.
     * @author matt
     *
     */
    private class StuckHourGlassDetector implements Runnable {      
        public void run() {
            while (Thread.currentThread().isAlive()) {
                if (hourglass.get()) {
                    // hourglass
                    try {
                        LOGGER.debug("waiting a while for hourglass to get stuck");
                        synchronized (lock) {
                            lock.wait(30000);
                        }
                        if (hourglass.get()) {
                            LOGGER.debug("in hourglass state");
                            if (hourglassSetTime.get() != 0) {
                                final long stuckFor = System.currentTimeMillis() - hourglassSetTime.get();
                                if (stuckFor > 28000) {
                                    LOGGER.warn("The hourglass cursor appears to have been stuck for " + StringUtils.translateTimeDuration(stuckFor));
                                    for (String caller : hourglassCallers) {
                                        LOGGER.warn("  " + caller);
                                    }
                                } else {
                                    LOGGER.debug("Only been in hourglass for " + StringUtils.translateTimeDuration(stuckFor));
                                }
                            } else {
                                LOGGER.debug("hourglass set time if zero");
                            }
                        } else {
                            LOGGER.debug("in normal state");
                        }
                    } catch (final InterruptedException e) {
                        LOGGER.debug("interrupted in hourglass state");
                        // nothing
                    }
                } else {
                    // normal
                    try {
                        LOGGER.debug("waiting for hourglass...");
                        synchronized (lock) {
                            lock.wait();
                        }
                        LOGGER.debug("out of wait for hourglass");
                    } catch (final InterruptedException e) {
                        LOGGER.debug("interrupted in normal state, perhaps in hourglass now?");
                        // nothing
                    }
                }
            }
        }
    }
}
