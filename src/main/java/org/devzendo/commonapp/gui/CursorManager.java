/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org http://devzendo.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.devzendo.commonapp.gui;

import javax.swing.JFrame;

/**
 * Allows applications to easily set/remove the hourglass cursor, and detect
 * when an app is 'stuck' with the hourglass.
 * 
 * @author matt
 *
 */
public interface CursorManager {
    /**
     * @return the application's main frame.
     */
    JFrame getMainFrame();

    /**
     * Set the application's main frame.
     * @param mainFrame the main application's frame
     */
    void setMainFrame(final JFrame mainFrame);

    /**
     * Shut down the stuck hourglass detection thread.
     */
    void shutdown();

    /**
     * Set the hourglass cursor, if a main component has been set.
     * @param caller the name of the caller, for stuck hourglass detection
     */
    void hourglass(final String caller);

    /**
     * Set the hourglass cursor, if a main component has been set. This
     * always runs on the event thread. If you're sure you're already on
     * the event thread, use hourglass().
     * @param caller the name of the caller, for stuck hourglass detection
     */
    void hourglassViaEventThread(final String caller);

    /**
     * Set the normal cursor, if the main component has been set. 
     * @param caller the name of the caller, for stuck hourglass detection
     */
    void normal(final String caller);

    /**
     * Set the normal cursor, if a main component has been set. This
     * always runs on the event thread. If you're sure you're already on
     * the event thread, use normal().
     * @param caller the name of the caller, for stuck hourglass detection
     */
    void normalViaEventThread(final String caller);
}