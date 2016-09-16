package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface InterfaceTypeTree extends TypeTree {

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_TYPE;
	}
}
