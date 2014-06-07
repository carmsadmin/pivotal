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

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.SQLParameterCriteria;
import com.p5solutions.core.utils.Comparison;

public class SimpleExpression extends AbstractRestriction implements Criterion {

  protected String bindingPath;
  protected Object value;

  public SimpleExpression(String bindingPath, Object value) {
    this.bindingPath = bindingPath;
    this.value = value;
  }

  @Override
  public String toSql(EntityDetail<?> entityDetail) {
    ParameterBinder pb = entityDetail.getParameterBinderByBindingPath(this.bindingPath);
    throwParameterBinderNotFound(pb, entityDetail.getEntityClass(), bindingPath);

    StringBuilder sb = new StringBuilder();
    if (Comparison.isNotNull(pb)) {

      sb.append('(');
      sb.append(pb.getColumnNameAnyJoinOrColumn());
      sb.append('=');
      sb.append(':');
      sb.append(pb.getBindingPathSQL());
      sb.append(')');
    }

    return sb.toString();
  }

  @Override
  public void addQueryCriteriaToQuery(EntityDetail<?> entityDetail, Query query) {
    query.addQueryCriteria(bindingPath, value);
  }

  @Override
  public List<SQLParameterCriteria> getQueryCriterias(EntityDetail<?> entityDetail) {
    throw new NotImplementedException("This method is not applicable for Simple Expressions");
  }
}
