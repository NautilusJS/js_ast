package com.mindlin.jsast.impl.parser;

import java.util.List;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;

/**
 * This is a helper class for {@link com.mindlin.jsast.impl.parser.JSParser JSParser} to throw errors
 * while parsing code. The methods of this class don't ensure that the code can be run without errors
 * (more static error detection is needed), but does serve to save debugging time for blatant errors.
 * @author mailmindlin
 */
public class Validator {
	/**
	 * Whether a given ExpressionTree can be assigned. Note that this check does not
	 * check that the expression being assigned a value will be valid at runtime,
	 * it just helps weed out the times when it's obviously wrong.
	 * @param expr ExpressionTree to test
	 * @return whether the tree is modifiable (directly represents a variable) 	
	 */
	public static boolean canBeAssigned(ExpressionTree expr, JSDialect dialect) {
		switch (expr.getKind()) {
			case IDENTIFIER:
			case MEMBER_SELECT:
			case ARRAY_ACCESS:
				return true;
			case PARENTHESIZED:
				//Recurse to see inside the parentheses
				return canBeAssigned(((ParenthesizedTree)expr).getExpression(), dialect);
			case PREFIX_INCREMENT:
			case POSTFIX_INCREMENT:
			case PREFIX_DECREMENT:
			case POSTFIX_DECREMENT:
				/*
				 * Not even technically against the ECMA 262 spec, but I can't
				 * figure out why not, and this allows the 'run to' operator
				 * (e.g., 'x---->y') to evaluate.
				 * See (stackoverflow.com/q/1642028/2759984#comment1511871_1642028)
				 */
				return dialect.supports("extension.inferlval");
			case SEQUENCE: {
				SequenceExpressionTree seq = (SequenceExpressionTree) expr;
				if (!dialect.supports("extension.inferlval"))
					return false;
				List<ExpressionTree> elements = seq.getElements();
				return canBeAssigned(elements.get(elements.size() - 1), dialect);
			}
			//TODO any others?
			default:
				break;
		}
		return false;
	}
}
