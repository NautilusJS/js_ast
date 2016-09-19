package com.mindlin.jsast.tree;

public interface BooleanLiteralTree extends LiteralTree<Boolean> {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.BOOLEAN_LITERAL;
	}
}
