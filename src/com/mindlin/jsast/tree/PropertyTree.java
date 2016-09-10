package com.mindlin.jsast.tree;

public interface PropertyTree extends Tree {
	FunctionExpressionTree getGetter();

	ExpressionTree getExpression();

	FunctionExpressionTree getSetter();

	ExpressionTree getValue();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PROPERTY;
	}
}
