package com.p5solutions.search.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sophanara
 * Date: 2013-10-10
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FilterResult {
    FilterResult applyOperation(Operator operator, FilterResult operand2);

}
