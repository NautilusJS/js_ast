package com.mindlin.jsast.tree;

public interface NewTree extends ExpressionTree {
	ExpressionTree getConstructorExpression();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NEW;
	}
}