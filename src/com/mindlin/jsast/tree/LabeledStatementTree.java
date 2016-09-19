package com.mindlin.jsast.tree;

public interface LabeledStatementTree extends StatementTree {
	IdentifierTree getName();
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.LABELED_STATEMENT;
	}
}
