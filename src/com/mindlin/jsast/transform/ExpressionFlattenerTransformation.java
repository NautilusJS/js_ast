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