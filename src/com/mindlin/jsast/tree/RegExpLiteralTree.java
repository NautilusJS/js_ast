package com.mindlin.jsast.tree;

public interface RegExpLiteralTree extends LiteralTree<String[]> {
	
	@Override
	default Kind getKind() {
		return Kind.REGEXP_LITERAL;
	}
}
