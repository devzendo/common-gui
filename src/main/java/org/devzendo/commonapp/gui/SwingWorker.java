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

import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on and examples of using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorker {
    private Object myValue; // see getValue(), setValue()

    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class ThreadVar {
        private Thread myThread;

        ThreadVar(final Thread t) {
            myThread = t;
        }

        synchronized Thread get() {
            return myThread;
        }

        synchronized void clear() {
            myThread = null;
        }
    }

    private final ThreadVar myThreadVar;

    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     * @return the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     */
    protected synchronized Object getValue() {
        return myValue;
    }

    /**
     * Set the value produced by worker thread
     */
    private synchronized void setValue(final Object x) {
        myValue = x;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     * @return the value to be returned by get().
     */
    public abstract Object construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        final Thread t = myThreadVar.get();
        if (t != null) {
            t.interrupt();
        }
        myThreadVar.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {
            final Thread t = myThreadVar.get();
            if (t == null) {
                return getValue();
            }
            try {
                t.join();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }

    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public SwingWorker() {
        final Runnable doFinished = new Runnable() {
            public void run() {
                finished();
            }
        };
        final Runnable doConstruct = new Runnable() {
            public void run() {
                try {
                    setValue(construct());
                } finally {
                    myThreadVar.clear();
                }
                SwingUtilities.invokeLater(doFinished);
            }
        };
        final Thread t = new Thread(doConstruct);
        myThreadVar = new ThreadVar(t);
    }

    /**
     * Start the worker thread.
     */
    public void start() {
        final Thread t = myThreadVar.get();
        if (t != null) {
            t.start();
        }
    }
}