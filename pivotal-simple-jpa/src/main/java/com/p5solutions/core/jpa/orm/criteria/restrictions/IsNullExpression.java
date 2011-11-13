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

public class IsNullExpression extends AbstractRestriction implements Criterion {

	private String bindingPath;

	public IsNullExpression(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	@Override
	public String toSql(EntityDetail<?> entityDetail) {
		ParameterBinder pb = entityDetail.getParameterBinderByBindingPath(bindingPath);
		throwParameterBinderNotFound(pb, entityDetail.getEntityClass(), bindingPath);
		
		StringBuilder sb = new StringBuilder();
		if (Comparison.isNotNull(pb)) {
			sb.append('(');
			sb.append(pb.getColumnNameAnyJoinOrColumn());
			sb.append(" IS NULL)");
		}

		return sb.toString();
	}

	public void addQueryCriteriaToQuery(EntityDetail<?> entityDetail, Query query) {
		query.addQueryCriteria(bindingPath, null);
	}

	public List<SQLParameterCriteria> getQueryCriterias(EntityDetail<?> entityDetail) {
		throw new NotImplementedException("This method is not applicable for Simple Expressions");
	}
}
