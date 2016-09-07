package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public interface GenericRefTypeTree extends TypeTree {
	IdentifierTree getName();
}
