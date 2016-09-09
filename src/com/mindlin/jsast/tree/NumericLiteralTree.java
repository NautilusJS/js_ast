package com.mindlin.jsast.tree;

public interface NumericLiteralTree extends LiteralTree<Number> {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NUMERIC_LITERAL;
	}
}
