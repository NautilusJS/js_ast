package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface VoidTypeTree extends TypeTree {
	boolean isImplicit();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VOID_TYPE;
	}
}
