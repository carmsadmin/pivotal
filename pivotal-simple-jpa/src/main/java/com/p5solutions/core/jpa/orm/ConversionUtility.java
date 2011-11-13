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
package com.p5solutions.core.jpa.orm;

import com.p5solutions.core.jpa.orm.exceptions.TypeConversionException;

/**
 * The Interface ConversionUtility: Interface defining the structure for a
 * conversion utility, which should be able to convert between various types of
 * Sql and Java types.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-05
 * 
 * @see ConversionUtilityImpl
 * @see MapUtility
 * @see Query#newSQLParameterSource(AbstractSQL, ConversionUtility)
 * 
 */
public interface ConversionUtility {

  /**
   * Convert a number to a given target type. Supports various target types, All
   * sub-types of {@link Number} including {@link Boolean}, and {@link String}
   * 
   * @param pb
   *          (optional) ParameterBinder used as part of the Query or DML
   *          statement
   * 
   * @param value
   *          the value to convert to the given target type
   * @param targetType
   *          the target type to convert the value to.
   * @return the converted value, if converted.
   * @throws TypeConversionException
   *           the type conversion exception
   */
  Object convertNumber(ParameterBinder pb, Number value, Class<?> targetType) throws TypeConversionException;

  /**
   * Convert a value to a given target type. Supports various target types.
   * 
   * @param pb
   *          (optional) ParameterBinder used as part of the Query or DML
   *          statement
   * 
   * @param value
   *          the value to convert to the given target type
   * @param targetType
   *          the target type to convert the value to.
   * @return the converted value, if converted.
   * @throws TypeConversionException
   *           the type conversion exception
   */
  Object convert(ParameterBinder pb, Object value, Class<?> targetType) throws TypeConversionException;

  /**
   * Convert a value to a given target type. Supports various target types.
   * 
   * @param pb
   *          (optional) ParameterBinder used as part of the Query or DML
   *          statement
   * 
   * @param value
   *          the value to convert to the given target type
   * @param bindingPath
   *          the binding path of the sql paramater
   * @param targetType
   *          the target type to convert the value to.
   * @return the converted value, if converted.
   * @throws TypeConversionException
   *           the type conversion exception
   */
  Object convert(ParameterBinder pb, Object value, String bindingPath, Class<?> targetType) throws TypeConversionException;

  /**
   * Checks if a given value is of the same type of class.
   * 
   * @param value
   *          the value
   * @param targetType
   *          the target type
   * @return true, if is same class
   */
  boolean isSameClass(Object value, Class<?> targetType);
}