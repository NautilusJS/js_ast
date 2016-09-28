package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeTree;

public interface IntersectionTypeTree extends TypeTree {
	TypeTree getLeftType();

	TypeTree getRightType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TYPE_INTERSECTION;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitIntersectionType(this, data);
	}
}
