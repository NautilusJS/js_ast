package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.TypeTree;

public interface ArrayTypeTree extends TypeTree {
	TypeTree getBaseType();
}
