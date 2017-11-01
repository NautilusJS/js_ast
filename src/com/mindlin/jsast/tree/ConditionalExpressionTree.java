package com.mindlin.jsast.tree;

public interface ConditionalExpressionTree extends ExpressionTree {
	ExpressionTree getCondition();

	ExpressionTree getFalseExpression();

	ExpressionTree getTrueExpression();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CONDITIONAL;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitConditionalExpression(this, data);
	}
}
