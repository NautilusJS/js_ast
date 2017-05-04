package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.TreeVisitor;

public interface LiteralTypeTree<T> extends TypeTree {
	LiteralTree<T> getValue();
	
	@Override
	default Kind getKind() {
		return Kind.LITERAL_TYPE;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return null;
	}
}
