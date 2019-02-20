package com.mindlin.jsast.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.mindlin.jsast.impl.analysis.SideEffectValidator;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceExpressionTreeImpl;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;

public class ExpressionFlattenerTransformation implements TreeTransformation<ASTTransformerContext> {

	@Override
	public ExpressionTree visitConditionalExpression(ConditionalExpressionTree node, ASTTransformerContext ctx) {
		ExpressionTree condition = node.getCondition();
		
		//Convert (c ? x : x) to (c, x)
		//We can trust the reducer for sequences to check better for side effects
		if (node.getTrueExpression().equivalentTo(node.getFalseExpression()))
			return new SequenceExpressionTreeImpl(node.getStart(), node.getEnd(), Arrays.asList(condition, node.getTrueExpression()));
		
		if (!SideEffectValidator.hasSideEffectsMaybe(ctx, condition)) {
			Optional<Boolean> coerced = SideEffectValidator.coerceToBoolean(ctx, condition);
			if (coerced.isPresent()) {
				if (coerced.get())
					return node.getTrueExpression();
				else
					return node.getFalseExpression();
			}
			
			//Squish to '&&'/'||' expression
			if (condition.equivalentTo(node.getTrueExpression()))
				return new BinaryTreeImpl(node.getStart(), node.getEnd(), Kind.LOGICAL_OR, condition, node.getFalseExpression());
			if (condition.equivalentTo(node.getFalseExpression()))
				return new BinaryTreeImpl(node.getStart(), node.getEnd(), Kind.LOGICAL_AND, condition, node.getTrueExpression());
		}
		
		return node;
	}
	
	@Override
	public ExpressionTree visitParentheses(ParenthesizedTree node, ASTTransformerContext d) {
		//TODO re-wrap expressions that violate precedence
		return node.getExpression();
	}

	@Override
	public ExpressionTree visitBinary(BinaryExpressionTree node, ASTTransformerContext ctx) {
		Tree.Kind kind = node.getKind();
		switch (kind) {
			case LOGICAL_OR:
			case LOGICAL_AND: {
				//Optional<Boolean> rhs = SideEffectValidator.coerceToSideEffectFreeBoolean(ctx, node.getRightOperand());
				if (!SideEffectValidator.hasSideEffectsMaybe(ctx, node.getLeftOperand())) {
					Optional<Boolean> lhs = SideEffectValidator.coerceToBoolean(ctx, node.getLeftOperand());
					if (lhs.isPresent()) {
						if (kind == Kind.LOGICAL_AND) {
							// (true && x) => x
							if (lhs.get())
								return node.getRightOperand();
							// (false && x) => false
							return node.getLeftOperand();
						} else {//kind == LOGICAL_OR
							// (true || x) => true
							if (lhs.get())
								return node.getLeftOperand();
							// (false || x) => x
							return node.getRightOperand();
						}
					}
				}
				
				if (!SideEffectValidator.hasSideEffectsMaybe(ctx, node.getRightOperand())) {
					Optional<Boolean> rhs = SideEffectValidator.coerceToBoolean(ctx, node.getRightOperand());
					if (rhs.isPresent()) {
						//We can't simplify these as well as when we know the LHS has no side-effects, because the LHS must be evaluated
						if (kind == Kind.LOGICAL_AND) {
							// (x && true) => x
							if (rhs.get())
								return node.getLeftOperand();
						} else {//kind == LOGICAL_OR
							// (x || false) => x
							if (!rhs.get())
								return node.getLeftOperand();
						}
						
						// (x || true) and (x && false) can't be reduced (x has side-effects), even though we know their values
						return node;
					}
				}
				break;
			}
			case ADDITION:
				//Addition is hard, b/c strings and stuff
				break;
			case SUBTRACTION:
			case MULTIPLICATION:
			case DIVISION:
			case REMAINDER:
			case EXPONENTIATION:
			case LEFT_SHIFT:
			case RIGHT_SHIFT:
			case UNSIGNED_RIGHT_SHIFT: {
				if (!SideEffectValidator.hasSideEffectsMaybe(ctx, node)) {
					Optional<Number> lhs = SideEffectValidator.coerceToNumber(ctx, node.getLeftOperand());
					if (!lhs.isPresent())
						break;
					
					Optional<Number> rhs = SideEffectValidator.coerceToNumber(ctx, node.getRightOperand());
					if (!rhs.isPresent())
						break;
					
					
					//TODO handle other implementations of 'Number'
					Number left = lhs.get(), right = rhs.get();
					
					//TODO handle differences between Java math and JS math
					Number result;
					switch (kind) {
						case SUBTRACTION:
							result = left.doubleValue() - right.doubleValue();
							break;
						case MULTIPLICATION:
							result = left.doubleValue() * right.doubleValue();
							break;
						case DIVISION:
							result = left.doubleValue() / right.doubleValue();
							break;
						case REMAINDER:
							result = left.doubleValue() % right.doubleValue();
							break;
						case EXPONENTIATION:
							result = Math.pow(left.doubleValue(), right.doubleValue());
							break;
						case LEFT_SHIFT:
							//TODO exception if not int?
							result = left.intValue() << right.intValue();
							break;
						case RIGHT_SHIFT:
							//TODO exception if not int?
							result = left.intValue() >> right.intValue();
							break;
						case UNSIGNED_RIGHT_SHIFT:
							//TODO exception if not int?
							result = left.intValue() >>> right.intValue();
							break;
						default:
							throw new IllegalStateException("This should be impossible");
					}
					
					return new NumericLiteralTreeImpl(node.getStart(), node.getEnd(), result);
				}
				break;
			}
			default:
				break;
		}
		return node;
	}
	
	@Override
	public ExpressionTree visitUnary(UnaryTree node, ASTTransformerContext ctx) {
		
		if (node.getKind() == Kind.UNARY_PLUS) {
			ExpressionTree expr = node.getExpression();
			switch (expr.getKind()) {
				case UNARY_MINUS://Already coerced to number
				case UNARY_PLUS:
				case NUMERIC_LITERAL:
					return expr;
				case BOOLEAN_LITERAL:
					return new NumericLiteralTreeImpl(node.getStart(), node.getEnd(), ((BooleanLiteralTree)expr).getValue() ? 1 : 0);
				case FUNCTION_EXPRESSION:
					return new NumericLiteralTreeImpl(node.getStart(), node.getEnd(), Double.NaN);
				case STRING_LITERAL: {
					//TODO finish
					break;
				}
				default:
					break;
			}
		}
		
		return node;
	}

	@Override
	public ExpressionTree visitSequence(SequenceExpressionTree node, ASTTransformerContext ctx) {
		boolean modified = false;
		ArrayList<ExpressionTree> expressions = new ArrayList<>();
		for (ExpressionTree expression : node.getExpressions().subList(0, node.getExpressions().size() - 1)) {
			if (!SideEffectValidator.hasSideEffectsMaybe(ctx, expression))
				modified = true;
			else
				expressions.add(expression);
		}
		
		//Ensure that the last value is preserved
		if (expressions.isEmpty() || expressions.get(expressions.size() - 1) != node.getExpressions().get(node.getExpressions().size() - 1))
			expressions.add(node.getExpressions().get(node.getExpressions().size() - 1));
		
		if (expressions.size() == 1)
			return expressions.get(0);
		else if (modified)
			return new SequenceExpressionTreeImpl(node.getStart(), node.getEnd(), expressions);
		else
			return node;
	}
}
