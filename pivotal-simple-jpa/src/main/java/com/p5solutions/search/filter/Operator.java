package com.p5solutions.search.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sophanara
 * Date: 2013-10-07
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Operator implements FilterElement {

    /** The and. */
    AND,
    /** The or. */
    OR,
    /** The not. */
    NOT,
    /** The IN operator */
    IN;
}
