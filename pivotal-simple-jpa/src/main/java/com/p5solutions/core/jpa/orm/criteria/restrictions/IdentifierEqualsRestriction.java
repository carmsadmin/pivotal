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

import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.utils.Comparison;

public class IdentifierEqualsRestriction extends AbstractRestriction implements Criterion {

	protected void addSingleIdentifierEqualsRestriction(Object id) {
		values.put("DEFAULT_FIRST_ID", id);
	}

	public IdentifierEqualsRestriction(Object... keyValue) {
		int length = keyValue.length;
		if (length == 1) {
			addSingleIdentifierEqualsRestriction(keyValue[0]);
		} else {
			double mod = length % 2;
			if (mod == 0) {
				int div = length / 2;
				for (int i = 0; i < div; i++) {
					String bindPath = (String) keyValue[i * 2];
					Object value = keyValue[i * 2 + 1];
					bindPath = ParameterBinder.getBindingPathSQL(bindPath);
					values.put(bindPath, value);
				}
			} else {
				throw new RuntimeException(
						"Key value pair length must be a key + pair. the modulas of array size "
								+ length + " by 2 = " + mod);
			}
		}
	}

	// TODO support composite

	@Override
	public String toSql(EntityDetail<?> entityDetail) {
		List<ParameterBinder> pks = entityDetail
				.getPrimaryKeyParameterBinders();

		StringBuilder sb = new StringBuilder();
		if (!Comparison.isEmptyOrNull(pks)) {
			sb.append('(');
			int i = 0;
			for (ParameterBinder pk : pks) {
				// TODO bind by index?

				sb.append(pk.getColumnNameUpper());
				sb.append('=');
				sb.append(':');
				sb.append(pk.getBindingPathSQL());

				if (++i < pks.size()) {
					sb.append(" AND ");
				}
			}
			sb.append(')');
		}

		return sb.toString();
	}
}
