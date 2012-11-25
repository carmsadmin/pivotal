package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.p5solutions.core.utils.Comparison;
import com.p5solutions.search.FilterChain.Operator;
import com.p5solutions.search.FilterCriteriaCondition.Condition;

/**
 * FilterCriteriaValue:
 * 
 * @author krasaee
 *
 */
public class FilterCriteriaValue implements Filter {
  private List<Object> values;
  
  public FilterCriteriaValue(List<Object> values) {
    this.values = values;
  }
  
  public FilterCriteriaValue(Object...values) {
    this.values = new ArrayList<Object>();
    for (Object value : values) {
      this.values.add(value);    
    }
  }

  public Object getActualValue() {
    if (Comparison.isEmptyOrNull(values)) {
      return null;
    }
    
    if (values.size() > 1) {
      // TODO output all values
      throw new RuntimeException("There are too many results within the array, expected only one value, got " + values.size() + " on filter " + this.getClass());
    }
    
    return values.get(0);
  }
  
  public List<Object> getValues() {
    return values;
  }

  public void setValues(List<Object> values) {
    this.values = values;
  }

  public Object getValue(int index) {
    return this.values.get(index);
  }
  
  
  @Override
  public Filter newFilter() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void copy(Filter filter) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  @Override
  public void setFilterSourceAccessorClass(Class<? extends FilterSourceAccessor> filterSourceAccessorClass) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public FilterUtility getFilterUtility() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  @Override
  public void setFilterUtility(FilterUtility filterUtility) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void initialize() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public FilterCriteriaColumn getColumn() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void setColumn(FilterCriteriaColumn column) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public String getColumnName() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public FilterCriteriaCondition getValue() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void setValue(FilterCriteriaCondition value) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void setValue(Object value, Condition condition) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void setValues(List<Object> values, Condition condition) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public FilterSourceAccessor getFilterSourceAccessor() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void setValueBetween(Object value1, Object value2) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public String getSourceAlias() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public FilterCriteriaColumn getJoinColumn() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  @Override
  public void setJoinColumn(FilterCriteriaColumn joinColumn) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public String getFilterSourceAccessorName() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public List<FilterJunctionComposite> getFilterJunctions() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void setFilterJunctions(List<FilterJunctionComposite> junctions) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void addFilter(Filter filter, Operator op) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  @Override
  public void addFilter(FilterJunctionComposite composite) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }
}