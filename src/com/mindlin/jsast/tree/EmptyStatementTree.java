package com.mindlin.jsast.tree;

public interface EmptyStatementTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EMPTY_STATEMENT;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitEmptyStatement(this, data);
	}
}
