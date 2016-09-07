package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public interface GenericTypeTree extends TypeTree {
	IdentifierTree getName();
	TypeTree getSupertype();
}
