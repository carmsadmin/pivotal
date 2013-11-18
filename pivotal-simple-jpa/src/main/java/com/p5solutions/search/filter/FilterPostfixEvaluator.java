package com.p5solutions.search.filter;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: sophanara Date: 2013-10-10 Time: 9:31 AM
 */
public class FilterPostfixEvaluator {

  public static FilterResult evaluate(List<FilterElement> filterElements, FilterProcessor processor) {

    for (FilterElement filterElement : filterElements) {
      if (FilterUtility.isOperand(filterElement)) {
        processor.push(filterElement);
      }
      if (FilterUtility.isOperator(filterElement)) {
        FilterResult operand2 = processor.pop();

        Operator operator = (Operator) filterElement;
        if (FilterUtility.isOperatorNOT(filterElement)) {
          // NOT or IN Operator #todo IN is not supported yet
          FilterResult filterResult = processor.applyOperator(operand2, operator);
          processor.push(filterResult);
        } else {
          FilterResult operand1 = processor.pop();
          FilterResult filterResult = processor.applyOperator(operand1, operand2, operator);
          processor.push(filterResult);
        }

      }
    }

    FilterResult result = processor.pop();
    return result;
  }
}
