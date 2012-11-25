package com.p5solutions.search;

import java.util.List;

import com.p5solutions.search.FilterChain.Operator;
import com.p5solutions.search.FilterCriteriaCondition.Condition;

/**
 * The Interface Filter.
 */
public interface Filter {

  /**
   * New filter.
   *
   * @return the filter
   */
  Filter newFilter();
  
  /**
   * @return
   */
  List<FilterJunctionComposite> getFilterJunctions();
  
  /**
   * @param junctions
   */
  void setFilterJunctions(List<FilterJunctionComposite> junctions);
  
  /**
   * Adds the filter.
   *
   * @param filter the filter
   * @param op the op
   */
  void addFilter(Filter filter, Operator op);
  
  /**
   * Adds the filter.
   *
   * @param composite the composite
   */
  void addFilter(FilterJunctionComposite composite);
  
  /**
   * Copy.
   *
   * @param filter the filter
   */
  void copy(Filter filter);
  
  /**
   * Gets the filter source accessor name.
   *
   * @return the filter source accessor name
   */
  String getFilterSourceAccessorName();
  
  /**
   * Gets the filter source accessor.
   *
   * @return the filter source accessor
   */
  FilterSourceAccessor getFilterSourceAccessor();
  
  /**
   * Gets the filter source accessor class.
   *
   * @return the filter source accessor class
   */
  Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass();
  
  
  /**
   * Sets the filter source accessor class.
   *
   * @param filtfilterSourceAccessorClasserSourceClass the new filter source accessor class
   */
  void setFilterSourceAccessorClass(Class<? extends FilterSourceAccessor> filtfilterSourceAccessorClasserSourceClass);
  
  /**
   * Gets the filter utility.
   *
   * @return the filter utility
   */
  FilterUtility getFilterUtility();
  
  /**
   * Sets the filter utility.
   *
   * @param filterUtility the new filter utility
   */
  void setFilterUtility(FilterUtility filterUtility);
  
  /**
   * Initialize.
   */
  void initialize();
 
  /**
   * Gets the column.
   *
   * @return the column
   */
  FilterCriteriaColumn getColumn();
  
  /**
   * Sets the column.
   *
   * @param column the new column
   */
  void setColumn(FilterCriteriaColumn column);
  
  /**
   * Gets the join column.
   * 
   * @return the join column
   */
  FilterCriteriaColumn getJoinColumn();

  /**
   * Sets the join column.
   * 
   * @param joinColumn
   *          the new join column
   */
  void setJoinColumn(FilterCriteriaColumn joinColumn);
  
  /**
   * Gets the column name.
   *
   * @return the column name
   */
  String getColumnName();
  
  /**
   * Gets the value.
   *
   * @return the value
   */
  FilterCriteriaCondition getValue();
  
  /**
   * Sets the value.
   *
   * @param value the new value
   */
  void setValue(FilterCriteriaCondition value);
  
  /**
   * Sets the value.
   *
   * @param value the value
   * @param condition the condition
   */
  void setValue(Object value, Condition condition);
	
  /**
   * Sets the value between.
   *
   * @param value1 the value1
   * @param value2 the value2
   */
  void setValueBetween(Object value1, Object value2);
  
  /**
   * Sets the values.
   *
   * @param values the values
   * @param condition the condition
   */
  void setValues(List<Object> values, Condition condition);
  
  /**
   * Gets the alias.
   *
   * @return the alias
   */
  String getSourceAlias();
}