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

import javax.swing.JTextPane;

/**
 * A JPanel that hosts a JTextArea for rendering read-only HTML
 * content.
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class HTMLPanel extends JTextPane {

    /**
     * Create a panel with no text. Can always call setHTMLText later.
     */
    public HTMLPanel() {
        super();
        initialise("");
    }

    /**
     * Create a panel with initial text.
     * @param html the HTML to render
     */
    public HTMLPanel(final String html) {
        super();
        initialise(html);
    }

    private void initialise(final String html) {
        setContentType("text/html");
        setHTMLText(html);
        setEditable(false);
    }
    
    /**
     * Set the HTML that this panel is to render.
     * @param html the HTML
     */
    public void setHTMLText(final String html) {
        setText(html);
        setCaretPosition(0);
    }
}
