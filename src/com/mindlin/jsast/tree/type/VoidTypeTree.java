package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeTree;

public interface VoidTypeTree extends TypeTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VOID_TYPE;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitVoidType(this, data);
	}
}
