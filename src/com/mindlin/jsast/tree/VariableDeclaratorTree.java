package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

public interface VariableDeclaratorTree extends NamedDeclarationTree, UnvisitableTree {
	
	@Override
	DeclarationName getName();
	
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
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATOR;
	}
}
