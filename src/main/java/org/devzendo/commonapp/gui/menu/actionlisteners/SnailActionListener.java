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

package org.devzendo.commonapp.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.GUIUtils;


/**
 * An ActionListener base class that can be attached to say a MenuItem, such
 * that when the user selects the menu item, the event thread isn't so tied up
 * with doing the menu item's code so that it can't redraw. The effect is that
 * the menu gets "stuck" on screen for a short while.
 * So, start an hourglass, and spawn a separate thread to let the EDT catch up.
 * 
 * @author matt
 *
 */
public abstract class SnailActionListener implements ActionListener {
    private final CursorManager cursorManager;

    /**
     * Create a SnailActionListener
     * @param cursor the cursor manager
     */
    public SnailActionListener(final CursorManager cursor) {
        this.cursorManager = cursor;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void actionPerformed(final ActionEvent e) {
        cursorManager.hourglass(this.getClass().getSimpleName());
        new Thread(new Runnable() {
            public void run() {
                GUIUtils.invokeLaterOnEventThread(new Runnable() {
                    public void run() {
                        actionPerformedSlowly(e);
                        actionFinished();
                    }
                });
            }
        }).start();
    }
    
    /**
     * Perform the action.
     * @param e the ActionEvent to process.
     */
    public abstract void actionPerformedSlowly(final ActionEvent e);

    private void actionFinished() {
        cursorManager.normal(this.getClass().getSimpleName());
    }
    
    /**
     * @return the cursor manager
     */
    protected final CursorManager getCursorManager() {
        return cursorManager;
    }
}
