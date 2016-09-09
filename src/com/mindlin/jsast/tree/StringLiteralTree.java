package com.mindlin.jsast.tree;

public interface StringLiteralTree extends LiteralTree<String> {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.STRING_LITERAL;
	}
}
