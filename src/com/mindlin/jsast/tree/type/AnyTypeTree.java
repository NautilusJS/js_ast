package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;

/**
 * The {@code any} type.
 * 
 * @author mailmindlin
 */
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
