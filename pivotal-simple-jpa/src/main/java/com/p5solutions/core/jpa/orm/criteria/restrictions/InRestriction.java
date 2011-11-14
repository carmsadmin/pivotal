/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  Zvjezdan Guzijan
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
package com.p5solutions.core.jpa.orm.criteria.restrictions;

import java.util.ArrayList;
import java.util.List;
import com.p5solutions.core.jpa.orm.EntityDetail;


/**
 * Emulate (col IN (a,b,c)) as (col=a OR col=b OR col=c).
 * 
 * @author Zvjezdan Guzijan
 * @since 2011-11-14
 */
public class InRestriction extends AbstractRestriction implements Criterion {

  private String bindingPath;
  private Object[] values;

  public InRestriction(String bindingPath, Object... values) {
    this.bindingPath = bindingPath;
    this.values = values;
  }

  public InRestriction(String bindingPath, String commaDelimList, boolean isNumber) {
    this.bindingPath = bindingPath;
    if (commaDelimList == null) {
      throw new NullPointerException("Cannot extract values for null comma delimited list.");
    }
    List<Object> list = new ArrayList<Object>();
    for (String code : commaDelimList.split(",")) {
      list.add(isNumber ? Integer.parseInt(code) : code);
    }
    this.values = list.toArray();
  }

  @Override
  public String toSql(EntityDetail<?> entityDetail) {
    if (values == null || values.length == 0) {
      throw new NullPointerException("Cannot build sql with null/empty values list supplied.");
    }
    StringBuilder sb = new StringBuilder("(");
    for (int i = 0; i < values.length; i++) {
      SimpleExpression e = new SimpleExpression(bindingPath, values[i]);
      sb.append(e.toSql(entityDetail));
      if (i != values.length - 1) {
        sb.append(" OR ");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
