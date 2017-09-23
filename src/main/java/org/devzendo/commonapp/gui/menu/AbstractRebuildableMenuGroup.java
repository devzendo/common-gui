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

package org.devzendo.commonapp.gui.menu;

/**
 * An AbstractRebuildableMenuGroup is a menu group that can be rebuilt. These
 * are used for menus that add/remove MenuItems, such as the View menu (when
 * certain tabs are enabled/disabled), the Window menu (when databases are
 * added/removed), and the File menu (when databases are opened, the Recent
 * files list needs updating).
 * 
 * @author matt
 */
public abstract class AbstractRebuildableMenuGroup extends AbstractMenuGroup {
    /**
     * Construct given the main menu, which subclasses will probably use to
     * cause updates to other parts of the menu system.
     * 
     * @param wiring the menu wiring
     */
    public AbstractRebuildableMenuGroup(final MenuWiring wiring) {
        super(wiring);
    }
    
    /**
     * Some event has happened, an update to the menu group's model, perhaps,
     * which means it has to be rebuilt.
     */
    public abstract void rebuildMenuGroup();
}
