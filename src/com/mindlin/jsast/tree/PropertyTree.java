package com.mindlin.jsast.tree;

public interface PropertyTree extends Tree {
	IdentifierTree getKey();

	ExpressionTree getValue();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PROPERTY;
	}
}
