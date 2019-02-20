package com.mindlin.jsast.tree;

public interface UnvisitableTree extends Tree {
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
