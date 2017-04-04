package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeTree;

/**
 * Type tree representing the 'any' type
 * @author mailmindlin
 */
public interface AnyTypeTree extends TypeTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ANY_TYPE;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitAnyType(this, data);
	}
}
