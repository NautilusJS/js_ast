package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface AnyTypeTree extends TypeTree {
	boolean isImplicit();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ANY_TYPE;
	}
}
