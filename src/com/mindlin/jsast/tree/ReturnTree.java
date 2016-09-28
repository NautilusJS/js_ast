package com.mindlin.jsast.tree;

public interface ReturnTree extends ExpressiveStatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.RETURN;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitReturn(this, data);
	}
}
