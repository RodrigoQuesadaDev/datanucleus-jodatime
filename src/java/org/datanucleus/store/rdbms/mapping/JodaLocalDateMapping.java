/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.store.rdbms.mapping;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.store.rdbms.mapping.java.TemporalMapping;
import org.datanucleus.store.types.converters.TypeConverter;
import org.joda.time.LocalDate;

/**
 * SCO Mapping for org.joda.time.LocalDate type.
 * Can be persisted using either
 * <ul>
 * <li>Single column using a String mapping.</li>
 * <li>Single column using DATE mapping.</li>
 * </ul>
 * See http://joda-time.sourceforge.net/
 */
public class JodaLocalDateMapping extends TemporalMapping
{
    public Class getJavaType()
    {
        return LocalDate.class;
    }

    /**
     * Accessor for the name of the java-type actually used when mapping the particular datastore
     * field. This java-type must have an entry in the datastore mappings.
     * @param index requested datastore field index.
     * @return the name of java-type for the requested datastore field.
     */
    public String getJavaTypeForDatastoreMapping(int index)
    {
        if (datastoreMappings == null || datastoreMappings.length == 0)
        {
            // Not got mappings yet so use column metadata to define
            ColumnMetaData[] colmds = getColumnMetaDataForMember(mmd, roleForMember);
            boolean useString = false;
            if (colmds != null && colmds.length > 0 && colmds[0].getJdbcType() != null)
            {
                if (MetaDataUtils.isJdbcTypeString(colmds[0].getJdbcType()))
                {
                    useString = true;
                }
            }
            return (useString ? ClassNameConstants.JAVA_LANG_STRING : ClassNameConstants.JAVA_SQL_DATE);
        }
        else if (datastoreMappings != null && datastoreMappings.length > 0 && datastoreMappings[0].isStringBased())
        {
            // Use String as our java type
            return ClassNameConstants.JAVA_LANG_STRING;
        }
        else
        {
            return ClassNameConstants.JAVA_SQL_DATE;
        }
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.TemporalMapping#getDefaultLengthAsString()
     */
    protected int getDefaultLengthAsString()
    {
        // Persist as "yyyy-MM-dd" when stored as string
        return 10;
    }

    public void setObject(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, Object value)
    {
        if (value == null)
        {
            getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
        }
        else if (datastoreMappings != null && datastoreMappings.length > 0 && datastoreMappings[0].isStringBased())
        {
            TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalDate.class, String.class);
            if (conv != null)
            {
                Object obj = conv.toDatastoreType(value);
                getDatastoreMapping(0).setObject(ps, exprIndex[0], obj);
            }
            else
            {
                throw new NucleusUserException("This type doesn't support persistence as a String");
            }
        }
        else
        {
            getDatastoreMapping(0).setObject(ps, exprIndex[0],
                new Timestamp(((LocalDate)value).toDateTimeAtStartOfDay().getMillis()));
        }
    }

    public Object getObject(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return null;
        }

        Object datastoreValue = getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        if (datastoreValue == null)
        {
            return null;
        }

        if (datastoreValue instanceof String)
        {
            TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalDate.class, String.class);
            if (conv != null)
            {
                return conv.toMemberType((String)datastoreValue);
            }
            else
            {
                throw new NucleusUserException("This type doesn't support persistence as a String");
            }
        }
        else if (datastoreValue instanceof Date)
        {
            return new LocalDate(((Date)datastoreValue).getTime());
        }
        else if (datastoreValue instanceof Timestamp)
        {
            return new LocalDate(((Timestamp)datastoreValue).getTime());
        }
        else
        {
            return null;
        }
    }
}