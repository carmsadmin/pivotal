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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.SQLParameterCriteria;

public class AbstractRestriction {
  protected Hashtable<String, Object> values = new Hashtable<String, Object>();

  public AbstractRestriction() {
    super();
  }

  protected void throwParameterBinderNotFound(ParameterBinder pb, Class<?> clazz, String name) {
    if (pb == null) {
      throw new NullPointerException("No parameter binder found for given name/path " + name + " under entity class type " + clazz);
    }
  }

  public void addQueryCriteriaToQuery(EntityDetail<?> entityDetail, Query query) {
    List<SQLParameterCriteria> qcs = getQueryCriterias(entityDetail);
    for (SQLParameterCriteria qc : qcs) {
      query.addQueryCriteria(qc);
    }
  }

  public List<SQLParameterCriteria> getQueryCriterias(EntityDetail<?> entityDetail) {
    List<ParameterBinder> pks = entityDetail.getPrimaryKeyParameterBinders();
    List<SQLParameterCriteria> qcs = new ArrayList<SQLParameterCriteria>();

    for (ParameterBinder pb : pks) {
      SQLParameterCriteria qc = new SQLParameterCriteria();
      qc.setBindingPath(pb.getBindingPathSQL());
      qc.setBindingType(pb.getTargetValueType());

      // TODO doesn't support index, only parameters bound by name.

      // get the value based on the binding name
      Object value = values.get(pb.getBindingPath());
      if (value == null) {
        value = values.get("DEFAULT_FIRST_ID");
        values.remove("DEFAULT_FIRST_ID");
      } else {
        values.remove(pb.getBindingPath());
      }

      qc.setValue(value);
      qcs.add(qc);
    }
    return qcs;
  }

}