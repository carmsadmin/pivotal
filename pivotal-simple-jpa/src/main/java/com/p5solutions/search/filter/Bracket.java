package com.p5solutions.search.filter;

/**
 * Created with IntelliJ IDEA. User: sophanara Date: 2013-10-08 Time: 2:59 PM To change this template use File |
 * Settings | File Templates.
 */
public enum Bracket implements FilterElement {

  LEFT("("), RIGHT(")");
  public final static String LEFT_BRACKET = "(";
  public final static String RIGHT_BRACKET = ")";
  protected String value;

  Bracket(String s) {
    value = s;
  }

  public static Bracket create(String value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    for (Bracket v : values()) {
      if (value.equals(v.getValue())) {
        return v;
      }
    }
    throw new IllegalArgumentException();
  }

  @Override
  public String toString() {
    return this.getValue();
  }

  public String getValue() {
    return value;
  }
}
