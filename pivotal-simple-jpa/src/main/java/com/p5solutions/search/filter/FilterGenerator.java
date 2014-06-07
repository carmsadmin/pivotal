package com.p5solutions.search.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sophanara
 * Date: 2013-10-14
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FilterGenerator <M extends FilterResult> {

    /**
     * Generate result.
     *
     * @param chain the chain
     * @return the filter generator result
     */
    M generateResult(FilterChain chain);
}
