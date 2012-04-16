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

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.transaction.InvalidTransactionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.aop.Targetable;
import com.p5solutions.core.jpa.orm.exceptions.TypeConversionException;
import com.p5solutions.core.utils.NumberUtils;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class ConversionUtilityImpl.
 */
public class ConversionUtilityImpl implements ConversionUtility {

  /** The entity utility. */
  private EntityUtility entityUtility;

  /** The LOGGER. */
  protected static Log logger = LogFactory.getLog(ConversionUtility.class);

  /**
   * Convert number.
   * 
   * @param pb
   *          the pb
   * @param value
   *          the value
   * @param targetType
   *          the target type
   * @return the object
   * @throws TypeConversionException
   *           the type conversion exception
   * @see com.p5solutions.core.jpa.orm.ConversionUtility#convertNumber(java.lang.Number, java.lang.Class)
   */
  @Override
  public Object convertNumber(ParameterBinder pb, Number value, Class<?> targetType) throws TypeConversionException {

    if (ReflectionUtility.isBooleanClass(targetType)) {
      if (value.intValue() == 1) {
        return Boolean.TRUE;
      } else if (value.intValue() == 0) {
        return Boolean.FALSE;
      }

      throw new TypeConversionException("Unable to convert value of " + value + " to Boolean type!");

    } else if (ReflectionUtility.isShortClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, Short.class);
    } else if (ReflectionUtility.isIntegerClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, Integer.class);
    } else if (ReflectionUtility.isLongClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, Long.class);
    } else if (ReflectionUtility.isFloatClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, Float.class);
    } else if (ReflectionUtility.isDoubleClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, Double.class);
    } else if (ReflectionUtility.isBigDecimalClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, BigDecimal.class);
    } else if (ReflectionUtility.isBigIntegerClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, BigInteger.class);
    } else if (ReflectionUtility.isByteClass(targetType)) {
      return NumberUtils.convertNumberToTargetClass(value, Byte.class);
    }

    return value.intValue();
  }

  /**
   * Convert timestamp.
   * 
   * @param pb
   *          the pb
   * @param timestamp
   *          the timestamp
   * @param targetType
   *          the target type
   * @return the object
   */
  public Object convertTimestamp(ParameterBinder pb, Timestamp timestamp, Class<?> targetType) {
    // TODO probably needs further checking based on JPA annotations, some
    // timezone issues???
    if (ReflectionUtility.isDate(targetType)) {
      // Check for Temporal
      if (pb != null) {
        Temporal temporal = pb.getTemporal();
        if (temporal != null) {
          Date converted = timestamp;
          if (TemporalType.DATE.equals(temporal.value())) {
            java.sql.Date dt = new java.sql.Date(timestamp.getTime());
            return dt;
          } else if (TemporalType.TIME.equals(temporal.value())) {
            java.sql.Time tm = new java.sql.Time(timestamp.getTime());
            return tm;
          } else if (TemporalType.TIMESTAMP.equals(temporal.value())) {

          }

          return converted;
        }
      }

      Date test = new Date(timestamp.getTime());
      return test;

      // return (Date) timestamp;
    } else if (ReflectionUtility.isStringClass(targetType)) {
      // TODO needs to be formatted based on the Format defined by the
      // 'custom?'
      // Format annotation ????
      return timestamp.toLocaleString();
    } else if (ReflectionUtility.isLongClass(targetType)) {

    }
    return timestamp;
  }

  /**
   * Convert blob.
   * 
   * @param blob
   *          the blob
   * @param targetType
   *          the target type
   * @return the object
   */
  public Object convertBLOB(Blob blob, Class<?> targetType) {
    if (ReflectionUtility.isStringClass(targetType)) {
      // Reader reader = clob.getCharacterStream();
      // TODO charset needs to be passed in from the resultset ??
      // CharBuffer cb = new CharBuffer(CharacterSet.AL32UTF8_CHARSET);
      // java.nio.CharBuffer cb = new java.nio.CharBuffer();

      // reader.read(target);
    } else if (ReflectionUtility.isByteArray(targetType)) {

    }
    return blob;
  }

  /**
   * Convert clob.
   * 
   * @param clob
   *          the clob
   * @param targetType
   *          the target type
   * @return the object
   */
  public Object convertCLOB(Clob clob, Class<?> targetType) {
    if (ReflectionUtility.isStringClass(targetType)) {
      // TODO charset needs to be passed in from the resultset, or defined
      // as
      // part of the transaction template???
      try {
        // TODO THIS NEEDS TO BE APPENDED IN UTF-8 ??? based on the
        // character
        // setting of the database???
        Reader reader = clob.getCharacterStream();
        StringBuilder output = new StringBuilder();
        int r = -1;
        while ((r = reader.read()) >= 0) {
          output.append((char) r);
        }

        return output.toString();
      } catch (SQLException e) {
        // TODO log it
        throw new RuntimeException(e);
      } catch (IOException e) {
        // TODO log it
        throw new RuntimeException(e);
      }
    } else if (ReflectionUtility.isByteArray(targetType)) {

    }
    return clob;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.core.jpa.orm.ConversionUtility#convert(com.p5solutions. core.jpa.orm.ParameterBinder ,
   * java.lang.Object, java.lang.Class)
   */
  @Override
  public Object convert(ParameterBinder pb, Object value, Class<?> targetType) throws TypeConversionException {
    return convert(pb, value, null, targetType);
  }

  /**
   * Convert simple value.
   * 
   * @param pb
   *          the pb
   * @param value
   *          the value
   * @param bindingPath
   *          the binding path
   * @param sourceType
   *          the source type
   * @param targetType
   *          the target type
   * @return the object
   * @throws TypeConversionException
   *           the type conversion exception
   */
  protected Object convertSimpleValue(ParameterBinder pb, Object value, String bindingPath, Class<?> sourceType,
      Class<?> targetType) throws TypeConversionException {

    if (ReflectionUtility.isClob(sourceType)) {
      value = convertCLOB((Clob) value, targetType);
    } else if (ReflectionUtility.isBlob(sourceType)) {
      value = convertBLOB((Blob) value, targetType);
    } else if (ReflectionUtility.isTimestamp(sourceType)) {
      value = convertTimestamp(pb, (Timestamp) value, targetType);
    } else if (ReflectionUtility.isNumberClass(sourceType)) {
      return convertNumber(pb, (Number) value, targetType);
    } else if (ReflectionUtility.isSerializableClass(sourceType) && ReflectionUtility.isSerializableClass(targetType)) {
      return value;
    } else {
      // TODO needs to have special exception thrown??
      String msg = "Cannot convert from class type " + sourceType + " to target type " + targetType;
      if (bindingPath != null) {
        msg += " when using binding path " + bindingPath;
      }
      throw new TypeConversionException(msg);
    }
    return value;
  }

  /**
   * Convert complex value.
   * 
   * @param pb
   *          the pb
   * @param value
   *          the value
   * @param sourceType
   *          the source type
   * @param targetType
   *          the target type
   * @return the object
   * @throws TypeConversionException
   *           the type conversion exception
   */
  protected Object convertComplexValue(ParameterBinder pb, Object value, Class<?> sourceType, Class<?> targetType)
      throws TypeConversionException {
    EntityDetail<?> entityDetail = getEntityUtility().getEntityDetail(sourceType);
    List<ParameterBinder> pkpbs = entityDetail.getPrimaryKeyParameterBinders();
    if (pkpbs == null) {
      throw new NullPointerException("No primary key columns defined for table-entity of type " + sourceType);
    }

    if (pkpbs.size() > 1) {
      throw new RuntimeException(new InvalidTransactionException("Only surogate keys "
          + "supported when arguments by reference, and not value. " + "Entity type " + sourceType));
    }

    ParameterBinder pkpb = pkpbs.get(0);

    // IDEALLY WE WANT TO DO THIS BY SOME SORT OF BINDING PATH.
    // ParameterBinder pb =
    // entityDetail.getParameterBinderByBindingPath(bindingPath);
    //
    if (pkpb != null) {
      Object newValue = ReflectionUtility.getValue(pkpb.getGetterMethod(), value);
      value = newValue;
    }
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.core.jpa.orm.ConversionUtility#convert(com.p5solutions. core.jpa.orm.ParameterBinder ,
   * java.lang.Object, java.lang.String, java.lang.Class)
   */
  @Override
  public Object convert(ParameterBinder pb, Object value, String bindingPath, Class<?> targetType)
      throws TypeConversionException {
    // null values are null values
    if (value == null) {
      return value;
    }
    // in case proxied object was passed in get the target class (e.g. proxied eagerly loaded type code object)
    if (value instanceof Targetable) {
      // the persistence layer is the simple-jpa implementation
      value = ((Targetable) value).getTarget();
    }
    // get the source type and do the conversion
    Class<?> sourceType = value.getClass();
    if (!isSameClass(value, targetType)) {
      value = convertSimpleValue(pb, value, bindingPath, sourceType, targetType);
    } else if (!ReflectionUtility.isBasicClass(sourceType)) {
      value = convertComplexValue(pb, value, sourceType, targetType);
    }
    // return converted value
    return value;
  }

  /**
   * Get the sql-type for this column, usually generated by the {@link EntityUtility#buildColumnMetaDataAll()}
   * 
   * @param pb
   *          the {@link ParameterBinder} used to extract the sql-type
   * @return sql-type integer usually defined by {@link java.sql.Types}
   */
  public int getSqlType(ParameterBinder pb) {
    return pb.getColumnMetaData().getColumnType();
  }

  @Override
  public Object convertToSqlType(ParameterBinder pb, Object value) {
    // TODO basic conversion, for example, to_char string to timestamp, number
    // to string, or string to date, or whatever.
    return value;
  }

  /**
   * Checks if is same class.
   * 
   * @param value
   *          the value
   * @param targetType
   *          the target type
   * @return true, if is same class
   * @see com.p5solutions.core.jpa.orm.ConversionUtility#isSameClass(java.lang.Object, java.lang.Class)
   */
  @Override
  public boolean isSameClass(Object value, Class<?> targetType) {
    if (value != null) {
      return value.getClass().equals(targetType);
    }
    return false;
  }

  /**
   * Gets the entity utility.
   * 
   * @return the entity utility
   */
  public EntityUtility getEntityUtility() {
    return entityUtility;
  }

  /**
   * Sets the entity utility.
   * 
   * @param entityUtility
   *          the new entity utility
   */
  public void setEntityUtility(EntityUtility entityUtility) {
    this.entityUtility = entityUtility;
  }
}
