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
 * Allows applications to easily set/remove the hourglass cursor, and detect
 * when an app is 'stuck' with the hourglass.
 */
public final class DefaultCursorManager {
    private static final Logger LOGGER = Logger.getLogger(DefaultCursorManager.class);
    private static final Cursor HOURGLASS = new Cursor(Cursor.WAIT_CURSOR);
    private static final Cursor NORMAL = new Cursor(Cursor.DEFAULT_CURSOR);
    private JFrame mMainFrame = null;

    private final AtomicBoolean mHourglassCursorActive = new AtomicBoolean(false);
    private final AtomicLong mHourglassSetTime = new AtomicLong(0);
    private Thread mStuckDetectorThread;
    private Object mLock;
    private List<String> mHourglassCallers;
    private boolean mAlive = true;
    

    /**
     * Instantiate the CursorManager, which won't be able to effect change
     * of the cursor until a main component has been set.
     */
    public DefaultCursorManager() {
        startStuckDetector();
    }

    private void startStuckDetector() {
        mLock = new Object();
        mHourglassCallers = new ArrayList<String>();
        mStuckDetectorThread = new Thread(new StuckHourGlassDetector());
        mStuckDetectorThread.setDaemon(true);
        mStuckDetectorThread.setName("Stuck Hourglass Detector");
        mStuckDetectorThread.start();
    }
    
    /**
     * @return the application's main frame.
     */
    public JFrame getMainFrame() {
        return mMainFrame;
    }

    /**
     * Set the application's main frame.
     * @param mainFrame the main application's frame
     */
    public void setMainFrame(final JFrame mainFrame) {
        mMainFrame = mainFrame;
        LOGGER.debug("CursorManager's main frame has been set to " + mainFrame);
    }
    
    /**
     * Shut down the stuck hourglass detection thread.
     */
    public void shutdown() {
        mAlive = false;
        mStuckDetectorThread.interrupt();
    }
    
    /**
     * Set the hourglass cursor, if a main component has been set.
     * @param caller the name of the caller, for stuck hourglass detection
     */
    public void hourglass(final String caller) {
        LOGGER.debug("Setting hourglass cursor");
        if (mMainFrame != null) {
            mMainFrame.setCursor(HOURGLASS);
            mHourglassCallers.add(caller);
            mHourglassCursorActive.set(true);
            mHourglassSetTime.set(System.currentTimeMillis());
            synchronized (mLock) {
                mLock.notify();
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
        if (mMainFrame != null) {
            mMainFrame.setCursor(NORMAL);
            mHourglassCursorActive.set(false);
            mHourglassSetTime.set(0);
            if (mHourglassCallers.size() > 0) {
                final int lastIndex = mHourglassCallers.size() - 1;
                if (mHourglassCallers.get(lastIndex).equals(caller)) {
                    mHourglassCallers.remove(lastIndex);
                }
            }
            synchronized (mLock) {
                mLock.notify();
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
     * Set the normal cursor, if a main component has been set. This
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
        if (mMainFrame == null) {
            LOGGER.warn("Frame is null");
            return null;
        }
        final JRootPane rootPane = mMainFrame.getRootPane();
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
            while (mAlive && Thread.currentThread().isAlive()) {
                if (mHourglassCursorActive.get()) {
                    // hourglass
                    try {
                        LOGGER.debug("waiting a while for hourglass to get stuck");
                        synchronized (mLock) {
                            mLock.wait(30000);
                        }
                        if (mHourglassCursorActive.get()) {
                            LOGGER.debug("in hourglass state");
                            if (mHourglassSetTime.get() != 0) {
                                final long stuckFor = System.currentTimeMillis() - mHourglassSetTime.get();
                                if (stuckFor > 28000) {
                                    LOGGER.warn("The hourglass cursor appears to have been stuck for " + StringUtils.translateTimeDuration(stuckFor));
                                    for (final String caller : mHourglassCallers) {
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
                        synchronized (mLock) {
                            mLock.wait();
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
