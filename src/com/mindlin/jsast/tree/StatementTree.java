package com.mindlin.jsast.tree;

public interface StatementTree extends Tree {
	<R, D> R accept(StatementTreeVisitor<R, D> visitor, D data);

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((StatementTreeVisitor<R, D>) visitor, data);
	}
}
