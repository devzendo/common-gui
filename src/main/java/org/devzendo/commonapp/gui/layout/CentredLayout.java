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

package org.devzendo.commonapp.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A simple layout manager that places its components in the
 * centre of the container. Ideally there would just be one
 * component added - they'll probably be overlaid in a mess if
 * you add more than one.
 * 
 * @author matt
 *
 */
public final class CentredLayout implements LayoutManager {
    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private boolean sizeUnknown = true;

    /**
     * 
     */
    public CentredLayout() {
    }

    /**
     * {@inheritDoc}
     */
    public void addLayoutComponent(final String name, final Component comp) {
    }

    /**
     * {@inheritDoc}
     */
    public void removeLayoutComponent(final Component comp) {
    }

    private void setSizes(final Container parent) {
        final int nComps = parent.getComponentCount();

        // Reset preferred/minimum width and height.
        preferredWidth = 0;
        preferredHeight = 0;
        minWidth = 0;
        minHeight = 0;

        for (int i = 0; i < nComps; i++) {
            final Component c = parent.getComponent(i);
            if (c.isVisible()) {
                final Dimension d = c.getPreferredSize();
                preferredWidth = Math.max(preferredWidth, d.width);
                preferredHeight = Math.max(preferredHeight, d.height);
                minWidth = Math.max(c.getMinimumSize().width,
                    minWidth);
                minHeight = Math.max(c.getMinimumSize().height,
                    minHeight);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public Dimension preferredLayoutSize(final Container parent) {
        setSizes(parent);
        sizeUnknown = false;
        // Always add the container's insets!
        final Insets insets = parent.getInsets();
        return new Dimension(
            preferredWidth + insets.left + insets.right, 
            preferredHeight + insets.top + insets.bottom);
    }

    /**
     * {@inheritDoc}
     */
    public Dimension minimumLayoutSize(final Container parent) {
        sizeUnknown = false;
        // Always add the container's insets!
        final Insets insets = parent.getInsets();
        return new Dimension(
            minWidth + insets.left + insets.right,
            minHeight + insets.top + insets.bottom);
    }

    /**
     * {@inheritDoc}
     * This is called when the panel is first displayed,
     * and every time its size changes.
     * Note: You CAN'T assume preferredLayoutSize or
     * minimumLayoutSize will be called -- in the case
     * of applets, at least, they probably won't be.
     */
    public void layoutContainer(final Container parent) {
        final Insets insets = parent.getInsets();
        final int nComps = parent.getComponentCount();

        // Go through the components' sizes, if neither
        // preferredLayoutSize nor minimumLayoutSize has
        // been called.
        if (sizeUnknown) {
            setSizes(parent);
        }

        for (int i = 0; i < nComps; i++) {
            final Component c = parent.getComponent(i);
            if (c.isVisible()) {
                final Dimension d = c.getPreferredSize();

                final int x = insets.left + (parent.getWidth() / 2) - (d.width / 2); 
                final int y = insets.top + (parent.getHeight() / 2) - (d.height / 2); 

                // Set the component's size and position.
                c.setBounds(x, y, d.width, d.height);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getName();
    }
}
