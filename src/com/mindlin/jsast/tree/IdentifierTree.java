package com.mindlin.jsast.tree;

public interface IdentifierTree extends ExpressionTree {
	String getName();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IDENTIFIER;
	}
}