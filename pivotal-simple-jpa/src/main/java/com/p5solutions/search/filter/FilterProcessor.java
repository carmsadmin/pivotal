package com.p5solutions.search.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sophanara
 * Date: 2013-10-10
 * Time: 10:26 AM
 *
 */
public interface FilterProcessor {
    void push(FilterElement filterElement);

    FilterResult pop();

    FilterResult applyOperator(FilterResult operand1, FilterResult operand2, Operator operator);

    void push(FilterResult filterResult);

    boolean isEmptyStack();

    FilterResult applyOperator(FilterResult operand1, Operator operator);
}


