package com.mindlin.jsast.tree;

public interface ThrowTree extends ExpressionStatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.THROW;
	}
}
