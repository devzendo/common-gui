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

import org.apache.log4j.Logger;
import org.devzendo.commoncode.os.OSTypeDetect;
import org.devzendo.commoncode.os.OSTypeDetect.OSType;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

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
        final OSType osType = OSTypeDetect.getInstance().getOSType();
        LOGGER.info("OS type detected as " + osType);
        if (osType == OSType.MacOSX) {
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
            try {
                // call QuaquaManager.setExcludedUIs(excludes) reflectively
                // so we don't call quaqua on non-Mac systems
                final Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
                final Method setExcludedUIsMethod = quaquaManagerClass.getMethod("setExcludedUIs", java.util.Set.class);
                setExcludedUIsMethod.invoke(quaquaManagerClass, excludes);
            } catch (final Exception e) {
                LOGGER.warn("Could not set Quaqua exclusions:" + e.getMessage(), e);
            }

            // set the Quaqua Look and Feel in the UIManager
            try {
                UIManager.setLookAndFeel(
                    "ch.randelshofer.quaqua.QuaquaLookAndFeel"
                );
                // set UI manager properties here that affect Quaqua
            } catch (final Exception e) {
                LOGGER.warn("Could not set Quaqua look and feel:" + e.getMessage(), e);
            }
        } else {
            LOGGER.info("Using Plastic XP look and feel");
            // Do this reflectively so we don't reference jgoodies looks on
            // non-Win/Linux (i.e. Mac) systems.
            try {
                final Class plasticXPLookAndFeelClass = Class.forName("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                final LookAndFeel plasticXPLookAndFeel = (LookAndFeel) plasticXPLookAndFeelClass.newInstance();
                UIManager.setLookAndFeel(plasticXPLookAndFeel);
            } catch (final UnsupportedLookAndFeelException e) {
                LOGGER.warn("Plastic XP look and feel is not supported: " + e.getMessage(), e);
            } catch (final Exception e) {
                LOGGER.warn("Could not set Plastic XP look and feel:" + e.getMessage(), e);
            }
        }
    }
}
