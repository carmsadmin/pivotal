package com.p5solutions.search.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @User: sophanara
 * @Date: 2013-10-08
 * @Time: 11:05 AM
 * <p/>
 * <p/>
 * Filter BNF Definition
 * <p/>
 * <filter> := <criteria> | <criteria> <operator> <filter> | <in_operator> <filter-value> | (<filter>) | <not_operator>
 * <filter> <in_operator> := IN <not_operator> := NOT <operator> := AND | OR <filter-value> := array of number.
 * <criteria> := filter criteria with expression
 * <p/>
 * This class make sure that filter chain follow the right grammar
 */
public class FilterBNFValidator {

  private List<FilterElement> filterElementResults;
  private ListIterator<FilterElement> listIterator;

  /**
   * @param filterChain
   */
  public FilterBNFValidator(FilterChain filterChain) {
    List<FilterElement> newList = new ArrayList<FilterElement>();
    flatteningChain(newList, filterChain);
    listIterator = newList.listIterator();
    //filterElementResults = new ArrayList<FilterElement>();
    filterElementResults = newList;
  }

  /**
   * Flat the filter chain so that we can process it
   * 
   * @param newList
   * @param filterChain
   */
  private void flatteningChain(List<FilterElement> newList, FilterChain filterChain) {
    List<FilterElement> oldList = filterChain.getFilterElements();
    if (oldList == null) {
      return;
    }
    for (FilterElement filterElement : oldList) {
      if (filterElement instanceof FilterChain) {
        flatteningChain(newList, (FilterChain) filterElement);
      } else {
        newList.add(filterElement);
      }
    }
  }

  public List<FilterElement> getFilterElementResults() {
    return filterElementResults;
  }

  public void filter() {

//    // <filter> := <criteria> <operator> <filter>
//    if (isOperator(peek())) {
//      filterElementResults.add(next());
//      if (peek() == null) {
//
//        throw new RuntimeException("OR | AND operator need a right side operand");
//      }
//      filter();
//
//      // <filter> := <in_operator> <filter-value>
//    } else if (isInOperator(peek())) {
//      filterElementResults.add(next());
//      if (isValues(peek())) {
//        filterElementResults.add(next());
//      } else {
//        throw new RuntimeException("Expecting a set of values");
//      }
//
//      // <filter> := <not_operator> <filter-value>
//    } else if (isNotOperator(peek())) {
//      filterElementResults.add(next());
//      if (peek() == null) {
//        throw new RuntimeException("NOT operator need a right side operand");
//      }
//      filter();
//      // <filter> := (<filter>)
//    } else if (FilterUtility.isLeftBracket(peek())) {
//      leftBracket();
//      filter();
//      rightBracket();
//    }
//
//    // <filter> := <criteria>
//    criteria();
  }

  private void criteria() {
    if (isCriteria(peek())) {
      filterElementResults.add(next());
      if (peek() != null) {
        filter();
      }
    }
  }

  private void leftBracket() {
    if (FilterUtility.isLeftBracket(peek())) {
      filterElementResults.add(next());
    } else {
      throw new RuntimeException("Expecting left bracket");
    }
  }

  private void rightBracket() {
    if (FilterUtility.isRightBracket(peek())) {
      filterElementResults.add(next());
    } else {
      throw new RuntimeException("Expecting right bracket");
    }
  }

  private boolean isValues(FilterElement filterElement) {
    if (filterElement instanceof Values) {
      return true;
    }
    return false;
  }

  private boolean isInOperator(FilterElement filterElement) {
    if (filterElement instanceof Operator) {
      return ((Operator) filterElement).equals(Operator.IN);
    }
    return false;
  }

  private boolean isNotOperator(FilterElement filterElement) {
    return FilterUtility.isOperatorNOT(filterElement);
  }

  private boolean isOperator(FilterElement operator) {
    if (operator instanceof Operator) {
      return ((Operator) operator).equals(Operator.AND) || ((Operator) operator).equals(Operator.OR);
    }
    return false;
  }

  private boolean isCriteria(FilterElement operator) {
    if (operator instanceof Criteria) {
      return true;
    }
    return false;
  }

  private FilterElement next() {
    FilterElement filterElement = listIterator.next();
    return filterElement;
  }

  protected FilterElement peek() {
    if (listIterator.hasNext()) {
      FilterElement filterElement = listIterator.next();
      listIterator.previous();
      return filterElement;
    }
    return null;
  }

}
