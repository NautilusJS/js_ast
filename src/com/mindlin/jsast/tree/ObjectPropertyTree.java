package com.mindlin.jsast.tree;

public interface ObjectPropertyTree extends Tree {
	ObjectPropertyKeyTree getKey();
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
