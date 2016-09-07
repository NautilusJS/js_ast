package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.TypeTree;

public interface UnionTypeTree extends TypeTree {
	TypeTree getLeftType();
	TypeTree getRightType();
}
