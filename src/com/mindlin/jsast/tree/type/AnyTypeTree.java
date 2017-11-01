package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;

public interface AnyTypeTree extends TypeTree {
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ANY_TYPE;
	}
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitAnyType(this, data);
	}
}
