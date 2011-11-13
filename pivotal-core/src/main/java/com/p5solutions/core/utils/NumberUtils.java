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

import java.math.BigDecimal;

/**
 * NumberUtils: extension of the spring {@link org.springframework.util.NumberUtils}
 * 
 * @author Kasra Rasaee
 * 
 * @see org.springframework.util.NumberUtils
 */
public class NumberUtils extends org.springframework.util.NumberUtils {

//	public static boolean isNumeric(String value, Class<?> targetType) {
//
//		
//		if (ReflectionUtility.isByteClass(targetType)) {
//			return isByte(value);
//		} else if (ReflectionUtility.isShortClass(targetType)) {
//			return isShort(value);
//		} else if (ReflectionUtility.isIntegerClass(targetType)) {
//			return isInteger(value);
//		} else if (ReflectionUtility.isLongClass(targetType)) {
//			return isLong(value);
//		} 
//		
//		boolean isdec = ReflectionUtility.isFloatClass(targetType)
//
//		return isDecimal(value);
//	}
	
	public static boolean isWithinRange(Long value, long min, long max) {
		if (value >= min && value <= max) {
			return true;
		}
		return false;
	}
	
	public static boolean isByte(String value) {
		if (isNatural(value)) {
			Long v = NumberUtils.valueOf(value.toString(), Long.class);
			if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isShort(String value) {
		if (isNatural(value)) {
			Long v = NumberUtils.valueOf(value.toString(), Long.class);
			if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInteger(String value) {
		if (isNatural(value)) {
			Long v = NumberUtils.valueOf(value.toString(), Long.class);
			if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLong(String value) {
		if (isNatural(value)) {
			Long v = NumberUtils.valueOf(value.toString(), Long.class);
			if (v >= Long.MIN_VALUE && v <= Long.MAX_VALUE) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNatural(String value) {
		return isNumeric(value, false);
	}
	
	public static boolean isDecimal(String value) {
		return isNumeric(value, true);
	}
	
	public static boolean isNumeric(String value, boolean allowDOT) {
		if (value == null) {
			return false;
		}
		
		byte[] val = value.toString().getBytes();
		
		for (int i = 0; i < val.length; i++) {
			byte v = val[i];

			if ( v < 48 || v > 57 ) {
				if ( allowDOT && v == '.' ) {
					continue;
				}
				
				return false;
			}
		}
		
		return true;
	}
	/**
	 * Parses the number, but first trim everything but numbers and decimals.
	 * 
	 * @param number
	 *          the number
	 * @param clazz
	 *          the clazz
	 * @return the object
	 */
	public static Object trimValueOf(String number, Class<?> clazz) {
		String trimmed = trim(number, false);
		return valueOf(trimmed, clazz);
	}

	/**
	 * Parses the number.
	 * 
	 * @param number
	 *          the number
	 * @param clazz
	 *          the clazz
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(String number, Class<?> clazz) {
		
		// if the string representation of the number is empty or null
		// then return a null, as it cannot be parsed by the <ClassNumber>.valueOf()
		if (Comparison.isEmptyOrNullTrim(number)) {
			return (T)null;
		}
		
		if (ReflectionUtility.isByteClass(clazz)) {
			return (T)Byte.valueOf(number);
		} else if (ReflectionUtility.isShortClass(clazz)) {
			return (T)Short.valueOf(number);
		} else if (ReflectionUtility.isIntegerClass(clazz)) {
			return (T)Integer.valueOf(number);
		} else if (ReflectionUtility.isLongClass(clazz)) {
			return (T)Long.valueOf(number);
		} else if (ReflectionUtility.isFloatClass(clazz)) {
			return (T)Short.valueOf(number);
		} else if (ReflectionUtility.isDoubleClass(clazz)) {
			return (T)Double.valueOf(number);
		} else if (ReflectionUtility.isBigDecimalClass(clazz)) {
			return (T)BigDecimal.valueOf(Double.valueOf(number));
		}

		return (T)null;
	}

	/**
	 * Trim.
	 * 
	 * @param value
	 *          the value
	 * @return the long
	 */
	public static Long trim(String value) {
		String trimmed = trim(value, false);
		if (trimmed.length() > 0) {
			return Long.valueOf(trimmed);
		}
		return null;
	}

	/**
	 * Trim out everything but numbers.
	 * 
	 * @param value
	 *          the value
	 * @param ignoreDOT
	 *          the ignore dot
	 * @return the string representation of the number
	 * @return
	 */
	public static String trim(String value, boolean ignoreDOT) {
		if (value != null && value.length() > 0) {
			String numeric = new String();

			byte[] buffer = value.getBytes();
			for (int i = 0; i < buffer.length; i++) {
				byte v = buffer[i];
				if ((v >= 48 && v <= 57) || (!ignoreDOT && v == '.')) {
					numeric += new String(new byte[] { v });
				}
			}
			return numeric;
		}

		return null;
	}

	/**
	 * Long value. Also checks for null
	 * 
	 * @param number
	 *          the number
	 * @return the long, returns null if param number is null
	 */
	public static Long longValue(Number number) {
		if (number != null) {
			return number.longValue();
		}
		return null;
	}
}