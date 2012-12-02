package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.p5solutions.search.FilterCriteriaCondition.Condition;

/**
 * The Class FilterChain.
 * 
 * @author Kasra Rasaee (krasaee)
 * 
 */
// TODO reason this filter chain implement Filter is such that filter chains can
// also be used as part of another filter chains intersect/join/minus, so forth.
public class FilterChain implements Filter<FilterStorageState> {

  private String[] returnColumns;
  
  /** The filter utility. */
  private FilterUtility filterUtility;

  /**
   * Instantiates a new filter chain.
   *
   * @param returnColumns the return columns
   */
  public FilterChain(String[] returnColumns) {
    this.returnColumns = returnColumns;
  }
  
  /**
   * The Enum Operator.
   */
  public enum Operator {
    /** The first is always void **/
    VOID,
    /** The and. */
    AND,
    /** The or. */
    OR,
    /** The not. */
    NOT
  }

  /** The filters. */
  private List<FilterJunctionComposite> filterJunctions;

  /**
   * Gets the return columns.
   *
   * @return the return columns
   */
  public String[] getReturnColumns() {
    return this.returnColumns;
  }
  
  /**
   * Sets the return columns.
   *
   * @param returnColumns the new return columns
   */
  public void setReturnColumns(String[] returnColumns) {
    this.returnColumns = returnColumns;
  }
  
  /**
   * Gets the return column definitions.
   *
   * @return the return column definitions
   */
  public String getReturnColumnDefinitions() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < returnColumns.length; i++) {
      String column = returnColumns[i];
      if (i > 0) {
        sb.append(",");
      }
      sb.append(column);
    }
    return sb.toString();
  }
  
  /**
   * Gets the filter junctions.
   * 
   * @return the filter junctions
   */
  public List<FilterJunctionComposite> getFilterJunctions() {
    return filterJunctions;
  }

  /**
   * Sets the filter junctions.
   * 
   * @param filterJunctions
   *          the new filter junctions
   */
  public void setFilterJunctions(List<FilterJunctionComposite> filterJunctions) {
    this.filterJunctions = filterJunctions;
  }

  /**
   * @see com.p5solutions.search.Filter#addFilter(com.p5solutions.search.Filter,
   *      com.p5solutions.search.FilterChain.Operator)
   */
  public void addFilter(Filter<? extends FilterStorageState> filter, Operator op) {
    FilterJunctionComposite composite = new FilterJunctionComposite(filter, op);
    addFilter(composite);
  }


  /**
   * @see com.p5solutions.search.Filter#addFilter(com.p5solutions.search.FilterJunctionComposite)
   */
  @Override
  public void addFilter(FilterJunctionComposite composite) {
    if (filterJunctions == null) {
      filterJunctions = new ArrayList<FilterJunctionComposite>();
    }
    filterJunctions.add(composite);
  }

  /**
   * @see com.p5solutions.search.Filter#newFilter()
   */
  @Override
  public Filter<FilterStorageState> newFilter() {
    throw new NotImplementedException(
        "Not yet implemented, but should technically clone all filters associated to this filter chain and return a new filter chain");
  }

  /**
   * @see com.p5solutions.search.Filter#copy(com.p5solutions.search.Filter)
   */
  @Override
  public void copy(Filter<FilterStorageState> filter) {
    throw new NotImplementedException(
        "Filter sources cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  /**
   * @see com.p5solutions.search.Filter#getFilterSourceAccessorClass()
   */
  @Override
  public Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass() {
    throw new NotImplementedException(
        "Filter sources cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  /**
   * @see com.p5solutions.search.Filter#setFilterSourceAccessorClass(java.lang.Class)
   */
  @Override
  public void setFilterSourceAccessorClass(Class<? extends FilterSourceAccessor> filterSourceAccessorClass) {
    throw new NotImplementedException(
        "Filter sources cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");
  }

  @Override
  public FilterUtility getFilterUtility() {
    return filterUtility;
  }

  @Override
  public void setFilterUtility(FilterUtility filterUtility) {
    this.filterUtility = filterUtility;
  }

  @Override
  public void initialize() {
    // TODO any initializing??
  }

  @Override
  public void setValue(Object value, Condition condition) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public void setValues(List<Object> values, Condition condition) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public FilterCriteriaCondition getValue() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public void setValue(FilterCriteriaCondition value) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public FilterSourceAccessor getFilterSourceAccessor() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public FilterCriteriaColumn getColumn() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public void setColumn(FilterCriteriaColumn column) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public String getColumnName() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public void setValueBetween(Object value1, Object value2) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public String getSourceAlias() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public FilterCriteriaColumn getJoinColumn() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public void setJoinColumn(FilterCriteriaColumn joinColumn) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");
  }

  @Override
  public String getFilterSourceAccessorName() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");
  }

  @Override
  public FilterState getFilterState() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public String getFilterType() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");
  }

  @Override
  public String getPresentationName() {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public void setPresentationName(String name) {
    throw new NotImplementedException(
        "Filter values cannot be set on filter chains, but rather on the individual filter itself since they are in turn dependant on the source");

  }

  @Override
  public Long getFilterId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFilterId(Long id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Long getFilterGroupId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFilterGroupId(Long filterGroupId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public FilterStorageState retrieveFilterStorageState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void initializeFromFilterStorageState(FilterStorageState state) {
    // TODO Auto-generated method stub    
  }

  @Override
  public String getResolvedDescription() {
    // TODO Auto-generated method stub
    return null;
  }
}
