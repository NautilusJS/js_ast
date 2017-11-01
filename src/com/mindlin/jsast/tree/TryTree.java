package com.mindlin.jsast.tree;

import java.util.List;

public interface TryTree extends StatementTree {
	BlockTree getBlock();

	List<? extends CatchTree> getCatches();

	BlockTree getFinallyBlock();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TRY;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitTry(this, data);
	}
}
