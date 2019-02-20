package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;

public interface IdentifierTypeTree extends TypeTree {
	/**
	 * Get actual identifier
	 * @return
	 */
	IdentifierTree getName();
	
	/**
	 * Get type parameters
	 * @return
	 */
	List<TypeTree> getGenerics();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IDENTIFIER_TYPE;
	}

	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitIdentifierType(this, data);
	}
}
