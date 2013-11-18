package com.p5solutions.search.filter;

import java.util.List;

/**
 * @author: smin
 * @Date: 10/27/13
 * @Time: 6:28 PM
 */
public interface FilterChain<CHAIN_STATE extends FilterChainStorageState> extends FilterElement {
  /**
   * return the filter chain id.
   * 
   * @return
   */
  Long getFilterChainId();

  /**
   * set the filter chain id.
   * 
   * @param filterChainId
   */
  void setFilterChainId(Long filterChainId);

  /**
   * 
   * @param criteria
   */
  void addFilter(FilterElement criteria);

  /**
   * 
   * @param chain
   * @param operator
   */
  void addFilterChain(FilterChainImpl chain, Operator operator);

  /**
   * 
   * @param chain
   */
  public void addFilter(FilterChainImpl chain);

  /**
   * 
   * @return
   */
  List<FilterElement> getFilterElements();

  /**
   * 
   * @return
   */
  String[] getReturnColumns();
}
