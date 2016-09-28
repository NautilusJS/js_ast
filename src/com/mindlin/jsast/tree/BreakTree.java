package com.mindlin.jsast.tree;

public interface BreakTree extends GotoTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.BREAK;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitBreak(this, data);
	}
}
