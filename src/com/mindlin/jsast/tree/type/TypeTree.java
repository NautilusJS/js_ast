package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;

public interface TypeTree extends Tree {
	boolean isImplicit();
	
	<R, D> R accept(TypeTreeVisitor<R, D> visitor, D data);
}
