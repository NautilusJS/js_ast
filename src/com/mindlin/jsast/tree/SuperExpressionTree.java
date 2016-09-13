package com.mindlin.jsast.tree;

public interface SuperExpressionTree extends ExpressionTree {

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SUPER_EXPRESSION;
	}
}
