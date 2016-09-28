package com.mindlin.jsast.tree;

import java.util.List;

public interface BlockTree extends StatementTree {
	List<? extends StatementTree> getStatements();

	boolean isScoped();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.BLOCK;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitBlock(this, data);
	}
}