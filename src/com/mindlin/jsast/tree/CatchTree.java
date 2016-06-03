package com.mindlin.jsast.tree;

public interface CatchTree extends Tree {
	BlockTree getBlock();
	IdentifierTree getParameter();
	ExpressionTree getExpression();
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CATCH;
	}
}