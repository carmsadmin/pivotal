package com.p5solutions.search;

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
   * Copy.
   *
   * @param filter the filter
   */
  void copy(Filter filter);
  
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
}