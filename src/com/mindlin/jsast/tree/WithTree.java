package com.mindlin.jsast.tree;

public interface WithTree extends StatementTree {
	ExpressionTree getScope();

	StatementTree getStatement();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.WITH;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitWith(this, data);
	}
}
