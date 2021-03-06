/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
   ...
**********************************************************************/
package org.datanucleus.store.types.jodatime.converters;

import org.datanucleus.store.types.converters.TypeConverter;
import org.joda.time.Interval;

/**
 * TypeConverter from Joda Interval to String.
 */
public class JodaIntervalStringConverter implements TypeConverter<Interval, String>
{
    private static final long serialVersionUID = -6236589041948410550L;

    /* (non-Javadoc)
     * @see org.datanucleus.store.types.converters.TypeConverter#toDatastoreType(java.lang.Object)
     */
    public String toDatastoreType(Interval itv)
    {
        return (itv != null ? itv.toString() : null);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.types.converters.TypeConverter#toMemberType(java.lang.Object)
     */
    public Interval toMemberType(String str)
    {
        if (str == null)
        {
            return null;
        }

        return new Interval(str);
    }
}