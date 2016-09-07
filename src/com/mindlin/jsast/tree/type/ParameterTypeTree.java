package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public interface ParameterTypeTree extends TypeTree {
	IdentifierTree getName();
	boolean isOptional();
	TypeTree getType();
}
