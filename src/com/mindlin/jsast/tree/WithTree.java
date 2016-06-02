package com.mindlin.jsast.tree;

public interface WithTree extends StatementTree {
	ExpressionTree getScope();
	StatementTree getStatement();
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.WITH;
	}
}
