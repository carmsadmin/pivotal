package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.p5solutions.core.utils.Comparison;
import com.p5solutions.search.FilterChain.Operator;
import com.p5solutions.search.FilterCriteriaCondition.Condition;

/**
 * FilterCriteriaValue:.
 * 
 * @author krasaee
 */
public class FilterCriteriaValue implements Filter<FilterStorageState> {

  /** The values. */
  private List<Object> values;

  /**
   * Instantiates a new filter criteria value.
   * 
   * @param values
   *          the values
   */
  public FilterCriteriaValue(List<Object> values) {
    this.values = values;
  }

  /**
   * Instantiates a new filter criteria value.
   * 
   * @param values
   *          the values
   */
  public FilterCriteriaValue(Object... values) {
    this.values = new ArrayList<Object>();
    for (Object value : values) {
      this.values.add(value);
    }
  }

  /**
   * Gets the actual value.
   * 
   * @return the actual value
   */
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

  /**
   * Gets the values.
   * 
   * @return the values
   */
  public List<Object> getValues() {
    return values;
  }

  /**
   * Sets the values.
   * 
   * @param values
   *          the new values
   */
  public void setValues(List<Object> values) {
    this.values = values;
  }

  /**
   * Gets the value.
   * 
   * @param index
   *          the index
   * @return the value
   */
  public Object getValue(int index) {
    return this.values.get(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#newFilter()
   */
  @Override
  public Filter<FilterStorageState> newFilter() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#copy(com.p5solutions.search.Filter)
   */
  @Override
  public void copy(Filter<FilterStorageState> filter) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterSourceAccessorClass()
   */
  @Override
  public Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.search.Filter#setFilterSourceAccessorClass(java.lang.Class)
   */
  @Override
  public void setFilterSourceAccessorClass(Class<? extends FilterSourceAccessor> filterSourceAccessorClass) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterUtility()
   */
  @Override
  public FilterUtility getFilterUtility() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setFilterUtility(com.p5solutions.search.
   * FilterUtility)
   */
  @Override
  public void setFilterUtility(FilterUtility filterUtility) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#initialize()
   */
  @Override
  public void initialize() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getColumn()
   */
  @Override
  public FilterCriteriaColumn getColumn() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setColumn(com.p5solutions.search.
   * FilterCriteriaColumn)
   */
  @Override
  public void setColumn(FilterCriteriaColumn column) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getColumnName()
   */
  @Override
  public String getColumnName() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getValue()
   */
  @Override
  public FilterCriteriaCondition getValue() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValue(com.p5solutions.search.
   * FilterCriteriaCondition)
   */
  @Override
  public void setValue(FilterCriteriaCondition value) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValue(java.lang.Object,
   * com.p5solutions.search.FilterCriteriaCondition.Condition)
   */
  @Override
  public void setValue(Object value, Condition condition) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValues(java.util.List,
   * com.p5solutions.search.FilterCriteriaCondition.Condition)
   */
  @Override
  public void setValues(List<Object> values, Condition condition) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterSourceAccessor()
   */
  @Override
  public FilterSourceAccessor getFilterSourceAccessor() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValueBetween(java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void setValueBetween(Object value1, Object value2) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getSourceAlias()
   */
  @Override
  public String getSourceAlias() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getJoinColumn()
   */
  @Override
  public FilterCriteriaColumn getJoinColumn() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setJoinColumn(com.p5solutions.search.
   * FilterCriteriaColumn)
   */
  @Override
  public void setJoinColumn(FilterCriteriaColumn joinColumn) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterSourceAccessorName()
   */
  @Override
  public String getFilterSourceAccessorName() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterJunctions()
   */
  @Override
  public List<FilterJunctionComposite> getFilterJunctions() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setFilterJunctions(java.util.List)
   */
  @Override
  public void setFilterJunctions(List<FilterJunctionComposite> junctions) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#addFilter(com.p5solutions.search.Filter,
   * com.p5solutions.search.FilterChain.Operator)
   */
  @Override
  public void addFilter(Filter<? extends FilterStorageState> filter, Operator op) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#addFilter(com.p5solutions.search.
   * FilterJunctionComposite)
   */
  @Override
  public void addFilter(FilterJunctionComposite composite) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterState()
   */
  @Override
  public FilterState getFilterState() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterType()
   */
  @Override
  public String getFilterType() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.FilterDisplay#getPresentationName()
   */
  @Override
  public String getPresentationName() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.search.FilterDisplay#setPresentationName(java.lang.String)
   */
  @Override
  public void setPresentationName(String name) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.FilterDisplay#getFilterId()
   */
  @Override
  public Long getFilterId() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.FilterDisplay#setFilterId(java.lang.Long)
   */
  @Override
  public void setFilterId(Long id) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.FilterDisplay#getFilterGroupId()
   */
  @Override
  public Long getFilterGroupId() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.FilterDisplay#setFilterGroupId(java.lang.Long)
   */
  @Override
  public void setFilterGroupId(Long filterGroupId) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#retrieveFilterStorageState()
   */
  @Override
  public FilterStorageState retrieveFilterStorageState() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /**
   * @see com.p5solutions.search.Filter#initializeFromFilterStorageState(com.p5solutions
   *      .search.FilterStorageState)
   */
  @Override
  public void initializeFromFilterStorageState(FilterStorageState state) {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }

  /**
   * @see com.p5solutions.search.FilterDisplay#getResolvedDescription()
   */
  @Override
  public String getResolvedDescription() {
    throw new NotImplementedException("Filter Criteria's could potentially be filters themselves?? however this has yet to be implemented");
  }
}