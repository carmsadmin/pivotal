package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

/**
 * The Class FilterChain.
 * 
 * @author Kasra Rasaee (krasaee)
 * 
 */
public class FilterChain implements Filter {

  /** The filter utility. */
  private FilterUtility filterUtility;
  
  /**
   * The Enum Operator.
   */
  public enum Operator {

    /** The and. */
    AND,
    /** The or. */
    OR,
    /** The not. */
    NOT
  }

  /**
   * The Class FilterJunction.
   */
  protected class FilterJunction {

    /** The filter. */
    Filter filter;

    /** The op. */
    Operator op;
  }

  /** The filters. */
  public List<FilterJunction> filters;

  /**
   * Adds the filter.
   * 
   * @param filter
   *          the filter
   * @param op
   *          the op
   */
  public void addFilter(Filter filter, Operator op) {
    FilterJunction junction = new FilterJunction();
    junction.filter = filter;
    junction.op = op;

    if (filters == null) {
      filters = new ArrayList<FilterJunction>();
    }

    filters.add(junction);
  }

  /**
   * @see com.p5solutions.search.Filter#newFilter()
   */
  @Override
  public Filter newFilter() {
    throw new NotImplementedException(
        "Not yet implemented, but should technically clone all filters associated to this filter chain and return a new filter chain");
  }

  /**
   * @see com.p5solutions.search.Filter#copy(com.p5solutions.search.Filter)
   */
  @Override
  public void copy(Filter filter) {
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
}
