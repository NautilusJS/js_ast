package com.mindlin.jsast.tree;

public interface CatchTree extends Tree {
	BlockTree getBlock();

	IdentifierTree getParameter();

	TypeTree getType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CATCH;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitCatch(this, data);
	}
}
