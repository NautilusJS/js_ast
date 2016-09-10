package com.mindlin.jsast.tree;

public interface ConditionalExpressionTree extends ExpressionTree {
	ExpressionTree getCondition();

	ExpressionTree getFalseExpression();

	ExpressionTree getTrueExcpression();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CONDITIONAL;
	}
}
