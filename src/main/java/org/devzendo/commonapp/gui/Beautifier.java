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

import java.util.HashSet;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.os.OSTypeDetect;
import org.devzendo.commoncode.os.OSTypeDetect.OSType;

import ch.randelshofer.quaqua.QuaquaManager;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

/**
 * Encapsulates the setting of the look and feel.
 * 
 * @author matt
 * 
 */
public final class Beautifier {
    private static final Logger LOGGER = Logger.getLogger(Beautifier.class);

    private Beautifier() {
        // nop
    }

    /**
     * Make the UI more beautiful. Unless we're on a Mac, in which case we're
     * already beautiful.
     * 
     */
    public static void makeBeautiful() {
        if (OSTypeDetect.getInstance().getOSType() == OSType.MacOSX) {
            LOGGER.info("Using Quaqua look and feel");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            
            // set system properties here that affect Quaqua
            // for example the default layout policy for tabbed
            // panes:
            System.setProperty(
               "Quaqua.tabLayoutPolicy", "wrap"
            );

            // Quaqua doesn't seem to render JButtons with their small form
            // correctly, via button.putClientProperty("JComponent.sizeVariant",
            // "small");
            // so knock it out for now.
            final Set<String> excludes = new HashSet<String>();
            excludes.add("Button");
            QuaquaManager.setExcludedUIs(excludes);

            // set the Quaqua Look and Feel in the UIManager
            try {
                 UIManager.setLookAndFeel(
                     "ch.randelshofer.quaqua.QuaquaLookAndFeel"
                 );
            // set UI manager properties here that affect Quaqua
            } catch (final Exception e) {
                LOGGER.warn("Could not set Quaqua look and feel:" + e.getMessage());
            }
        } else {
            LOGGER.info("Using Plastic XP look and feel");
            try {
                UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            } catch (final UnsupportedLookAndFeelException e) {
                LOGGER.warn("Plastic XP look and feel is not supported: " + e.getMessage());
            }
        }
    }
}
