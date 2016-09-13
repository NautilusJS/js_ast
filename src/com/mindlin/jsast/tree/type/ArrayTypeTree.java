package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface ArrayTypeTree extends TypeTree {
	TypeTree getBaseType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_TYPE;
	}
}
