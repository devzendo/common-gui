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
package org.devzendo.commonapp.prefs;

import javax.swing.JOptionPane;

import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.string.StringUtils;

/**
 * Warns the user of prefs dir creation failure via a dialog.
 * 
 * @author matt
 *
 */
public final class GuiPrefsStartupHelper extends AbstractPrefsStartupHelper {
    /**
     * @param prefsLocation the location of the prefs
     * @param prefsFactory the factory in which to store it.
     * @param prefsInstantiator the instantiator of prefs
     */
    public GuiPrefsStartupHelper(
            final PrefsLocation prefsLocation,
            final PrefsFactory prefsFactory,
            final PrefsInstantiator prefsInstantiator) {
        super(prefsLocation, prefsFactory, prefsInstantiator);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void warnUserOfPrefsDirCreationFailure() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                showPrefsDirCreationFailureMessage();
            }
        });
    }
    
    private void showPrefsDirCreationFailureMessage() {
        final String errorMessage = StringUtils.join(createErrorMessage(), "");
        JOptionPane.showMessageDialog(null, 
            // NOTE user-centric message
            // I18N
            errorMessage,
            "Could not create settings folder",
            JOptionPane.ERROR_MESSAGE);
        
        System.exit(0);
    }
}
