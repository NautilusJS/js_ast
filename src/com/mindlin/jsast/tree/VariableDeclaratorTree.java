package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

public interface VariableDeclaratorTree extends Tree {
	
	/**
	 * Get any initializer for this variable. For parameters, this is the
	 * default value. Null means that there was no initializer/default value.
	 * 
	 * @return initializer, else null if not present
	 */
	ExpressionTree getInitializer();
	
	/**
	 * Get the type of this variable. Return null for any.
	 * 
	 * @return type (or null)
	 */
	TypeTree getType();
	
	/**
	 * Get the pattern of the variable declared. Note that this is not
	 * necessarily an {@link IdentifierTree}.
	 * 
	 * @return identifier
	 */
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
