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

import javax.swing.JMenu;



/**
 * An AbstractMenuGroup is the base class for encapsulations of menus - that is,
 * typically vertical menu strips that offer a specific set of related
 * functions, such as the File, View, Window menus. They could encapsulate
 * subsets of a vertical menu, but usually, it'll be the whole menu.
 *
 * @author matt
 *
 */
public abstract class AbstractMenuGroup {
    private final MenuWiring menuWiring;

    /**
     * Construct given the main menu, which subclasses will probably use to
     * cause updates to other parts of the menu system.
     * @param wiring the menu wiring
     */
    public AbstractMenuGroup(final MenuWiring wiring) {
        this.menuWiring = wiring;
    }

    /**
     * Get the menu wiring
     * @return the menu wiring
     */
    protected final MenuWiring getMenuWiring() {
        return menuWiring;
    }

    /**
     * If this menu group represents a full menu, it will be returned here.
     * @return the JMenu, or null, if this is not a full menu.
     */
    public abstract JMenu getJMenu();

    /**
     * Create a new JMenuItem, and wire it into the MenuWiring.
     * Called on the EDT
     * @param menuIdentifier the MenuIdentifier
     * @param menuItemText the text for this menu item
     * @param mnemonic the mnemonic
     * @param menu the menu to add it to.
     */
    public final void createMenuItem(final MenuIdentifier menuIdentifier,
            final String menuItemText, final char mnemonic, final JMenu menu) {

        menu.add(
            menuWiring.createMenuItem(menuIdentifier, menuItemText, mnemonic));
    }

    /**
     * Replace a new JMenuItem, and wire it into the MenuWiring.
     * Called on the EDT
     * @param menuIdentifier the MenuIdentifier
     * @param menuItemText the text for this menu item
     * @param mnemonic the mnemonic
     * @param menu the menu to add it to.
     */
    public final void replaceMenuItem(final MenuIdentifier menuIdentifier,
            final String menuItemText, final char mnemonic, final JMenu menu) {

        menu.add(
            menuWiring.replaceMenuItem(menuIdentifier, menuItemText, mnemonic));
    }

}
