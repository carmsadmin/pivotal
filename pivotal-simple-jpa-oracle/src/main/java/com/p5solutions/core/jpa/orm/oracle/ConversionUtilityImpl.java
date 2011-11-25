/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  KASRA RASAEE
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.p5solutions.core.jpa.orm.oracle;

import java.sql.SQLException;

import oracle.sql.TIMESTAMP;

import com.p5solutions.core.jpa.orm.ConversionUtility;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.exceptions.TypeConversionException;

/**
 * The Class ConversionUtilityImpl for Oracle
 * 
 * @author Kasra Rasaee
 * @since 2011-11-22
 * 
 * 
 */
public class ConversionUtilityImpl extends com.p5solutions.core.jpa.orm.ConversionUtilityImpl 
	implements ConversionUtility {

  // TODO this needs to be checked without an actual reference to
  // oracle.sql.TIMESTMAP, as it may not be part of the
  // class path
  protected boolean isOracleTimeStamp(Class<?> type) {
    if (oracle.sql.TIMESTAMP.class.isAssignableFrom(type)) {
      return true;
    }
    return false;
  }

  @Override
  public Object convertToSqlType(ParameterBinder pb, Object value) {
   
    // TODO should handle blob or any other conversion here.
    
    return super.convertToSqlType(pb, value);
  }

  @Override
  protected Object convertSimpleValue(ParameterBinder pb, Object value, String bindingPath, Class<?> sourceType, Class<?> targetType)
      throws TypeConversionException {
    if (isOracleTimeStamp(sourceType)) {
    	
      // TODO needs adjustment refactoring?
      TIMESTAMP tz = (TIMESTAMP) value;
      try {
        return tz.timestampValue();
      } catch (SQLException e) {
        throw new RuntimeException("Unable to convert oracle timestamp on parameter binder " + pb.getBindingPath() + " for entity type "
            + pb.getEntityClass());
      }
    } 
    
    //otherwise
    return super.convertSimpleValue(pb, value, bindingPath, sourceType, targetType);
  }
}
