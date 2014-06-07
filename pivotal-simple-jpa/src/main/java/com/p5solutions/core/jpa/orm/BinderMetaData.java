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

public interface BinderMetaData {
  String getColumnLabel();

  void setColumnLabel(String columnLabel);

  String getColumnName();

  void setColumnName(String columnName);

  int getColumnType();

  void setColumnType(int columnType);

  String getColumnTypeName();

  void setColumnTypeName(String columnTypeName);

  int getLength();

  void setLength(int length);

  int getPrecision();

  void setPrecision(int precision);

  int getScale();

  void setScale(int scale);
}
