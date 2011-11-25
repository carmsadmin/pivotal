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

/**
 * ParameterBinders's ColumnMetaData, as a result from a SELECT * FROM XYZ_TBL WHERE 1=0
 * 
 * @author Kasra Rasaee
 * @since 2011-11-23
 * 
 * @see EntityUtility#buildColumnMetaData(javax.persistence.Table, EntityDetail)
 * @see ParameterBinder#getColumnMetaData()
 */
public class ParameterBinderColumnMetaData {
	private String columnLabel;
	private String columnName;
	private int columnType;
	private String columnTypeName;
	private int length;
	private int precision;
	private int scale;

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	//	sb.append("Index: ");  // SHOULD NOT BE USED, SINCE EVERY UNIQUE QUERY STRING WOULD RESULT IN A DIFFERENT INDEX
	//	sb.append(getColumnIndex());
		sb.append("Label: ");
		sb.append(getColumnLabel());
		sb.append(", Name: ");
		sb.append(getColumnName());
		sb.append(", SqlType: ");
		sb.append(getColumnType());
		sb.append(", SqlTypeName: ");
		sb.append(getColumnTypeName());
		sb.append(", Scale: ");
		sb.append(getScale());
		sb.append(", Precision: ");
		sb.append(getPrecision());
		sb.append(", Length: ");
		sb.append(getLength());
		return sb.toString();
	}
}
