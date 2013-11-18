package com.p5solutions.search.filter.database;

import com.p5solutions.search.filter.*;

/**
 * Created with IntelliJ IDEA.
 * User: sophanara
 * Date: 2013-10-10
 * Time: 3:52 PM To change this template use File |
 * Settings | File Templates.
 */
public class TableFilterProcessor extends AbstractFilterProcessor {

  @Override
  public void push(FilterElement filterElement) {
    if (filterElement instanceof TableCriteria) {
      push(new TableFilterGeneratorResult((TableCriteria) filterElement));
      return;
    }
    throw new RuntimeException("Filter of type: " + filterElement.getClass() + " is not supported.");
  }

  @Override
  public FilterResult applyOperator(FilterResult operand1, FilterResult operand2, Operator operator) {
    ((TableFilterGeneratorResult) operand1).getWhereClauseList().add(0, "(");
    operand1 = operand1.applyOperation(operator, operand2);
    ((TableFilterGeneratorResult) operand1).getWhereClauseList().add(")");
    return operand1;
  }

  @Override
  public FilterResult applyOperator(FilterResult operand1, Operator operator) {
    return operand1.applyOperation(operator, null);
  }
}
