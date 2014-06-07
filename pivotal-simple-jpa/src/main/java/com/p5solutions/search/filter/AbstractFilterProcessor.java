package com.p5solutions.search.filter;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA. User: sophanara Date: 2013-10-10 Time: 3:56 PM To change this template use File |
 * Settings | File Templates.
 */
public abstract class AbstractFilterProcessor implements FilterProcessor {
  protected Stack<FilterResult> stack = new Stack();

  @Override
  public FilterResult pop() {
    return stack.pop();
  }

  @Override
  public void push(FilterResult filterResult) {
    stack.push(filterResult);
  }

  @Override
  public boolean isEmptyStack() {
    return stack.empty();
  }

  @Override
  public FilterResult applyOperator(FilterResult operand1, FilterResult operand2, Operator operator) {
    return operand1.applyOperation(operator, operand2);
  }

  @Override
  public FilterResult applyOperator(FilterResult operand1, Operator operator) {
    return operand1.applyOperation(operator, null);
  }
}
