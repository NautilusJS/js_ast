package com.mindlin.jsast.tree;

public interface SuperExpressionTree extends Tree {

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SUPER_EXPRESSION;
	}
}
