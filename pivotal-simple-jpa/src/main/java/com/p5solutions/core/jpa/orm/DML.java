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

import java.util.List;

import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class DML.
 */
public class DML extends AbstractSQL {

  /**
   * Instantiates a new DML.
   * 
   * @param entityClass
   *          the entity class
   */
  public DML(Class<?> entityClass) {
    super(entityClass);
  }

  /**
   * Gets the DML identifier. Generates a key value pair separated by a
   * semi-colon
   * 
   * @return the query identifier
   */
  public String getDMLIdentifier() {
    return super.getSQLIdentifier();
  }

  /**
   * Gets the DML.
   * 
   * @return the dml
   */
  public String getDML() {
    return super.getSQL();
  }

  /**
   * Sets the DML.
   * 
   * @param dml
   *          the new query
   */
  public void setDML(String dml) {
    super.setSQL(dml);
  }

}
