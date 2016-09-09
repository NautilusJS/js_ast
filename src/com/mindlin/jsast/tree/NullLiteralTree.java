package com.mindlin.jsast.tree;

public interface NullLiteralTree extends LiteralTree<Void>{
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NULL_LITERAL;
	}
}
