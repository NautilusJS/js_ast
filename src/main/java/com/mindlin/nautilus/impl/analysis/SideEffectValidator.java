package com.mindlin.jsast.impl.analysis;

import java.util.Optional;

import com.mindlin.jsast.transform.ASTTransformerContext;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;

public class SideEffectValidator {
	public static Optional<Boolean> coerceToBoolean(ASTTransformerContext ctx, ExpressionTree tree) {
		switch (tree.getKind()) {
			case BOOLEAN_LITERAL:
				return Optional.of(((BooleanLiteralTree)tree).getValue());
			case NUMERIC_LITERAL: {
				Number value = ((NumericLiteralTree)tree).getValue();
				return Optional.of(!Double.isNaN(value.doubleValue()) && value.doubleValue() != 0.0);
			}
			case STRING_LITERAL:
				return Optional.of(!((StringLiteralTree)tree).getValue().isEmpty());
			case CONDITIONAL: {
				//Special case where coerce(lhs) == coerce(rhs)
				Optional<Boolean> lhs = coerceToBoolean(ctx, ((ConditionalExpressionTree)tree).getTrueExpression());
				if (!lhs.isPresent())
					break;
				Optional<Boolean> rhs = coerceToBoolean(ctx, ((ConditionalExpressionTree)tree).getFalseExpression());
				if (!rhs.isPresent())
					break;
				if (lhs.get() != rhs.get())
					break;
				return lhs;
			}
			case PARENTHESIZED:
				//Recurse.
				return coerceToBoolean(ctx, ((ParenthesizedTree)tree).getExpression());
			case OBJECT_LITERAL:
			case ARRAY_LITERAL:
				return Optional.of(true);
			case NULL_LITERAL:
				return Optional.of(false);
			case IDENTIFIER:
				//TODO we need an 'undefined' literal
				if (((IdentifierTree)tree).getName().equals("undefined"))
					return Optional.of(false);
				break;
			default:
				break;
		}
		//We have no idea
		return Optional.empty();
	}
	
	public static Optional<Boolean> coerceToSideEffectFreeBoolean(ASTTransformerContext ctx, ExpressionTree tree) {
		//Refactor out a common pattern
		Optional<Boolean> coerced = coerceToBoolean(ctx, tree);
		
		if (coerced.isPresent() && hasSideEffectsMaybe(ctx, tree))
			coerced = Optional.empty();
		
		return coerced;
	}
	
	public static Optional<Number> coerceToNumber(ASTTransformerContext ctx, ExpressionTree tree) {
		switch (tree.getKind()) {
			case BOOLEAN_LITERAL:
				return Optional.of(((BooleanLiteralTree)tree).getValue() ? 1 : 0);
			case NUMERIC_LITERAL:
				return Optional.of(((NumericLiteralTree)tree).getValue());
			case STRING_LITERAL:
				//return Optional.of(((StringLiteralTree)tree).getValue());
			default:
				break;
		}
		//We have no idea
		return Optional.empty();
	}
	
	/**
	 * Determines if an expression may have side effects. Errs on the side of returning true.
	 * @param ctx
	 * @param tree
	 * @return If the given expression may have side effects
	 */
	public static boolean hasSideEffectsMaybe(ASTTransformerContext ctx, ExpressionTree tree) {
		switch (tree.getKind()) {
			case NULL_LITERAL:
			case BOOLEAN_LITERAL:
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case REGEXP_LITERAL:
			case IDENTIFIER:
				return false;
			case TEMPLATE_LITERAL:
				for (ExpressionTree expr : ((TemplateLiteralTree)tree).getExpressions())
					if (hasSideEffectsMaybe(ctx, expr))
						return true;
				return false;
			case OBJECT_LITERAL:
				//TODO better checking
				//This shows that '{}' has no side-effects
				if (((ObjectLiteralTree)tree).getProperties().isEmpty())
					return false;
				break;
			case ARRAY_LITERAL:
				//No SE if no element has a SE
				for (ExpressionTree element : ((ArrayLiteralTree)tree).getElements())
					if (hasSideEffectsMaybe(ctx, element))//TODO check context
						return true;
				return false;
			//Don't have an inherent side-effect, but one of their operands might
			case EQUAL:
			case NOT_EQUAL:
			case STRICT_EQUAL:
			case STRICT_NOT_EQUAL:
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
			case LESS_THAN:
			case LESS_THAN_EQUAL:
			case ADDITION:
			case SUBTRACTION:
			case MULTIPLICATION:
			case DIVISION:
			case REMAINDER:
			case EXPONENTIATION:
			case LEFT_SHIFT:
			case RIGHT_SHIFT:
			case UNSIGNED_RIGHT_SHIFT:
			case BITWISE_OR:
			case BITWISE_XOR:
			case BITWISE_AND:
			case IN:
			case INSTANCEOF: {
				BinaryExpressionTree bTree = (BinaryExpressionTree) tree;
				return hasSideEffectsMaybe(ctx, bTree.getLeftOperand())
						|| hasSideEffectsMaybe(ctx, bTree.getRightOperand());
			}
			case ADDITION_ASSIGNMENT:
			case SUBTRACTION_ASSIGNMENT:
			case MULTIPLICATION_ASSIGNMENT:
			case DIVISION_ASSIGNMENT:
			case REMAINDER_ASSIGNMENT:
			case EXPONENTIATION_ASSIGNMENT:
			case LEFT_SHIFT_ASSIGNMENT:
			case RIGHT_SHIFT_ASSIGNMENT:
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
			case BITWISE_OR_ASSIGNMENT:
			case BITWISE_XOR_ASSIGNMENT:
			case BITWISE_AND_ASSIGNMENT:
			case PREFIX_INCREMENT:
			case POSTFIX_INCREMENT:
			case PREFIX_DECREMENT:
			case POSTFIX_DECREMENT:
			case YIELD:
			case YIELD_GENERATOR:
			case FUNCTION_INVOCATION://TODO check if called function has SE
				//Expression has inherent side-effect
				return true;
			default:
				break;
		}
		//We can't prove that there *aren't* side effects
		return true;
	}
}
