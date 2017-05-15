package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

public interface VariableDeclaratorTree extends Tree {

	/**
	 * Get any initializer for this variable. For parameters, this is the
	 * default value. An empty Optional means that there was no
	 * initializer/default value.
	 * 
	 * @return initializer
	 */
	ExpressionTree getInitializer();

	/**
	 * Get the type of this variable. Return null for any.
	 * 
	 * @return type (or null)
	 */
	TypeTree getType();
	
	PatternTree getIdentifier();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATOR;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
