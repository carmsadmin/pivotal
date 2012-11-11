package com.p5solutions.search;

import java.util.List;

import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.search.FilterCriteriaCondition.Condition;

public class FilterCriteria implements Filter {
  
  private FilterUtility filterUtility;
  private FilterCriteriaColumn column;
  private FilterCriteriaCondition value;  
  
  // TODO switch these to class types vs. names.
  private String filterName;
  private Class<? extends FilterSourceAccessor> filterSourceAccessorClass;

  public FilterCriteria() {
    super();
  }
  
  @Override
  public Filter newFilter() {
    @SuppressWarnings("unchecked")
    Class<FilterCriteria> clazz = (Class<FilterCriteria>)getClass();
    FilterCriteria filter = ReflectionUtility.newInstance(clazz);
    copy(filter);
    return filter;
  }
  
  @Override
  public void copy(Filter filter) {
    FilterCriteria criteria = (FilterCriteria)filter;
    criteria.column = column;
    criteria.value = value;
    criteria.filterName = filterName;
    criteria.filterSourceAccessorClass = filterSourceAccessorClass;
    criteria.filterUtility = filterUtility;
  }
  
  @Override
  public void initialize() {
    // TODO logger.debug("nothing to initialize on base filter criteria");
  }
  
  public FilterCriteriaColumn getColumn() {
    return column;
  }
  
  public void setColumn(FilterCriteriaColumn column) {
    this.column = column;
  }
  
  public FilterCriteriaCondition getValue() {
    return value;
  }
  
  public void setValue(FilterCriteriaCondition value) {
    this.value = value;
  }
  

  public FilterSourceAccessor getFilterSourceAccessor() {
    return filterUtility.getFilterSourceAccessor(this.filterSourceAccessorClass); 
  }
  
  public String getFilterName() {
    return filterName;
  }
  
  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
  
  public Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass() {
    return this.filterSourceAccessorClass;
  }
  
  @Override
  public void setFilterSourceAccessorClass(Class<? extends FilterSourceAccessor> filterSourceAccessorClass) {
    this.filterSourceAccessorClass = filterSourceAccessorClass;
  }
  
  public String getFilterSourceName() {
    return this.filterSourceAccessorClass.getName();
  }
  
  @SuppressWarnings("unchecked")
  public void setFilterSourceName(String filterSourceName) {
    try {
      this.filterSourceAccessorClass = (Class<? extends FilterSourceAccessor>)Class.forName(filterSourceName);
    } catch (ClassNotFoundException e) {
      return;
    }
  }
  
  public FilterUtility getFilterUtility() {
    return filterUtility;
  }

  public void setFilterUtility(FilterUtility filterUtility) {
    this.filterUtility = filterUtility;
  }

  public void setValue(Object value, Condition condition) {
    FilterCriteriaValue v = new FilterCriteriaValue(value);
    FilterCriteriaCondition c = new FilterCriteriaCondition(condition, v);
    this.value = c;
  }
  
  public void setValues(List<Object> values, Condition condition) {
    FilterCriteriaValue v = new FilterCriteriaValue(values);
    FilterCriteriaCondition c = new FilterCriteriaCondition(condition, v);
    this.value = c;
  }
}