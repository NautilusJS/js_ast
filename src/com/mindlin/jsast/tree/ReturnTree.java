package com.mindlin.jsast.tree;

public interface ReturnTree extends ExpressiveStatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.RETURN;
	}
}
