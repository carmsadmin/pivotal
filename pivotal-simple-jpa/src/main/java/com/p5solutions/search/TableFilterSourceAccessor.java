package com.p5solutions.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.ParameterBinder;

public class TableFilterSourceAccessor extends AbstractFilterSource implements FilterSourceAccessor {

  private EntityDetail<?> source;
 
  private Map<String, FilterCriteriaColumn> columns;
  
  public TableFilterSourceAccessor(EntityDetail<?> source) {
    this.source = source;
  }
  
  @Override
  public String getName() {
    return source.getTableName();
  }
  
  public void setup() {
    
    columns = new HashMap<String, FilterCriteriaColumn>();
    
    List<ParameterBinder> binders = source.getParameterBinders();
    for (ParameterBinder binder : binders) {
      String columnName = binder.getColumnNameAnyJoinOrColumn();
      FilterCriteriaColumn column = new FilterCriteriaColumn();
      column.setName(columnName);
      column.setBinder(binder);
      columns.put(columnName, column);
    } 
  }
  
  public FilterCriteriaColumn findColumn(String name) {
    FilterCriteriaColumn column = columns.get(name);
    return column;
  }
  
  // ????  dynamically build filters, versus injecting them from spring?? future plan perhaps.
  public void buildFilters() {
    List<ParameterBinder> binders = source.getParameterBinders();
    for (ParameterBinder binder : binders) {
      FilterCriteria criteria = new FilterCriteria();
      FilterCriteriaColumn column = new FilterCriteriaColumn();
      column.setBinder(binder);
      criteria.setColumn(column);
    }
  }
}