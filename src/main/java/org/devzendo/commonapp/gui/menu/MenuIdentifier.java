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
package org.devzendo.commonapp.gui.menu;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * How a menu item is identified.
 * 
 * @author matt
 *
 */
public final class MenuIdentifier {
    private final String mName;
    /**
     * @param name the name of the menu identifier
     */
    public MenuIdentifier(final String name) {
        mName = name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MenuIdentifier other = (MenuIdentifier) obj;
        return new EqualsBuilder().append(this.mName, other.mName).isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
        .append(mName).toHashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return mName;
    }
}