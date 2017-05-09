package com.mindlin.jsast.transform;

import java.util.ArrayList;
import java.util.Optional;

import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceTreeImpl;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.SequenceTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class ExpressionFlattenerTransformation implements TreeTransformation<ASTTransformerContext> {
	protected Optional<Boolean> coerceToBoolean(ASTTransformerContext ctx, ExpressionTree tree) {
		switch (tree.getKind()) {
			case BOOLEAN_LITERAL:
				return Optional.of(((BooleanLiteralTree)tree).getValue());
			case NUMERIC_LITERAL: {
				Number value = ((NumericLiteralTree)tree).getValue();
				return Optional.of(!Double.isNaN(value.doubleValue()) && value.doubleValue() != 0.0);
			}
			case STRING_LITERAL:
				return Optional.of(!((StringLiteralTree)tree).getValue().isEmpty());
		}
		//We have no idea
		return Optional.empty();
	}
	
	protected boolean hasSideEffectsMaybe(ASTTransformerContext ctx, ExpressionTree tree) {
		switch (tree.getKind()) {
			case NULL_LITERAL:
			case BOOLEAN_LITERAL:
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case REGEXP_LITERAL:
				return false;
			//Don't have an inherent side-effect, but could
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
			case INSTANCEOF:
				return hasSideEffectsMaybe(ctx, ((BinaryTree)tree).getLeftOperand())
						|| hasSideEffectsMaybe(ctx, ((BinaryTree)tree).getRightOperand());
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
				return true;
		}
		//We can't prove that there *aren't* side effects
		return true;
	}
	
	@Override
	public StatementTree visitBlock(BlockTree node, ASTTransformerContext ctx) {
		// TODO Auto-generated method stub
		/*
		 * - Merge sub-blocks, if can be done w/o SE
		 * - Remove empty statements
		 * - Hoist variables (let/const's 'temporal dead zone' shouldn't be a thing now)
		 */
		return TreeTransformation.super.visitBlock(node, ctx);
	}

	@Override
	public ExpressionTree visitConditionalExpression(ConditionalExpressionTree node, ASTTransformerContext ctx) {
		ExpressionTree condition = node.getCondition();
		
		Optional<Boolean> coerced = this.coerceToBoolean(ctx, condition);
		if (coerced.isPresent() && !hasSideEffectsMaybe(ctx, condition)) {
			if (coerced.get())
				return node.getTrueExpression();
			else
				return node.getFalseExpression();
		}
		
		return node;
	}

	@Override
	public ExpressionTree visitSequence(SequenceTree node, ASTTransformerContext ctx) {
		boolean modified = false;
		ArrayList<ExpressionTree> expressions = new ArrayList<>();
		for (ExpressionTree expression : node.getExpressions()) {
			if (!this.hasSideEffectsMaybe(ctx, expression)) {
				modified = true;
				continue;
			}
			expressions.add(expression);
		}
		
		//Ensure that the last value is preserved
		if (expressions.isEmpty() || expressions.get(expressions.size() - 1) != node.getExpressions().get(node.getExpressions().size() - 1))
			expressions.add(node.getExpressions().get(node.getExpressions().size() - 1));
		
		ExpressionTree result;
		
		if (expressions.size() == 1)
			return expressions.get(0);
		else if (modified)
			return new SequenceTreeImpl(node.getStart(), node.getEnd(), expressions);
		else
			return node;
	}

	@Override
	public StatementTree visitIf(IfTree node, ASTTransformerContext ctx) {
		StatementTree concequent = node.getThenStatement();
		StatementTree alternative = node.getElseStatement();
		
		if (concequent.getKind() == Kind.EMPTY_STATEMENT && (alternative == null || alternative.getKind() == Kind.EMPTY_STATEMENT)) {
			if (this.hasSideEffectsMaybe(ctx, node.getExpression()))
				return new ExpressionStatementTreeImpl(node.getStart(), node.getEnd(), node.getExpression());
			return new EmptyStatementTreeImpl(node.getStart(), node.getEnd());
		}
		
		Optional<Boolean> coerced = this.coerceToBoolean(ctx, node.getExpression());
		//TODO support side-effect-y condition as StatementExpression
		if (coerced.isPresent() && !this.hasSideEffectsMaybe(ctx, node.getExpression())) {
			if (coerced.get())
				return node.getThenStatement();
			else {
				if (alternative == null)
					return new EmptyStatementTreeImpl(node.getStart(), node.getEnd());
				else
					return alternative;
			}
		}
		
		return node;
	}
	
}