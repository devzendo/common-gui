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

import java.awt.Frame;
import java.io.IOException;

import org.devzendo.commonapp.gui.MainFrameFactory;
import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.commonapp.spring.springloader.SpringLoaderUnittestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the MainFrame FactoryBean
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/commongui/MainFrameFactoryTestCase.xml")
public final class TestMainFrameFactory extends SpringLoaderUnittestCase {

    /**
     *
     */
    @Test
    public void testStoreMainFrame() {
        final Frame testFrame = new Frame();

        final MainFrameFactory mainFrameFactory = getMainFrameFactory();
        Assert.assertNotNull(mainFrameFactory);
        mainFrameFactory.setMainFrame(testFrame);

        final Frame mainFrame = getMainFrame();
        Assert.assertNotNull(mainFrame);

        Assert.assertEquals(testFrame, mainFrame);
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void itsASingleton() throws IOException {
        final Frame testFrame = new Frame();
        getMainFrameFactory().setMainFrame(testFrame);

        final Frame frame1 = getMainFrame();
        final Frame frame2 = getMainFrame();
        Assert.assertSame(frame1, frame2);
    }

    private Frame getMainFrame() {
        return getSpringLoader().getBean("mainFrame", Frame.class);
    }

    private MainFrameFactory getMainFrameFactory() {
        return getSpringLoader().getBean("&mainFrame", MainFrameFactory.class);
    }
}
