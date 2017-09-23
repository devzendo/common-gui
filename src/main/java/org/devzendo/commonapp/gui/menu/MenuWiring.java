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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;


/**
 * Provides wiring between MenuItems (referenced via MenuIdentifiers) and their
 * ActionListeners.
 * 
 * @author matt
 *
 */
public final class MenuWiring {
    private static final Logger LOGGER = Logger.getLogger(MenuWiring.class);
    /**
     * A stash for JMenuItems and their ActionListeners
     * 
     * @author matt
     *
     */
    private class MenuDetails {
        private JMenuItem mMenuItem;
        private ActionListener mActionListener;

        /**
         * Create a MenuDetails with no JMenuItem until set, and no
         * ActionListener until one is set.
         * 
         */
        public MenuDetails() {
            this.mMenuItem = null;
            this.mActionListener = null;
        }
        
        /**
         * Create a MenuDetails for a given JMenuItem, with no ActionListener
         * until one is set.
         * 
         * @param jMenuItem the JMenuItem for this stash 
         */
        public MenuDetails(final JMenuItem menuItem) {
            this.mMenuItem = menuItem;
            this.mActionListener = null;
        }

        /**
         * @return the actionListener
         */
        public ActionListener getActionListener() {
            return mActionListener;
        }

        /**
         * @param actionListener the actionListener to set
         */
        public void setActionListener(final ActionListener actionListener) {
            this.mActionListener = actionListener;
        }

        /**
         * @param menuItem the JMenuItem to set
         */
        public void setMenuItem(final JMenuItem menuItem) {
            this.mMenuItem = menuItem;
        }
        
        /**
         * @return the menuItem
         */
        public JMenuItem getMenuItem() {
            return mMenuItem;
        }
    }
    private final Map<MenuIdentifier, MenuDetails> menuDetailsMap;

    /**
     * Create some MenuWiring
     */
    public MenuWiring() {
        menuDetailsMap = new HashMap<MenuIdentifier, MenuDetails>();
    }

    /**
     * Obtain the ActionListener for a given MenuIdentifier.
     * 
     * @param menuIdentifier the MenuIdentifier to search for an ActionListener
     * @return the ActionListener, if one has been set, or null if not set.
     */
    public ActionListener getActionListener(final MenuIdentifier menuIdentifier) {
        synchronized (menuDetailsMap) {
            final MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            if (menuDetails == null) {
                return null;
            }
            return menuDetails.getActionListener();
        }
    }

    /**
     * Obtain the JMenuItem for a given MenuIdentifier
     * @param menuIdentifier the MenuIdentifier to search for a JMenuItem
     * @return the JMenuItem, if one has been set for this MenuIdentifier, or
     * null if not set.
     */
    public JMenuItem getMenuItem(final MenuIdentifier menuIdentifier) {
        synchronized (menuDetailsMap) {
            final MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            if (menuDetails == null) {
                return null;
            }
            return menuDetails.getMenuItem();
        }
    }
    
    /**
     * Generate a direct ActionListener for a given MenuIdentifier which will,
     * at runtime, lookup the indirect ActionListener for the MenuIdentifier,
     * and if set, call it.
     *  
     * @param menuIdentifier the MenuIdentifier to generate a direct
     * ActionListener for.
     * @return a direct ActionListener.
     */
    private ActionListener generateDirectActionListener(final MenuIdentifier menuIdentifier) {
        // adding a level of temporal indirection here so that listeners
        // can be wired up after the menu has been created
        return new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                synchronized (menuDetailsMap) {
                    final MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
                    if (menuDetails == null) {
                        return;
                    }
                    final ActionListener actionListener = menuDetails.getActionListener();
                    if (actionListener == null) {
                        LOGGER.debug("No indirect ActionListener stored for menu identifier " + menuIdentifier);
                    } else {
                        LOGGER.debug("Calling indirect ActionListener " + actionListener + " for menu identifier " + menuIdentifier);
                        actionListener.actionPerformed(e);
                    }
                }
            }
        };
    }
    
    /**
     * Create the mapping between a JMenuItem and its MenuIdentifier, and set
     * the JMenuItem's direct ActionListener.
     * 
     * @param menuIdentifier the MenuIdentifier to create a mapping for
     * @param menuItem the JMenuItem to map
     */
    public void storeMenuItem(final MenuIdentifier menuIdentifier, final JMenuItem menuItem) {
        synchronized (menuDetailsMap) {
            final MenuDetails existingMenuDetails = menuDetailsMap.get(menuIdentifier);
            if (existingMenuDetails == null) {
                menuDetailsMap.put(menuIdentifier, new MenuDetails(menuItem));
            } else {
                existingMenuDetails.setMenuItem(menuItem);
            }
            menuItem.addActionListener(generateDirectActionListener(menuIdentifier));
        }
    }
    
    /**
     * Store an indirect ActionListener for a MenuIdentifier. The JMenuItem would have
     * an initial direct ActionListener bound - see storeMenuItem. This sets the 
     * indirect ActionListener - the one one that will be triggered by the direct
     * ActionListener
     * @param menuIdentifier the MenuIdentifier to set the indirect ActionListener for
     * @param actionListener the indirect ActionListener.
     */
    public void setActionListener(final MenuIdentifier menuIdentifier, final ActionListener actionListener) {
        synchronized (menuDetailsMap) {
            final MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            if (menuDetails == null) {
                final MenuDetails newMenuDetails = new MenuDetails();
                newMenuDetails.setActionListener(actionListener);
                menuDetailsMap.put(menuIdentifier, newMenuDetails);
                LOGGER.warn("Setting ActionListener for menu identifier " + menuIdentifier + ": it currently has no associated menu item");
            } else {
                LOGGER.debug("Storing indirect ActionListener " + actionListener + " for menu identifier " + menuIdentifier);
                menuDetails.setActionListener(actionListener);
            }
        }
    }

    /**
     * Trigger the actioning of an action listener given its
     * menu identifier.
     *
     * The ActionEvent's source will contain the MenuItem stored for this
     * MenuIdentifier, or, if no MenuItem has been stored, it will contain the
     * MenuIdentifier itself. This is used in the case where menu ActionListeners
     * are to be triggered by non-menu activities:
     *
     * (e.g. an adapter that translates
     * the Apple menu's Quit item (not a real MenuItem) into the triggering of
     * the File/Exit ActionListener - on the Mac, there is no File/Exit menu
     * item). 
     * @param menuIdentifier the identifier of the menu item whose
     * action listener is to be triggered.
     */
    public void triggerActionListener(final MenuIdentifier menuIdentifier) {
        synchronized (menuDetailsMap) {
            LOGGER.debug("Triggering action listener for " + menuIdentifier);
            final MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            if (menuDetails != null) {
                final JMenuItem menuItem = menuDetails.getMenuItem();
                if (menuItem == null) {
                    LOGGER.warn("There is no menu item stored for menu identifier " + menuIdentifier + ": passing to indirect action listener");
                    final ActionListener indirectActionListener = menuDetails.mActionListener;
                    if (indirectActionListener != null) {
                        indirectActionListener.actionPerformed(new ActionEvent(menuIdentifier, 0, ""));
                    } else {
                        LOGGER.warn("There is no indirect action listener for menu identifier " + menuIdentifier + ": cannot deliver event");
                    }
                } else {
                    injectActionEvent(menuIdentifier, new ActionEvent(menuItem, 0, ""));
                }
            } else {
                LOGGER.warn("Not triggering action listener since there are no menu details stored for " + menuIdentifier);
            }
        }
    }

    /**
     * Dispatching an event to direct listeners. Originally used
     * by unit tests, is also used to trigger menu events given
     * their MenuIdentifiers by other parts of the system.
     * @param menuIdentifier the MenuIdentifier to dispatch for
     * @param event the event to dispatch
     */
    void injectActionEvent(final MenuIdentifier menuIdentifier, final ActionEvent event) {
        synchronized (menuDetailsMap) {
            final MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            if (menuDetails != null && menuDetails.getMenuItem() != null) {
                for (final ActionListener actionListener : menuDetails.getMenuItem().getActionListeners()) {
                    actionListener.actionPerformed(event);
                }
            }
        }
    }

    /**
     * Is the menu item for this identifier enabled?
     * <p>
     * If no menu item has been stored for this menu identifier, an 
     * IllegalStateException will be thrown.
     * 
     * @param menuIdentifier the menu identifier to check for enabledness
     * @return true iff the menu item is enabled
     */
    public boolean isMenuItemEnabled(final MenuIdentifier menuIdentifier) {
        return getMenuItemThrowingIfNull(menuIdentifier).isEnabled();
    }

    private JMenuItem getMenuItemThrowingIfNull(final MenuIdentifier menuIdentifier) {
        final JMenuItem menuItem = getMenuItem(menuIdentifier);
        if (menuItem == null) {
            throw new IllegalStateException("No menu item stored against menu identifier " + menuIdentifier);
        }
        return menuItem;
    }

    /**
     * Disable a menu item identified by a menu identifier.
     * <p>
     * If no menu item has been stored for this menu identifier, an 
     * IllegalStateException will be thrown.
     * @param menuIdentifier the menu identifier to disable
     */
    public void disableMenuItem(final MenuIdentifier menuIdentifier) {
        setMenuItemEnabled(menuIdentifier, false);
    }

    /**
     * Enable a menu item identified by a menu identifier.
     * <p>
     * If no menu item has been stored for this menu identifier, an 
     * IllegalStateException will be thrown.
     * @param menuIdentifier the menu identifier to enable
     */
    public void enableMenuItem(final MenuIdentifier menuIdentifier) {
        setMenuItemEnabled(menuIdentifier, true);
    }

    /**
     * Enable or disable a menu item identified by a menu identifier.
     * <p>
     * If no menu item has been stored for this menu identifier, an 
     * IllegalStateException will be thrown.
     * @param menuIdentifier the menu identifier to enable
     * @param enable true to enable; false to disable
     */
    public void setMenuItemEnabled(final MenuIdentifier menuIdentifier, final boolean enable) {
        LOGGER.debug((enable ? "Enabling " : "Disabling ") + menuIdentifier);
        getMenuItemThrowingIfNull(menuIdentifier).setEnabled(enable);
    }

    /**
     * Create a JMenuItem for the given menu identifier, item text
     * and mnemonic and store it in the wiring map, or, if one
     * already exists for this identifier in the wiring map, return
     * that one (i.e., only create once and cache it).
     * 
     * @param menuIdentifier the MenuIdentifier
     * @param menuItemText the text for this menu item
     * @param mnemonic the mnemonic
     * @return the JMenuItem for this identifier
     */
    public JMenuItem createMenuItem(final MenuIdentifier menuIdentifier,
           final String menuItemText, final char mnemonic) {
        final JMenuItem existingMenuItem = getMenuItem(menuIdentifier);
        if (existingMenuItem != null) {
            return existingMenuItem;
        } else {
            final JMenuItem menuItem = new JMenuItem(menuItemText);
            menuItem.setMnemonic(mnemonic);
            storeMenuItem(menuIdentifier, menuItem);
            return menuItem;
        }
    }

    /**
     * Create a JMenuItem for the given menu identifier, item text
     * and mnemonic and store it in the wiring map, and, if one
     * already exists for this identifier in the wiring map, remove
     * it, and replace it with the new one.
     * 
     * @param menuIdentifier the MenuIdentifier
     * @param menuItemText the text for this menu item
     * @param mnemonic the mnemonic
     * @return the JMenuItem for this identifier
     */
    public JMenuItem replaceMenuItem(final MenuIdentifier menuIdentifier,
            final String menuItemText, final char mnemonic) {
        synchronized (menuDetailsMap) {
            menuDetailsMap.remove(menuIdentifier);
            final JMenuItem menuItem = new JMenuItem(menuItemText);
            menuItem.setMnemonic(mnemonic);
            storeMenuItem(menuIdentifier, menuItem);
            return menuItem;
        }            
    }
}
