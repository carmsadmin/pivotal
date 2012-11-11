package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

/**
 * FilterCriteriaValue:
 * 
 * @author krasaee
 *
 */
public class FilterCriteriaValue implements Filter {
  private List<Object> values;

  public FilterCriteriaValue(Object value) {
    values = new ArrayList<Object>();
    values.add(value);
  }

  public List<Object> getValues() {
    return values;
  }

  public void setValues(List<Object> values) {
    this.values = values;
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
}