package com.p5solutions.search;

/**
 * The Class FilterCriteriaCondition.
 */
public class FilterCriteriaCondition {

  /**
   * The Enum Condition.
   */
  public enum Condition {

    /** The in. */
    IN,
    /** The like. */
    LIKE,
    /** The eq. */
    EQ,
    /** The neq. */
    NEQ,
    /** The less. */
    LESS,
    /** The less eq. */
    LESS_EQ,
    /** The greater. */
    GREATER,
    /** The greater eq. */
    GREATER_EQ,
    /** The regexp. */
    REGEXP,
    /** The between. */
    BETWEEN,
    /** The null. */
    NULL
  }

  /** The condition. */
  private Condition condition;

  /** The value. */
  private FilterCriteriaValue value;

  
  /**
   * Instantiates a new filter criteria condition.
   * 
   * @param condition
   *          the condition
   */
  public FilterCriteriaCondition(Condition condition) {
    this.condition = condition;
  }

  /**
   * Instantiates a new filter criteria condition.
   * 
   * @param condtition
   *          the condtition
   * @param value
   *          the value
   */
  public FilterCriteriaCondition(Condition condtition, FilterCriteriaValue value) {
    this.condition = condtition;
    this.value = value;
  }

  /**
   * Gets the condition.
   * 
   * @return the condition
   */
  public Condition getCondition() {
    return condition;
  }

  /**
   * Sets the condition.
   * 
   * @param condtition
   *          the new condition
   */
  public void setCondition(Condition condtition) {
    this.condition = condtition;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public FilterCriteriaValue getValue() {
    return value;
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          the new value
   */
  public void setValue(FilterCriteriaValue value) {
    this.value = value;
  }

  /**
   * Checks if is null.
   * 
   * @return the boolean
   */
  public Boolean isNull() {
    return (Condition.NULL.equals(condition));
  }

  /**
   * Checks if is equal.
   * 
   * @return the boolean
   */
  public Boolean isEqual() {
    return (Condition.EQ.equals(condition));
  }

  /**
   * Checks if is not equal.
   * 
   * @return the boolean
   */
  public Boolean isNotEqual() {
    return (Condition.NEQ.equals(condition));
  }

  /**
   * Checks if is in.
   * 
   * @return the boolean
   */
  public Boolean isIn() {
    return (Condition.IN.equals(condition));
  }

  /**
   * Checks if is like.
   * 
   * @return the boolean
   */
  public Boolean isLike() {
    return (Condition.LIKE.equals(condition));
  }

  /**
   * Checks if is less than.
   * 
   * @return the boolean
   */
  public Boolean isLess() {
    return (Condition.LESS.equals(condition));
  }

  /**
   * Checks if is less or equal.
   * 
   * @return the boolean
   */
  public Boolean isLessOrEqual() {
    return (Condition.LESS_EQ.equals(condition));
  }

  /**
   * Checks if is greater.
   * 
   * @return the boolean
   */
  public Boolean isGreater() {
    return (Condition.GREATER.equals(condition));
  }

  /**
   * Checks if is greater or equal.
   * 
   * @return the boolean
   */
  public Boolean isGreaterOrEqual() {
    return (Condition.GREATER_EQ.equals(condition));
  }

  /**
   * Checks if is between.
   * 
   * @return the boolean
   */
  public Boolean isBetween() {
    return (Condition.BETWEEN.equals(condition));
  }

  /**
   * Checks if is neq.
   * 
   * @return the boolean
   */
  public Boolean isNEQ() {
    return (Condition.NEQ.equals(condition));
  }

  public String hasBasicCondition() {
    if (this.isEqual()) {
      return " = ";
    } else if (this.isNotEqual()) {
      return " != ";
    } else if (this.isLess()) {
      return" < ";
    } else if (this.isLessOrEqual()) {
      return" <= ";
    } else if (this.isGreater()) {
      return " > ";
    } else if (this.isGreaterOrEqual()) {
      return " >= ";
    }
    return null;
  }
}
