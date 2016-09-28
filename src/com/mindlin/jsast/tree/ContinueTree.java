package com.mindlin.jsast.tree;

public interface ContinueTree extends GotoTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CONTINUE;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitContinue(this, data);
	}
}
