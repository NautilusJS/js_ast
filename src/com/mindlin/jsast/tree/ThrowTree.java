package com.mindlin.jsast.tree;

public interface ThrowTree extends ExpressionStatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.THROW;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitThrow(this, data);
	}
}
