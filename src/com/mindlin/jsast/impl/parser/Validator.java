package com.mindlin.jsast.impl.parser;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ParenthesizedTree;

public class Validator {
	/**
	 * Whether a given ExpressionTree can be modified (e.g., assigned a value).
	 * @param expr ExpressionTree to test
	 * @return whether the tree is modifiable (directly represents a variable) 	
	 */
	public static boolean canBeModified(ExpressionTree expr) {
		switch (expr.getKind()) {
			case IDENTIFIER:
			case MEMBER_SELECT:
			case ARRAY_ACCESS:
				return true;
			case PARENTHESIZED:
				//Recurse to see inside the parentheses
				return canBeModified(((ParenthesizedTree)expr).getExpression());
			//TODO any others?
			default:
		}
		return false;
	}
}
