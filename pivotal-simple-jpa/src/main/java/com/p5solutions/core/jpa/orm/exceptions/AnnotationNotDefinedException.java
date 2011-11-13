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
package com.p5solutions.core.jpa.orm.exceptions;

/**
 * The Class AnnotationNotDefinedException.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-18
 * 
 */
public class AnnotationNotDefinedException extends RuntimeException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new annotation not defined exception.
   * 
   * @param message
   *          the message
   */
  public AnnotationNotDefinedException(String message) {
    super(message);
  }
}
