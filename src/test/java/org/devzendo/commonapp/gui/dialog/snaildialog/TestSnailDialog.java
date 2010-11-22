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

package org.devzendo.commonapp.gui.dialog.snaildialog;

import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests the SnailDialog's threading and delayed initialisation mechanism.
 * 
 * @author matt
 */
public final class TestSnailDialog  {
    private static final Logger LOGGER = Logger
            .getLogger(TestSnailDialog.class);

    private volatile StubRecordingSnailDialog snailDialog;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * @throws InterruptedException
     *         on latch failure
     */
    @Test(timeout = 8000)
    @Ignore
    public void testIt() throws InterruptedException {
        LOGGER.debug("starting test");
        final CountDownLatch creationLatch = new CountDownLatch(1);
        LOGGER.debug("running creation on edt");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                LOGGER.debug("Creating main frame");
                final JFrame mainFrame = new JFrame("main frame");
                LOGGER.debug("Setting main frame visible");
                mainFrame.setVisible(true);
                LOGGER.debug("Creating dialog");
                snailDialog = new StubRecordingSnailDialog(mainFrame,
                        new CursorManager());
                snailDialog.postConstruct();
                snailDialog.pack();
                
                LOGGER.debug("Created dialog; counting down");
                creationLatch.countDown();
                LOGGER.debug("finished creation");
            }
        });
        LOGGER.debug("waiting for creation");
        creationLatch.await();
        LOGGER.debug("Created");
        Assert.assertFalse(snailDialog.isInitialised());
        Assert.assertFalse(snailDialog.isSwingWorkerConstructedOnNonEventThread());
        Assert.assertFalse(snailDialog.isFinishedOnEventThread());
        LOGGER.debug("making it visible");

        final CountDownLatch visibleLatch = new CountDownLatch(1);
        try {
            GUIUtils.runOnEventThread(new Runnable() {
                public void run() {
                    try {
                        LOGGER.debug("making visible on EDT");
                        snailDialog.setVisible(true); // hangs here
                        LOGGER.debug("counting down visibleLatch");
                        visibleLatch.countDown();
                        LOGGER.debug("Counted down visibleLatch");
                    } catch (final Throwable t) {
                        LOGGER.error("Caught unexpected "
                                + t.getClass().getSimpleName(), t);
                    }
                }
            });
            ThreadUtils.waitNoInterruption(250);
            LOGGER.debug("made it visible; waiting for visibleLatch");
            visibleLatch.await();
            LOGGER.debug("visible lLatch counted down");
            Assert.assertTrue(snailDialog.isInitialised());
            Assert.assertTrue(snailDialog.isSwingWorkerConstructedOnNonEventThread());
            Assert.assertTrue(snailDialog.isFinishedOnEventThread());
            LOGGER.debug("ending test in finally block");
        } finally {
            GUIUtils.runOnEventThread(new Runnable() {
                public void run() {
                    LOGGER.debug("clearing");
                    snailDialog.clearAndHide();
                    LOGGER.debug("cleared");
                }
            });
        }
    }
}
