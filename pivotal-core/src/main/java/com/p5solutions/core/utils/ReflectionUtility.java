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
package com.p5solutions.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;

/**
 * The Class ReflectionUtility. Consists of static helper methods for use in reflections on java objects / classes
 * 
 * TODO: perhaps amalgamate with spring's reflection utility?
 * 
 * @author Kasra Rasaee
 * 
 */
public class ReflectionUtility {

	/** The cached methods. */
	private static Map<Class<?>, List<Method>> cachedMethods = new HashMap<Class<?>, List<Method>>();

	/** The logger. */
	private static Log logger = LogFactory.getLog(ReflectionUtility.class);

	/**
	 * Builds the field name.
	 * 
	 * @param method
	 *          the method
	 * @return the string
	 */
	public static String buildFieldName(Method method) {
		String fieldName = null;
		String methodName = method.getName();

		if (isGetter(method) || isSet(method)) {
			fieldName = methodName.substring(3);
		} else if (isIs(method)) {
			fieldName = methodName.substring(2);
		}

		fieldName = StringUtils.uncapitalize(fieldName);

		return fieldName;
	}

	/**
	 * Builds the setter name.
	 * 
	 * @param getterMethod
	 *          the getter method
	 * @return the string
	 */
	public static String buildSetterName(Method getterMethod) {
		String name = buildFieldName(getterMethod);
		String firstLetter = name.substring(0, 1).toUpperCase();
		name = "set" + firstLetter + name.substring(1);
		return name;
	}

	/**
	 * Builds the getter name.
	 * 
	 * @param setterMethod
	 *          the setter method
	 * @return the string
	 */
	public static String buildGetterName(Method setterMethod) {
		if (setterMethod != null) {
			Class<?>[] paramTypes = setterMethod.getParameterTypes();

			String fieldName = setterMethod.getName().substring(3);
			if (paramTypes != null && paramTypes.length == 1) {
				if (paramTypes[0].isAssignableFrom(boolean.class)) {
					return "is" + fieldName;
				}
			} else {
				throw new RuntimeException("Invalid number of arguments for setter for " + "field name: " + fieldName);
			}

			return "get" + fieldName;
		}

		throw new RuntimeException("Setter method cannot be null");
	}

	/**
	 * Find setter method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param getterMethod
	 *          the getter method
	 * @return the method
	 */
	public static Method findSetterMethod(Class<?> clazz, Method getterMethod) {
		if (getterMethod != null) {
			String setterName = buildSetterName(getterMethod);
			if (setterName != null) {
				return findMethod(clazz, setterName, getterMethod.getReturnType());
			}
		}
		return null;
	}

	/**
	 * Find setter method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @param paramTypes
	 *          the param types
	 * @return the method
	 */
	public static Method findSetterMethod(Class<?> clazz, String fieldName, Class<?>... paramTypes) {
		String set = "set";
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		set += firstLetter + fieldName.substring(1);
		return findMethod(clazz, set, paramTypes);
	}

	/**
	 * Find setter method ignoring any parameter arguments.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @return the method
	 */
	public static Method findSetterMethodIgnoreParams(Class<?> clazz, String fieldName) {
		return findSetterMethod(clazz, fieldName, true, (Class<?>[]) null);
	}

	/**
	 * Find setter method.
	 * 
	 * @param clazz
	 *          the clazz to search against
	 * @param fieldName
	 *          the field name to search for
	 * @param ignoreParamSearch
	 *          ignore parameter search
	 * @param paramTypes
	 *          the parameter types, note that if ignoreParamSearch is set to <code>true</code> this field is ignore
	 *          completely
	 * @return the method
	 */
	public static Method findSetterMethod(Class<?> clazz, String fieldName, boolean ignoreParamSearch,
			Class<?>... paramTypes) {
		String set = "set";
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		set += firstLetter + fieldName.substring(1);
		return findMethod(clazz, set, ignoreParamSearch, paramTypes);
	}

	/**
	 * Find setter method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @return the method
	 */
	public static Method findSetterMethod(Class<?> clazz, String fieldName) {
		return findSetterMethod(clazz, fieldName, (Class<?>[]) null);
	}

	/**
	 * Find getter method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param setterMethod
	 *          the setter method
	 * @return the method
	 */
	public static Method findGetterMethod(Class<?> clazz, Method setterMethod) {
		if (setterMethod != null) {
			String getterName = buildGetterName(setterMethod);
			if (getterName != null) {
				return findMethod(clazz, getterName, (Class<?>[]) null);
			}
		}
		return null;
	}

	/**
	 * Find getter method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @return the method
	 */
	public static Method findGetterMethod(Class<?> clazz, String fieldName) {
		String get = null;

		if (clazz.isAssignableFrom(Boolean.class)) {
			get = "is";
		} else {
			get = "get";
		}

		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		get += firstLetter + fieldName.substring(1);

		return findMethod(clazz, get);
	}

	/**
	 * Checks if a method is transient either by specifying the transient modifier or the {@link Transient} annotation.
	 * 
	 * @param method
	 *          the method
	 * @return true, if is transient
	 */
	public static boolean isTransient(Method method) {
		if (Modifier.isTransient(method.getModifiers())) {
			return true;
		}

		if (method.isAnnotationPresent(Transient.class)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if is a boolean class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isBooleanClass(Class<?> clazz) {
		return Boolean.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is clob.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isClob(Class<?> clazz) {
		return Clob.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is timestamp.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isTimestamp(Class<?> clazz) {
		return Timestamp.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is blob.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isBlob(Class<?> clazz) {
		return Blob.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is number class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isNumberClass(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is serializable class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isSerializableClass(Class<?> clazz) {
		return Serializable.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is date.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isDate(Class<?> clazz) {
		return Date.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is byte class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isByteClass(Class<?> clazz) {
		return Byte.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is short class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isShortClass(Class<?> clazz) {
		return Short.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is integer class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isIntegerClass(Class<?> clazz) {
		return Integer.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is big integer class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isBigIntegerClass(Class<?> clazz) {
		return BigInteger.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is long class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isLongClass(Class<?> clazz) {
		return Long.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is float class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isFloatClass(Class<?> clazz) {
		return Float.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is double class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isDoubleClass(Class<?> clazz) {
		return Double.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is big decimal class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isBigDecimalClass(Class<?> clazz) {
		return BigDecimal.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is natural number class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isNaturalNumberClass(Class<?> clazz) {
		return isByteClass(clazz) // byte
				|| isShortClass(clazz) // short
				|| isIntegerClass(clazz) // integer
				|| isLongClass(clazz) // long
				|| isBigIntegerClass(clazz); // big integer
	}

	/**
	 * Checks if is decimal number class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isDecimalNumberClass(Class<?> clazz) {
		return isFloatClass(clazz) // float
				|| isDoubleClass(clazz) // double
				|| isBigDecimalClass(clazz); // big-decimal
	}

	/**
	 * Checks if is string class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isStringClass(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	// TODO
	public static Boolean isBasicClass(Class<?> clazz) {
		if (clazz != null) {
			if (clazz.isPrimitive()) {
				return true;
			}

			if (isNumberClass(clazz)) {
				return true;
			}

			// check for char

			if (String.class.equals(clazz)) {
				return true;
			}

			if (isDate(clazz)) {
				return true;
			}

			// TODO check for other types.. such as enums
		}
		return false;
	}

	/**
	 * Checks if is map.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isMap(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is collection class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isCollectionClass(Class<?> clazz) {
		return Collection.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if clazz is a map class.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isMapClass(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if is array.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isArray(Class<?> clazz) {
		return clazz.isArray();
	}

	/**
	 * Checks if is array.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isEnum(Class<?> clazz) {
		return clazz.isEnum();
	}

	/**
	 * Checks if is byte array.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the boolean
	 */
	public static Boolean isByteArray(Class<?> clazz) {
		return byte[].class.isAssignableFrom(clazz);
	}

	/**
	 * Check if the method is abstract.
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isAbstract(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}

	/**
	 * Is method a getter, such as (getXYZ).
	 * 
	 * @param method
	 *          the method
	 * @return true, if checks if is getter
	 */
	public static boolean isGetter(Method method) {

		if (method != null) {
			String methodName = method.getName();
			return methodName.startsWith("get");
		}

		return false;
	}

	/**
	 * Checks if is checks if is.
	 * 
	 * @param method
	 *          the method
	 * @return true, if is checks if is
	 */
	public static boolean isIs(Method method) {

		if (method != null) {
			String methodName = method.getName();
			return methodName.startsWith("is");
		}

		return false;
	}

	/**
	 * Checks if is sets the.
	 * 
	 * @param method
	 *          the method
	 * @return true, if is sets the
	 */
	public static boolean isSet(Method method) {
		if (method != null) {
			String methodName = method.getName();
			return methodName.startsWith("set");
		}
		return false;
	}

	/**
	 * Gets the value.
	 * 
	 * @param field
	 *          the field
	 * @param entity
	 *          the entity
	 * @return the value
	 */
	public static Object getValue(String field, Object entity) {
		Method method = findGetterMethod(entity.getClass(), field);
		return getValue(method, entity);
	}

	/**
	 * Gets the value by field.
	 * 
	 * @param fieldName
	 *          the field name
	 * @param target
	 *          the target
	 * @return the value by field
	 */
	public static Object getValueByField(String fieldName, Object target) {
		Class<?> clazz = target.getClass();
		Field field = ReflectionUtility.findField(clazz, fieldName);
		return getValue(field, target);
	}

	/**
	 * Gets the value.
	 * 
	 * @param method
	 *          the method
	 * @param entity
	 *          the entity
	 * @return the value
	 */
	public static Object getValue(Method method, Object entity) {
		try {
			return method.invoke(entity, new Object[] {});
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Sets the value.
	 * 
	 * @param method
	 *          the method
	 * @param entity
	 *          the entity
	 * @param value
	 *          the value
	 */
	public static void setValue(Method method, Object entity, Object value) {
		invoke(entity, method, value);
	}

	/**
	 * Set value on entity using the field to search for a setter name.
	 * 
	 * @param field
	 *          Field to use when building a setter name
	 * @param entity
	 *          The entity or target object to set the value on
	 * @param value
	 *          The actual value being set on the target or entity using the field name as the base for searching the set
	 *          method
	 */
	public static void setValue(String field, Object entity, Object value) {
		Class<?> clazz = entity.getClass();
		// TODO: why is it ignore-params? ease-of-use?
		Method method = findSetterMethodIgnoreParams(clazz, field);
		// Field field = ReflectionUtility.findField(clazz, fieldName);
		// Class<?>[] fieldSetterParam = new Class[] { field.getClass() };
		// Method method = findSetterMethod(clazz, fieldName, fieldSetterParam);
		if (method != null) {
			setValue(method, entity, value);
		}
	}

	/**
	 * Gets the value.
	 * 
	 * @param field
	 *          the field
	 * @param target
	 *          the target
	 * @return the value
	 */
	public static Object getValue(Field field, Object target) {
		Object val = null;
		boolean access = field.isAccessible();
		try {
			field.setAccessible(true);
			val = field.get(target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			field.setAccessible(access);
		}
		return val;
	}

	/**
	 * Sets the value.
	 * 
	 * @param field
	 *          the field
	 * @param target
	 *          the target
	 * @param value
	 *          the value
	 */
	public static void setValue(Field field, Object target, Object value) {
		try {
			boolean access = field.isAccessible();
			field.setAccessible(true);
			field.set(target, value);
			field.setAccessible(access);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Make accessible.
	 * 
	 * @param method
	 *          the method
	 * @return true, if successful
	 */
	public static boolean makeAccessible(Method method) {
		boolean original = method.isAccessible();
		if (!original) {
			method.setAccessible(true);
		}
		return original;
	}

	/**
	 * Undo accessible.
	 * 
	 * @param method
	 *          the method
	 * @param original
	 *          the original
	 */
	public static void undoAccessible(Method method, boolean original) {
		method.setAccessible(original);
	}

	/**
	 * Gets the methods by annotation.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param annotation
	 *          the annotation
	 * @return the methods by annotation
	 */
	public static <T extends Annotation> List<Method> getMethodsByAnnotation(Class<?> clazz, Class<T> annotation) {

		List<Method> methods = new ArrayList<Method>();

		for (Method method : findAllMethods(clazz)) {
			T ann = findAnnotation(method, annotation);

			if (ann != null) {
				methods.add(method);
			}
		}

		return methods;
	}

	/**
	 * Checks if is static or final.
	 * 
	 * @param field
	 *          the field
	 * @return true, if is static or final
	 */
	public static boolean isStaticOrFinal(Field field) {
		return isStatic(field) || isFinal(field);
	}

	/**
	 * Checks if is static.
	 * 
	 * @param field
	 *          the field
	 * @return true, if is static
	 */
	public static boolean isStatic(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isStatic(modifiers);
	}

	/**
	 * Checks if is final.
	 * 
	 * @param field
	 *          the field
	 * @return true, if is final
	 */
	public static boolean isFinal(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isFinal(modifiers);
	}

	/**
	 * Find field.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @return the field
	 */
	public static Field findField(Class<?> clazz, String fieldName) {
		return findField(clazz, fieldName, true);
	}

	/**
	 * Find field.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @param throwException
	 *          the throw exception
	 * @return the field
	 */
	public static Field findField(Class<?> clazz, String fieldName, boolean throwException) {

		Field field = null;
		Class<?> superClazz = clazz.getSuperclass();
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			if (!throwException) {
				logger.error(e);
			}
			if (superClazz.equals(Object.class)) {
				throw new RuntimeException(e);
			}
		}

		if (field == null) {
			field = findField(superClazz, fieldName);
		}

		return field;
	}

	/**
	 * Find fields.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the list
	 */
	public static List<Field> findFields(Class<?> clazz) {
		List<Field> array = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();

		// add all the fields within this class
		Collections.addAll(array, fields);

		// check for super classes and fields
		Class<?> superClazz = clazz.getSuperclass();
		if (!superClazz.equals(Object.class)) {
			List<Field> superArray = findFields(superClazz);
			array.addAll(superArray);
		}

		return array;
	}

	/**
	 * Find all methods.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the list
	 */
	public static List<Method> findAllMethods(Class<?> clazz) {

		List<Method> methods = new ArrayList<Method>();

		// Iterate each method
		for (Method method : clazz.getDeclaredMethods()) {
			methods.add(method);
		}

		// recursively build list of methods
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			List<Method> superMethods = findAllMethods(superClazz);
			methods.addAll(superMethods);
		}

		return methods;
	}

	/**
	 * Find get methods with no params. All getters that aren't Native, Static, Abstract, Synthetic, or Bridge are
	 * returned.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return the list
	 */
	public synchronized static List<Method> findGetMethodsWithNoParams(Class<?> clazz) {

		/* if a cache already exists simply return it */
		List<Method> returnMethods = cachedMethods.get(clazz);

		if (returnMethods == null) {
			/* create a new cache and return it */
			returnMethods = new ArrayList<Method>();

			for (Method method : findAllMethods(clazz)) {

				// check for compiler introduced methods, as well as native methods.
				// simply ignore these methods, as they are probably not what we are
				// looking for.

				int modifiers = method.getModifiers();

				if (Modifier.isNative(modifiers) //
						|| Modifier.isStatic(modifiers) //
						|| Modifier.isAbstract(modifiers) //
						|| method.isSynthetic() //
						|| method.isBridge()) {
					continue;
				}

				// if its a getter then add it to the list
				if (isIs(method) || isGetter(method)) {
					if (!doesMethodHaveParams(method)) {
						returnMethods.add(method);
					}
				}
			}

			cachedMethods.put(clazz, returnMethods);
		}

		return returnMethods;
	}

	/**
	 * Find annotation.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param method
	 *          the method
	 * @param annotation
	 *          the annotation
	 * @return the t
	 */
	public static <T extends Annotation> T findAnnotation(Method method, Class<T> annotation) {
		return method.getAnnotation(annotation);
	}

	/**
	 * Find methods with annotation.
	 * 
	 * aram <T> the generic type
	 * 
	 * @param clazz
	 *          the clazz
	 * @param annotation
	 *          the annotation
	 * @return the a list of objects, otherwise returns empty list
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> List<Method> findMethodsWithAnnotation(Class<?> clazz, Class<T> annotation) {
		List<Method> methods = new ArrayList<Method>();
		// find all methods and filter out overridden-s taking only the inner most
		// ones (children)
		// ***won't work***: abstract annotated methods will show up twice but be
		// mapped once w/o annotation... @#^$@#$%#$^
		// Map<String, Method> map = new HashMap<String, Method>();
		// List<Method> allMethods = findAllMethods(clazz); // including super-class
		// for (Method method : allMethods) {
		// String signature = methodSignature(method);
		// if (!map.containsKey(signature)) {
		// map.put(signature, method);
		// }
		// }
		// work off of those methods then
		// List<Method> uniqueMethods = new ArrayList<Method>(map.values());
		// for (Method method : uniqueMethods) { // including super-class
		for (Method method : findAllMethods(clazz)) { // including super-class
			if (hasAnyAnnotation(method, annotation)) {
				methods.add(method);
			}
		}
		// return result
		return methods;
	}

	/**
	 * Return type + name + parameter types. Modifiers not included as they can be changed and still be considered as
	 * overriding. Example: String_getName(_), void_setPerson(_Person_boolean)
	 * 
	 * @param method
	 * @return
	 */
	public static String getMethodSignature(Method method) {
		// return method.toGenericString();
		StringBuilder sb = new StringBuilder(method.getReturnType().getSimpleName()).append("_").append(method.getName())
				.append("(_");
		for (Class<?> clazz : method.getParameterTypes()) {
			sb.append(clazz.getSimpleName()).append("_");
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Find method with annotation.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param annotation
	 *          the annotation
	 * @return the method
	 */
	public static <T extends Annotation> Method findMethodWithAnnotation(Class<?> clazz, Class<T> annotation) {
		List<Method> methods = findMethodsWithAnnotation(clazz, annotation);

		if (methods.size() == 0) {
			throw new RuntimeException("No methods founds under class type " + clazz + " with an annotation of " + annotation);
		}

		if (methods.size() > 1) {
			throw new RuntimeException("Too many methods found under class type " + clazz + " with annotation " + annotation);
		}

		return methods.get(0);
	}

	/**
	 * Checks for any annotation.
	 * 
	 * @param method
	 *          the method
	 * @param clazzAnnotations
	 *          the clazz annotations
	 * @return true, if successful
	 */
	public static boolean hasAnyAnnotation(Method method, Class<? extends Annotation>... clazzAnnotations) {
		for (Class<? extends Annotation> annotation : clazzAnnotations) {
			if (findAnnotation(method, annotation) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for any annotation.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param clazzAnnotations
	 *          the clazz annotations
	 * @return true, if successful
	 */
	public static boolean hasAnyAnnotation(Class<?> clazz, Class<? extends Annotation>... clazzAnnotations) {
		for (Class<? extends Annotation> annotation : clazzAnnotations) {
			if (findAnnotation(clazz, annotation) != null) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Find annotation.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param annotation
	 *          the annotation
	 * @return the t
	 */
	public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotation) {
		T ann = clazz.getAnnotation(annotation);
		if (ann == null) {
			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && !superClazz.equals(Object.class)) {
				ann = findAnnotation(superClazz, annotation);
			}
		}
		return ann;
	}

	/**
	 * Find method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param methodName
	 *          the method name
	 * @return the method
	 */
	public static Method findMethod(Class<?> clazz, String methodName) {
		return findMethod(clazz, methodName, (Class<?>[]) null);
	}

	/**
	 * Find method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param methodName
	 *          the method name
	 * @param paramTypes
	 *          the param types
	 * @return the method
	 */
	public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		return findMethod(clazz, methodName, false, paramTypes);
	}

	/**
	 * Check params.
	 * 
	 * @param method
	 *          the method
	 * @param paramTypes
	 *          the param types
	 * @return true, if successful
	 */
	public static boolean checkParams(Method method, Class<?>... paramTypes) {

		/* Check the length of parameter types, should be the same */
		Class<?>[] methodParamTypes = method.getParameterTypes();

		// Length of the parameters and method parameters
		int paramTypesLen = 0;
		int methodParamTypesLen = 0;

		// get the parameter length if any
		if (paramTypes != null) {
			methodParamTypesLen = paramTypes.length;
		}

		// get the method parameter length if any
		if (methodParamTypes != null) {
			paramTypesLen = methodParamTypes.length;
		}

		// are the lengths equal?
		if (paramTypesLen != methodParamTypesLen) {
			// if not then return quickly.
			return false;
		}

		// params match yet? no!
		boolean paramsMatch = true;

		/* check to see if parameter types are the same */
		for (int i = 0; i < methodParamTypes.length; i++) {
			/* get the class types for the parameters */
			Class<?> parameterType = paramTypes[i];
			Class<?> methodParamClazz = methodParamTypes[i];

			/* does the parameter match in sequence */
			if (!methodParamClazz.isAssignableFrom(parameterType)) {
				paramsMatch = false;
			}
		}

		/* if all parameter types match then return the method */
		if (paramsMatch) {
			return true;
		}

		return false;
	}

	/**
	 * Find method.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param methodName
	 *          the method name
	 * @param ignoreParamSearch
	 *          the ignore param search
	 * @param paramTypes
	 *          the param types
	 * @return the method
	 */
	public static Method findMethod(Class<?> clazz, String methodName, boolean ignoreParamSearch, Class<?>... paramTypes) {

		Method method = null;

		// Iterate all declared methods of the class
		for (Method m : clazz.getDeclaredMethods()) {

			// continue to next method, if name does not match
			if (!m.getName().equals(methodName)) {
				continue;
			}

			// only check the parameter lengths and
			// types if ignoreParamSearch is false
			if (!ignoreParamSearch && checkParams(m, paramTypes)) {
				method = m; // set the returning result
				break; // exit the loop
			} else if (ignoreParamSearch && method != null) {
				throw new RuntimeException(
						"Multiple methods found, please specify a list of parameter types to refine your method search.");
			}

			method = m;
		}

		/* if the method is still null, then check its superclass */
		if (method == null) {
			Class<?> superClazz = clazz.getSuperclass();

			// if we've hit the base object?
			if (!superClazz.equals(Object.class)) {

				// recursively look for the method
				method = findMethod( //
						superClazz, //
						methodName, //
						ignoreParamSearch, //
						paramTypes);
			}
		}

		return method;
	}

	/**
	 * Does method have params.
	 * 
	 * @param method
	 *          the method
	 * @return true, if successful
	 */
	public static boolean doesMethodHaveParams(Method method) {
		/* Ignore any getters with parameters */
		if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if is value null.
	 * 
	 * @param target
	 *          the target
	 * @param method
	 *          the method
	 * @return true, if is value null
	 */
	public static boolean isValueNull(Object target, Method method) {
		// get the value of the method
		Object value = ReflectionUtility.invoke(target, method);

		// if its not null then exit with a false
		if (value != null) {
			return false;
		}

		return true;
	}

	/**
	 * Invoke.
	 * 
	 * @param object
	 *          the object
	 * @param method
	 *          the method
	 * @return the object
	 */
	public static Object invoke(Object object, Method method) {
		return invoke(object, method, (Object[]) null);
	}

	/**
	 * Invoke.
	 * 
	 * @param <O>
	 *          the generic type
	 * @param <I>
	 *          the generic type
	 * @param object
	 *          the object
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * @return the o
	 */
	@SuppressWarnings("unchecked")
	public static <O, I> O invoke(I object, Method method, Object... args) {
		return (O) invoke(object, method, false, args);
	}

	/**
	 * Invoke.
	 * 
	 * @param <O>
	 *          the generic type
	 * @param <I>
	 *          the generic type
	 * @param object
	 *          the object
	 * @param method
	 *          the method
	 * @param ignoreAccess
	 *          the ignore access
	 * @return the o
	 */
	@SuppressWarnings("unchecked")
	public static <O, I> O invoke(I object, Method method, boolean ignoreAccess) {
		return (O) invoke(object, method, ignoreAccess, (Object[]) null);
	}

	/**
	 * Invoke.
	 * 
	 * @param <O>
	 *          the generic type
	 * @param <I>
	 *          the generic type
	 * @param object
	 *          the object
	 * @param method
	 *          the method
	 * @param ignoreAccess
	 *          the ignore access
	 * @param args
	 *          the args
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	public static <O, I> O invoke(I object, Method method, boolean ignoreAccess, Object... args) {
		if (object == null) {
			throw new RuntimeException("Object cannot be null when invoking its methods");
		}

		Object returnObject = null;
		Class<?> clazz = object.getClass();

		try {

			/* call the method */
			if (method != null) {
				if (AopUtils.isAopProxy(object)) {
					InvocationHandler handler = Proxy.getInvocationHandler(object);
					returnObject = handler.invoke(object, method, args);
				} else {
					boolean isAccessible = method.isAccessible();
					try {
						if (!isAccessible && ignoreAccess) {
							method.setAccessible(true);
						}
						returnObject = method.invoke(object, args);
					} finally {
						if (ignoreAccess) {
							method.setAccessible(isAccessible);
						}
					}
				}
			} else {
				throw new RuntimeException("Method cannot be null");
			}
		} catch (Throwable e) {
			// get the target class if its a proxy
			clazz = AopUtils.getTargetClass(object);

			/* Logger that is available to subclasses */
			Log logger = LogFactory.getLog(clazz);
			// logger.error("Unable to invoke method " + method.getName() + " within " + clazz.getCanonicalName() + "\n"
			// + getStackTrace(e));

			throw new RuntimeException(e);
		}

		return (O) returnObject;
	}

	/**
	 * Gets the log stack trace.
	 * 
	 * @param e
	 *          the e
	 * @return the log stack trace
	 */
	public static String getStackTrace(Throwable e) {
		if (e == null) {
			return "";
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			e.printStackTrace(ps);
			String ret = os.toString();
			ps.close();
			os.close();
			return ret;
		} catch (IOException t) {
			logger.error("Error in closing the output stream for byte array when getting stack trace on exception "
					+ e.toString());
		}
		return "";
	}

	/**
	 * New instance.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @return the t
	 */
	public static <T> T newInstance(Class<T> clazz) {
		T instance = null;
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			logger.error(e);
		}
		return instance;
	}

	/**
	 * New instance.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param type
	 *          the type
	 * @param argument
	 *          the argument
	 * @return the t
	 */
	public static <T> T newInstance(Class<T> clazz, Class<?> type, Object argument) {
		return newInstance(clazz, new Class<?>[] { type }, new Object[] { argument });
	}

	/**
	 * New instance.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param paramTypes
	 *          the param types
	 * @param arguments
	 *          the arguments
	 * @return the t
	 */
	public static <T> T newInstance(Class<T> clazz, Class<?>[] paramTypes, Object[] arguments) {
		T instance = null;

		try {
			// Constructor<?>[] constructors = clazz.getDeclaredConstructors();
			Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
			boolean access = constructor.isAccessible();
			constructor.setAccessible(true);
			instance = constructor.newInstance(arguments);
			if (!access) {
				constructor.setAccessible(false);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return instance;
	}

	/**
	 * Gets the field class.
	 * 
	 * @param clazz
	 *          the class
	 * @param fieldName
	 *          the field name
	 * @return the field class
	 */
	public static Class<?> getFieldClass(Class<?> clazz, String fieldName) {

		if (Comparison.isNull(clazz)) {
			// field does not exist
			return null;
		}
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				// found field return field type
				return field.getType();
			}
		}
		// recursive call to check the super class for field
		return getFieldClass(clazz.getSuperclass(), fieldName);
	}
}
