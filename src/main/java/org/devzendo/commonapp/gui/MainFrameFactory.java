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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for obtaining the singleton MainFrame - the application's main
 * window.
 * 
 * @author matt
 *
 */
public final class MainFrameFactory implements FactoryBean<Frame> {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameFactory.class);
    private Frame factoryMainFrame;
    
    /**
     * {@inheritDoc}
     */
    public Frame getObject() throws Exception {
        LOGGER.debug("MainFrameFactory returning main frame as Frame object");
        return factoryMainFrame;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return Frame.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Factory population method
     * @param frame the Frame to return as a Singleton
     */
    public void setMainFrame(final Frame frame) {
        LOGGER.debug("MainFrameFactory being populated with Frame object");
        factoryMainFrame = frame;
    }
}
