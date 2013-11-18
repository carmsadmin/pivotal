package com.p5solutions.search.filter;

import java.util.ArrayList;
import java.util.List;

import com.p5solutions.core.json.JsonTransient;

/**
 * FilterChain implementation.
 * 
 * @User: sophanara
 * @Date: 2013-10-08
 * @Time: 12:03 AM
 * 
 */
public class FilterChainImpl implements FilterChain {
  /** The presentation name. */
  private String presentationName;
  private Long filterChainId;
  /**
   * The filters.
   */
  private List<FilterElement> filterElements;
  private String[] returnColumns;

  /**
   * 
   * @return
   */
  @JsonTransient
  public List<FilterElement> getFilterElements() {
    return filterElements;
  }

  /**
   * 
   * @param filterElements
   */
  public void setFilterElements(List<FilterElement> filterElements) {
    this.filterElements = filterElements;
  }

  /**
   * 
   * @param criteria
   */
  @Override
  public void addFilter(FilterElement criteria) {
    if (filterElements == null) {
      filterElements = new ArrayList<FilterElement>();
    }
    filterElements.add(criteria);
  }

  /**
   * 
   * @param chain
   */
  @Override
  public void addFilter(FilterChainImpl chain) {
    if (filterElements == null) {
      filterElements = new ArrayList<FilterElement>();
    }
    filterElements.add(Bracket.LEFT);
    filterElements.add(chain);
    filterElements.add(Bracket.RIGHT);
  }

  /**
   * 
   * @param chain
   * @param operator
   */
  @Override
  public void addFilterChain(FilterChainImpl chain, Operator operator) {
    if (operator == null) {
      throw new RuntimeException("Operator cannot be null");
    }
    if (operator.equals(Operator.IN)) {
      throw new RuntimeException("IN Operator is not supported");
    }

    if (filterElements == null) {
      filterElements = new ArrayList<FilterElement>();
    }
    filterElements.add(operator);
    filterElements.add(Bracket.LEFT);
    filterElements.add(chain);
    filterElements.add(Bracket.RIGHT);
  }

  /**
   * 
   * @return
   */
  public String getPresentationName() {
    return presentationName;
  }

  /**
   * 
   * @param presentationName
   */
  public void setPresentationName(String presentationName) {
    this.presentationName = presentationName;
  }

  /**
   * 
   * @return
   */
  public Long getFilterChainId() {
    return filterChainId;
  }

  /**
   * 
   * @param filterChainId
   */
  public void setFilterChainId(Long filterChainId) {
    this.filterChainId = filterChainId;
  }

  /**
   * 
   * @return
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    if (getFilterElements() != null) {
      for (FilterElement filterElement : getFilterElements()) {
        buffer.append(filterElement + " ");
      }
    }

    return buffer.toString();
  }

  /**
   * Gets the return columns.
   * 
   * @return the return columns
   */
  @Override
  public String[] getReturnColumns() {
    return this.returnColumns;
  }

  /**
   * Sets the return columns.
   * 
   * @param returnColumns
   *          the new return columns
   */
  public void setReturnColumns(String[] returnColumns) {
    this.returnColumns = returnColumns;
  }

}
