package com.p5solutions.search;

import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.RandomCharacterGenerator;
import com.p5solutions.search.FilterChain.Operator;

/**
 * The Class FilterJunction.
 * 
 * @author Kasra Rasaee
 * @since 2012-11-13
 */
public class FilterJunction {

  /** The id. */
  private String id;
 
  /** The filter. */
  private Filter filter;

  /** The op. */
  private Operator op;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    if (Comparison.isEmpty(id)) {
      this.id = RandomCharacterGenerator.generate(4);
    }
    
    return this.id;
  }
  
  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Instantiates a new filter junction.
   *
   * @param filter the filter
   * @param op the op
   */
  protected FilterJunction(Filter filter, Operator op) {
    this.op = op;
    this.filter = filter;
  }

  /**
   * Gets the op.
   *
   * @return the op
   */
  public Operator getOp() {
    return op;
  }

  /**
   * Gets the filter.
   *
   * @return the filter
   */
  public Filter getFilter() {
    return filter;
  }

  /**
   * Checks if is or.
   * 
   * @return the boolean
   */
  public Boolean isOr() {
    return op.equals(Operator.OR);
  }

  /**
   * Checks if is and.
   * 
   * @return the boolean
   */
  public Boolean isAnd() {
    return op.equals(Operator.AND);
  }

  /**
   * Checks if is not.
   * 
   * @return the boolean
   */
  public Boolean isNot() {
    return op.equals(Operator.NOT);
  }

  /**
   * Checks if is void.
   * 
   * @return the boolean
   */
  public Boolean isVoid() {
    return op.equals(Operator.VOID);
  }

}