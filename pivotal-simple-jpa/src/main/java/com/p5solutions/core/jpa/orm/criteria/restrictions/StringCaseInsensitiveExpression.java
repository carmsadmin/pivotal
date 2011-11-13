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
package com.p5solutions.core.jpa.orm.criteria.restrictions;

import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.utils.Comparison;

public class StringCaseInsensitiveExpression extends SimpleExpression implements Criterion {

  public StringCaseInsensitiveExpression(String bindingPath, Object value) {
    super(bindingPath, value);
  }

  @Override
  public String toSql(EntityDetail<?> entityDetail) {
    ParameterBinder pb = entityDetail.getParameterBinderByBindingPath(this.bindingPath);
    throwParameterBinderNotFound(pb, entityDetail.getEntityClass(), this.bindingPath);

    StringBuilder sb = new StringBuilder();
    if (Comparison.isNotNull(pb)) {

      sb.append('(');
      sb.append("UPPER(");//
      sb.append(pb.getColumnNameAnyJoinOrColumn());
      sb.append(')');//
      sb.append('=');
      sb.append("UPPER(");//
      sb.append(':'); // TODO should probably use
                      // EntityUtility.getBindingCharacter() based on the
                      // database source?? .NET is unique per database type
      sb.append(pb.getBindingPathSQL());
      sb.append(')');//
      sb.append(')');
    }

    return sb.toString();
  }

}
