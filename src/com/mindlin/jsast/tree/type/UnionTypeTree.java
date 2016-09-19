package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface UnionTypeTree extends TypeTree {
	TypeTree getLeftType();

	TypeTree getRightType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.UNION;
	}
}
