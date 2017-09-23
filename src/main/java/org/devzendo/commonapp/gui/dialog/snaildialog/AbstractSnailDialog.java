/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org http://devzendo.org
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

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.SwingWorker;


/**
 * An AbstractSnailDialog is a modal JDialog that takes a long time to
 * initialise to its final state.
 *
 * Examples might be the Tools->Options dialog (which loads several beans, one
 * for each tab), and the About dialog (which loads several pages of text).
 *
 * AbstractSnailDialogs use a two-phase construction to ensure
 * correct storage of subclass instance data before any real work
 * is done.
 *
 * Subclass constructors should upcall to super(), then store any
 * constructor parameters they need, and NO MORE.
 *
 * Client code should call the subclass constructor, then call
 * postConstruct(). This ensures that all subclass instance data
 * is stored correctly. 
 *
 * After postConstruct(), the hourglass cursor will be shown, and once
 * construction is finished, it will be set back to normal. 
 *
 * During initialisation, several SwingWorkers can be constructed and added
 * to a list. Once the dialog is visible, these will be executed (then the
 * cursor is set back normal).
 * @author matt
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractSnailDialog extends JDialog {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractSnailDialog.class);
    private final CursorManager cursorManager;
    private final List<SwingWorker> workers;
    private final Object lock;
    private AWTEventListener awtEventListener;

    /**
     * Construct the AbstractSnailDialog
     * @param parentFrame the parent frame; this is a modal dialog
     * @param cursor the cursor manager
     * @param title the title for this frame, which could be left empty and set
     * via one of the workers initialised by the initialise method.
     */
    public AbstractSnailDialog(final Frame parentFrame, final CursorManager cursor, final String title) {
        super(parentFrame, true);
        cursorManager = cursor;
        setTitle(title);
        lock = new Object();
        workers = new ArrayList<SwingWorker>();
    }

    /**
     * After subclass construction, call this to start the
     * construction process.
     */
    public final void postConstruct() {
        cursorManager.hourglassViaEventThread(this.getClass().getSimpleName());
        setContentPane(createMainComponent());
        initialise();
        addCursorNormalWorker();
        
        // Load and display the about texts after the window has been
        // made visible. Performance legerdemain...
        awtEventListener = new AWTEventListener() {
                            public void eventDispatched(final AWTEvent event) {
                                if (event.getID() == WindowEvent.WINDOW_OPENED && event.getSource() == AbstractSnailDialog.this) {
                                    synchronized (lock) {
                                        final Thread swingWorkerExecutorThread = new Thread(new Runnable() {
                                            public void run() {
                                                LOGGER.debug("executing all swing workers");
                                                for (SwingWorker worker : workers) {
                                                    LOGGER.debug("executing swing worker");
                                                    worker.start();
                                                    LOGGER.debug("executed swing worker");
                                                }
                                                LOGGER.debug("executed all swing workers");
                                            }
                                        });
                                        swingWorkerExecutorThread.setName("SwingWorkerExecutor");
                                        swingWorkerExecutorThread.start();
                                    }
                                }
                            }
                            };
        Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, AWTEvent.WINDOW_EVENT_MASK);
    }
    
    /**
     * Create and return the dialog's main component; might be a JPanel or
     * JOptionPane into which other items might be dynamically loaded during
     * initialise.
     * @return the dialog's main component
     */
    protected abstract Container createMainComponent();

    private void addCursorNormalWorker() {
        addSwingWorker(new SwingWorker() {

            @Override
            public Object construct() {
                LOGGER.debug("Normal Cursor SwingWorker - construct");
                return null;
            }
            
            @Override
            public void finished() {
                LOGGER.debug("Normal Cursor SwingWorker - finished");
                cursorManager.normal(this.getClass().getSimpleName());
            }
        });
    }

    /**
     * Called by subclasses' initialise method, add a SwingWorker to the list
     * of workers that will be executed when the window is visible.
     * @param worker the worker to execute upon visibility.
     */
    protected final void addSwingWorker(final SwingWorker worker) {
        synchronized (lock) {
            workers.add(worker);
        }
    }
    
    /**
     * Initialise the dialog by adding SwingWorkers to the worker list.
     */
    protected abstract void initialise();


    /**
     * This method clears the dialog and hides it.
     */
    public final void clearAndHide() {
        if (awtEventListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
        }
        setVisible(false);
        cursorManager.normal(this.getClass().getSimpleName());
        dispose();
    }
}
