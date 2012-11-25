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

// TODO Bindable sucks for a name, something more appropriate, maybe Binder??

public interface Binder {
  String getColumnNameAnyJoinOrColumn();

  ParameterBinderColumnMetaData getColumnMetaData();

  void setColumnMetaData(ParameterBinderColumnMetaData columnMetaData);
  
  String getBindingPath();
  
  String getBindingPathForStatement();
}