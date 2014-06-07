package com.p5solutions.search.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: sophanara
 * Date: 2013-10-09
 * Time: 11:44 AM
 * <p/>
 * This class allow to converts logical expression into a postfix logical expression.
 * For example:
 * <p/>
 * true && false || (true && false)  -> true false && true false && ||
 */
public class FilterPostfixConverter {

    public static List<FilterElement> convert(List<FilterElement> filterElementList) {
        Stack<FilterElement> stack = new Stack<FilterElement>();
        List<FilterElement> postfix = new ArrayList<FilterElement>();

        for (FilterElement filterElement : filterElementList) {
            if (isOperand(filterElement)) {
                postfix.add(filterElement);

            } else if (isOperator(filterElement) && !isUnaryOperator(filterElement)) {
                while (!stack.empty() && precedence(stack.peek()) >= precedence(filterElement)) {
                    postfix.add(stack.pop());
                }
                stack.push(filterElement);
            } else if (FilterUtility.isLeftBracket(filterElement) || isUnaryOperator(filterElement)) {
                stack.push(filterElement);
            } else if (FilterUtility.isRightBracket(filterElement)) {
                while (!stack.isEmpty() && !FilterUtility.isLeftBracket(stack.peek())) {
                    postfix.add(stack.pop());
                }
                stack.pop();  // pop the left parenthesis
            }
        }

        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }
        return postfix;
    }

    private static boolean isUnaryOperator(FilterElement filterElement) {
        //#todo add IN
        if (FilterUtility.isOperatorNOT(filterElement)) {
            return true;
        }
        return false;

    }

    public static int precedence(FilterElement operator) {
        int precedence = 0;
        if (operator instanceof Operator) {

            if (FilterUtility.isOperatorOR(operator)) {
                precedence = 1;
            } else if (FilterUtility.isOperatorAND(operator)) {
                precedence = 2;
            } else if (FilterUtility.isOperatorNOT(operator)) {
                precedence = 3;
            }
        }

        return precedence;
    }


    private static boolean isOperator(FilterElement filterElement) {
        return FilterUtility.isOperator(filterElement);
    }

    private static boolean isOperand(FilterElement filterElement) {
        return FilterUtility.isOperand(filterElement);
    }
}
