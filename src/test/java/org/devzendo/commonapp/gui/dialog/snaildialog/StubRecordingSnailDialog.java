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

package org.devzendo.commonapp.gui.dialog.snaildialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.SwingWorker;


/**
 * A SnailDialog that initialises a worker that records the threads it has been
 * initialised on.
 * 
 * @author matt
 *
 */
public final class StubRecordingSnailDialog extends AbstractSnailDialog {
    private static final long serialVersionUID = -5310917886433456342L;
    private static final Logger LOGGER = Logger
            .getLogger(StubRecordingSnailDialog.class);
    private volatile boolean isInitialised = false;
    private volatile boolean isSwingWorkerConstructedOnNonEventThread = false;

    private volatile boolean isFinishedOnEventThread = false;
    
    /**
     * Construct a recording snail
     * @param parentFrame the parent
     * @param manager the cursor manager
     */
    public StubRecordingSnailDialog(final Frame parentFrame, final CursorManager manager) {
        super(parentFrame, manager, "Stub");
        LOGGER.debug("Constructed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Container createMainComponent() {
        LOGGER.debug("getMainComponent called");
        final JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(200, 200));
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialise() {
        LOGGER.debug("initialise called");
        isInitialised = true;
        final SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
                LOGGER.debug("StubRecordingSnailDialog SwingWorker - construct called");
                isSwingWorkerConstructedOnNonEventThread = !EventQueue.isDispatchThread();
                return new Object();
            }
            
            @Override
            public void finished() {
                LOGGER.debug("StubRecordingSnailDialog SwingWorker - finished called");
                isFinishedOnEventThread = EventQueue.isDispatchThread();
            }
        };
        addSwingWorker(worker);
        LOGGER.debug("Out of initialise");
    }

    /**
     * Has the initialise method been called?
     * @return true iff it has
     */
    public boolean isInitialised() {
        return isInitialised;
    }

    /**
     * Has the construct method of the SwingWorker been called on a non EDT?
     * @return true iff called on a non EDT
     */
    public boolean isSwingWorkerConstructedOnNonEventThread() {
        return isSwingWorkerConstructedOnNonEventThread;
    }

    /**
     * Has the finished method of the SwingWorker been called on an EDT?
     * @return true iff called on an EDT
     */
    public boolean isFinishedOnEventThread() {
        return isFinishedOnEventThread;
    }
}
