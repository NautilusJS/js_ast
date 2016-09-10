package com.mindlin.jsast.tree;

public interface ThisExpressionTree extends ExpressionTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.THIS_EXPRESSION;
	}
}
