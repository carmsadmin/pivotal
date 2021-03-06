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

import java.lang.reflect.Method;
import java.sql.Blob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.jpa.orm.exceptions.TypeConversionException;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class MapUtility. This class will either map a value to a target object
 * defined by a specific path, or it will get a specific object instance from a
 * given target object by the defined path.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-10
 * 
 * @see ReflectionUtility
 */
public class MapUtilityImpl implements MapUtility {

  /** The conversion utility. */
  protected ConversionUtility conversionUtility;

  /** The logger. */
  private static Log logger = LogFactory.getLog(MapUtilityImpl.class);

  /**
   * @see com.p5solutions.core.jpa.orm.MapUtility#map(com.p5solutions.core.jpa.orm.ParameterBinder,
   *      java.lang.Object, java.lang.Object, java.lang.String)
   */
  @Override
  public Object map(ParameterBinder pb, Object target, Object value, String bindingPath) {
    Class<?> targetClazz = target.getClass();
    String[] p = bindingPath.split("\\.", 2);

    // get the getter and setter methods using the first part of the path, for example address.line1 : p[0] = address.
    Method[] methods = getMethodsGetterAndSetter(targetClazz, p[0]);
    if (methods != null) {
      
      Method get = methods[0];
      Method set = methods[1];

      // whats the return type of the getter method?
      Class<?> targetType = get.getReturnType();
// NOT, SINCE IT COULD BE RECURSIVE e.g. @Embedded      Class<?> targetType = pb.getTargetValueType();

      // if the length of p is equal == to 1, then we've hit the end
      // of the path
      if (p.length == 1) {
        // convert the parameter type
        try {
          value = convertValueForEntityFromSQL(pb, value, bindingPath, targetType);
        } catch (TypeConversionException e) {
          throw new RuntimeException("Unable map data due to conversion problem, please check target class " + targetClazz
              + " for the problem when binding path " + bindingPath, e);
        }

        // only map the object to the target entity property if the 
        // value is assignable to the target type, otherwise reject it.    
        if (value != null && !targetType.isAssignableFrom(value.getClass())) {
          // value is not assignable to target property.
          logger.error("The value [" + value + "] of type [" + value.getClass() + "] is not assignable to target type [" + targetType
              + "] on " + pb.getEntityClass() + "->" + pb.getBindingName());
          value = null;
        }
          
        // finally set the value at the last path
        ReflectionUtility.setValue(set, target, value);

      } else if (p.length == 2) {
        // if p length == to 2 the, continue to recursively walk the graph.
        Class<?> nextClass = get.getReturnType();
        Object next = ReflectionUtility.getValue(get, target);
        if (next == null) {
          next = ReflectionUtility.newInstance(nextClass);
          ReflectionUtility.setValue(set, target, next);
        }

        // try to map the value to the next available path
        return map(pb, next, value, p[1]);
      }
    }
    return target;

    /*
     * // if the length of p is > 0 then the path is valid. if (p.length > 0) {
     * 
     * // get the field name from index 0, since we only splice by a // maximum
     * of 2 String fieldName = p[0];
     * 
     * // find the getter method, make sure it exists Method getterMethod =
     * ReflectionUtility.findGetterMethod( targetClazz, fieldName); if
     * (getterMethod == null) { throw new RuntimeException(new
     * NoSuchMethodException( "No getter method found when using field name [" +
     * fieldName + "] as search pattern!")); }
     * 
     * // find the setter method, make sure it exists Method setterMethod =
     * ReflectionUtility.findSetterMethod( targetClazz, getterMethod); if
     * (setterMethod == null) { throw new RuntimeException(new
     * NoSuchMethodException( "No setter method found when using field name [" +
     * fieldName + "] as search pattern!")); }
     * 
     * // whats the return type of the getter method? Class<?> targetType =
     * getterMethod.getReturnType();
     * 
     * // if the length of p is equal == to 1, then we've hit the end // of the
     * path if (p.length == 1) { // convert the parameter type value =
     * convertValue(value, targetType);
     * 
     * // finally set the value at the last path
     * ReflectionUtility.setValue(setterMethod, target, value); } else if
     * (p.length == 2) {
     * 
     * // if p length == to 2 the, continue to recursively walk the // graph.
     * Class<?> nextClass = getterMethod.getReturnType(); Object nextTarget =
     * ReflectionUtility.getValue(getterMethod, target); if (nextTarget == null)
     * { nextTarget = ReflectionUtility.newInstance(nextClass);
     * ReflectionUtility .setValue(setterMethod, target, nextTarget); }
     * 
     * // try to map the value to the next available path return map(nextTarget,
     * value, p[1]); } } return target;
     */
  }

  protected Method[] getMethodsGetterAndSetter(Class<?> targetClazz, String fieldName) {
    // String[] p = path.split("\\.", 2);

    // if the field name is not null or empty, then its probably valid.
    if (Comparison.isNotEmpty(fieldName)) {

      Method[] methods = new Method[2];

      // get the field name from index 0, since we only splice by a
      // maximum of 2
     // String fieldName = p[0];

      // find the getter method, make sure it exists
      Method getterMethod = ReflectionUtility.findGetterMethod(targetClazz, fieldName);
      if (getterMethod == null) {
        throw new RuntimeException(new NoSuchMethodException("No getter method found when using field name [" + fieldName + "] as search pattern!"));
      }

      // find the setter method, make sure it exists
      Method setterMethod = ReflectionUtility.findSetterMethod(targetClazz, getterMethod);
      if (setterMethod == null) {
        throw new RuntimeException(new NoSuchMethodException("No setter method found when using field name [" + fieldName + "] as search pattern!"));
      }

      methods[0] = getterMethod;
      methods[1] = setterMethod;
      return methods;
    }
    return null;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.MapUtility#get(com.p5solutions.core.jpa.orm.ParameterBinder,
   *      java.lang.Object, java.lang.String)
   */
  @Override
  public Object get(ParameterBinder pb, Object target, String bindingPath) {
    if (target == null) {
      return null;
    }

    Class<?> targetClazz = target.getClass();
    String[] p = bindingPath.split("\\.", 2);

    // p[0] is the first part for example. address.line1 : p[0] = address p[1] = line1
    Method[] methods = getMethodsGetterAndSetter(targetClazz, p[0]);
    if (methods != null) {
    	
      // getter method in first part of array
      Method get = methods[0];

      if (p.length == 1) {
        // Get the final value and return it, but first, pass it throw a conversion method (just-in-case)
        return convertValueForSQLFromEntity(pb, ReflectionUtility.getValue(get, target));
      } else {
        // if p length == to 2 the, continue to recursively walk the graph.
        Object next = ReflectionUtility.getValue(get, target);
        return get(pb, next, p[1]);
      }
    }
    return null;
  }

  /**
   * Should be overwritten if there are special cases, for example handling of Blob to oid in postgresql scenario,
   * or converting strings to dates or numbers or whatever else to another type before passing it onto the PreparedStatement
   * 
   * @param pb
   * @param value
   * @return
   */
  protected Object convertValueForSQLFromEntity(ParameterBinder pb, Object value) {
    
    // if instance of any of these or is flagged with @Lob annotation
    if (value instanceof Blob || pb.isLob() || value instanceof byte[]) {
      return getConversionUtility().convertToSqlType(pb, value);
    }
    
    return value;
  }
  
  /**
   * Convert a value returned from a query resultset back to the entity's property.
   * 
   * @param value
   *          the value
   * @param targetType
   *          the target type
   * @return the object
   */
  protected Object convertValueForEntityFromSQL(ParameterBinder pb, Object value, String bindingPath, Class<?> targetType) throws TypeConversionException {
    // if value is not null, then check the target type against
    // the value type
    if (value != null) {
      // if target type and value type are not the same, the
      // attempt a conversion
      if (!conversionUtility.isSameClass(value, targetType)) {
        value = conversionUtility.convert(pb, value, bindingPath, targetType);
      }
    }
    return value;
  }

  /**
   * Gets the conversion utility.
   * 
   * @return the conversion utility
   */
  public ConversionUtility getConversionUtility() {
    if (conversionUtility == null) {
      this.conversionUtility = new ConversionUtilityImpl();
    }
    return conversionUtility;
  }

  /**
   * Sets the conversion utility.
   * 
   * @param conversionUtility
   *          the new conversion utility
   */
  public void setConversionUtility(ConversionUtility conversionUtility) {
    this.conversionUtility = conversionUtility;
  }

}
