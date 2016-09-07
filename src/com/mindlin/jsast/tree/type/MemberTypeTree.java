package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public interface MemberTypeTree extends TypeTree {
	IdentifierTree getName();
	TypeTree getType();
	boolean isOptional();
}
