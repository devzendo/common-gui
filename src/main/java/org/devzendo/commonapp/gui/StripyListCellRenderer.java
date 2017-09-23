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

package org.devzendo.commonapp.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;



/**
 * A ListCellRenderer that renders a subtle stripe effect.
 * @author matt
 *
 */
public final class StripyListCellRenderer extends DefaultListCellRenderer implements ListCellRenderer<Object>  {
    private static final long serialVersionUID = -471403224429769232L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getListCellRendererComponent(
            final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {
        final Component ret = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ((index & 0x01) == 0x01) {
            setBackground(GUIUtils.slightlyDarkerColor(getBackground()));
        }
        return ret;
    }
}
