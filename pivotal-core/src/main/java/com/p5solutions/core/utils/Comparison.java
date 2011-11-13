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

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * Comparison: Common utility functions, such as comparing object equality,
 * checking if object content is null, or empty.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-04
 */
public class Comparison {

	/**
	 * Compare to.
	 * 
	 * @param a
	 *          the a
	 * @param b
	 *          the b
	 * @return the int
	 */
	public static int compareTo(String a, String b) {
		// if both values are not null then compare them
		if (a != null && b != null) {
			return a.compareTo(b);
		}

		// if 'b' == null then 'a' must be greater than b
		if (a != null && b == null) {
			return 1;
		}

		// if 'a' == null then 'b' must be greater than a
		if (b != null && a == null) {
			return -1;
		}

		// otherwise they are equal
		return 0;
	}

	/**
	 * Checks if is equal.
	 * 
	 * @param a
	 *          the a
	 * @param b
	 *          the b
	 * @return true, if is equal
	 */
	public static boolean isEqual(Object a, Object b) {
		if (isNull(a) && isNull(b)) {
			return true;
		}

		if (!isNull(a)) {
			return a.equals(b);
		} else if (!isNull(b)) {
			return b.equals(a);
		}

		return false;
	}

	/**
	 * Checks if is english.
	 * 
	 * @param locale
	 *          the locale
	 * @return true, if is english
	 */
	public static boolean isEnglish(Locale locale) {
		String lang = Locale.ENGLISH.getLanguage();
		if (lang.equals(locale.getLanguage())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is french.
	 * 
	 * @param locale
	 *          the locale
	 * @return true, if is french
	 */
	public static boolean isFrench(Locale locale) {
		String lang = Locale.FRENCH.getLanguage();
		if (lang.equals(locale.getLanguage())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is true or null.
	 * 
	 * @param val
	 *          the val
	 * 
	 * @return true, if is true or null
	 */
	public static boolean isTrueOrNull(Boolean val) {
		return val == null || val.equals(true);
	}

	/**
	 * Checks if is false or null.
	 * 
	 * @param val
	 *          the val
	 * @return true, if is false or null
	 */
	public static boolean isFalseOrNull(Boolean val) {
		return val == null || val.equals(false);
	}

	/**
	 * Checks if is true.
	 * 
	 * @param val
	 *          the val
	 * @return true, if is true
	 */
	public static boolean isTrue(Boolean val) {
		return Boolean.TRUE.equals(val);
	}

	/**
	 * Checks if a list is empty or null.
	 * 
	 * @param list
	 *          the list
	 * 
	 * @return true, if is empty or null
	 */
	public static boolean isEmptyOrNull(List<?> list) {
		return (list == null || list.size() == 0);
	}

	/**
	 * Checks if is not empty or null.
	 *
	 * @param list the list
	 * @return true, if is not empty or null
	 */
	public static boolean isNotEmptyOrNull(List<?> list) {
		return !isEmptyOrNull(list);
	}
	
	/**
	 * Check if an array is empty or null.
	 * 
	 * @param list
	 *          the list
	 * @return if empty
	 */
	public static boolean isEmptyOrNull(Object[] list) {
		return (list == null || list.length == 0);
	}

	/**
	 * Checks if is null.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is null
	 */
	public static boolean isNull(Object value) {
		return value == null;
	}

	/**
	 * Checks if is not null.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is not null
	 */
	public static boolean isNotNull(Object value) {
		return !isNull(value);
	}

	/**
	 * Checks if is empty.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String value) {
		return StringUtils.isEmpty(value);
	}

	/**
	 * Checks if is empty or null, after trimming all white spaces.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is empty or null trim
	 */
	public static boolean isEmptyOrNullTrim(String value) {
		if (value != null) {
			value = value.trim();
		}
		return isEmpty(value);
	}

	/**
	 * Checks if is not empty.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is not empty
	 */
	public static boolean isNotEmpty(String value) {
		return StringUtils.isNotEmpty(value);
	}

	/**
	 * Checks if is numeric.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is numeric
	 */
	public static boolean isNumeric(String value) {
		return StringUtils.isNumeric(value);
	}

	/**
	 * Checks if is alpha.
	 * 
	 * @param value
	 *          the value
	 * @return true, if is alpha
	 */
	public static boolean isAlpha(String value) {
		return StringUtils.isAlpha(value);
	}
}