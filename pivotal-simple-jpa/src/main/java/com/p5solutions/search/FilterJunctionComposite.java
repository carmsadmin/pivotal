/*
 * 
 */
package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.RandomCharacterGenerator;
import com.p5solutions.search.FilterChain.Operator;

/**
 * The Class FilterJunction.
 * 
 * @author Kasra Rasaee
 * @since 2012-11-13
 */
public class FilterJunctionComposite {

  /** The junctions. */
  private List<FilterJunction> junctions;

  /**
   * Instantiates a new filter junction composite.
   */
  public FilterJunctionComposite() {
    super();
  }
  

  
  /**
   * Instantiates a new filter junction.
   * 
   * @param filter
   *          the filter
   * @param op
   *          the op
   * @param order
   *          the order
   */
  public FilterJunctionComposite(Filter filter, Operator op) {
    addJunction(filter, op);
  }

  /**
   * Add a new Junction to this Filter Junction e.g. (X = :x1 OR X != x2 OR Y =
   * :y1)
   * 
   * @param filter
   * @param op
   */
  public void addJunction(Filter filter, Operator op) {
    if (junctions == null) {
      junctions = new ArrayList<FilterJunction>();
    }
    junctions.add(new FilterJunction(filter, op));
  }

  /**
   * Gets the filter.
   * 
   * @return the filter
   */
  public Filter getFilter(int index) {
    return junctions.get(index).getFilter();
  }

  /**
   * Gets the op.
   * 
   * @return the op
   */
  public Operator getOp(int index) {
    return junctions.get(index).getOp();
  }

  /**
   * Checks if is or.
   * 
   * @return the boolean
   */
  public Boolean isOr(int index) {
    return getOp(index).equals(Operator.OR);
  }

  /**
   * Checks if is and.
   * 
   * @return the boolean
   */
  public Boolean isAnd(int index) {
    return getOp(index).equals(Operator.AND);
  }

  /**
   * Checks if is not.
   * 
   * @return the boolean
   */
  public Boolean isNot(int index) {
    return getOp(index).equals(Operator.NOT);
  }

  /**
   * Checks if is void.
   * 
   * @return the boolean
   */
  public Boolean isVoid(int index) {
    return getOp(index).equals(Operator.VOID);
  }

  /**
   * Junction size.
   * 
   * @return the int
   */
  public int junctionSize() {
    return junctions.size();
  }

  /**
   * Gets the filter junctions.
   * 
   * @return the filter junctions
   */
  public List<FilterJunction> getFilterJunctions() {
    return junctions;
  }

  /**
   * Gets the junction.
   * 
   * @param index
   *          the index
   * @return the junction
   */
  public FilterJunction getJunction(int index) {
    return junctions.get(index);
  }
  
  @Override
  @Transient
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (Comparison.isNotEmptyOrNull(junctions)) {
      for (FilterJunction junction : this.junctions) {
        sb.append("\n >>> junction: " + junction.toString());
      }
    }
    return sb.toString();
  }
}