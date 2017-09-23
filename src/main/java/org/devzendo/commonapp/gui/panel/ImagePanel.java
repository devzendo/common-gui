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

package org.devzendo.commonapp.gui.panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * A JPanel that shows a background graphic.
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
    private final Image mImage;

    /**
     * Construct an ImagePanel from a file.
     * @param fileName the name of the graphic file.
     */
    public ImagePanel(final String fileName) {
        this(new ImageIcon(fileName).getImage());
    }

    /**
     * Construct an ImagePanel from a URL, possibly via the
     * ResourceLoader?
     * @param url the URL of a graphic file
     */
    public ImagePanel(final URL url) {
        this(new ImageIcon(url).getImage());
    }
    
    /**
     * Construct an ImagePanel given an Image
     * @param image the image to show in the background
     */
    public ImagePanel(final Image image) {
        this.mImage = image;
        final Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void paintComponent(final Graphics g) {
        g.drawImage(mImage, 0, 0, null);
    }
}
