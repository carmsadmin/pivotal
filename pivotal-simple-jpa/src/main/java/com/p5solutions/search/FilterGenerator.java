package com.p5solutions.search;

/**
 * The Interface FilterGenerator.
 */
public interface FilterGenerator<M extends FilterGeneratorResult> {
   
   /**
    * Generate result.
    *
    * @param chain the chain
    * @return the filter generator result
    */
   M generateResult(FilterChain chain);
}
