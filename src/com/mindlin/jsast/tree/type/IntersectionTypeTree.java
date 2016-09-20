package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface IntersectionTypeTree extends TypeTree {
	TypeTree getLeftType();

	TypeTree getRightType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TYPE_INTERSECTION;
	}
}
