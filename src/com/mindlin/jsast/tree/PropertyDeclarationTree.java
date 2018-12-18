package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeElementTree;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * 
 * @author mailmindlin
 */
public interface PropertyDeclarationTree extends PropertyTree, DecoratableTree, NamedDeclarationTree, TypeElementTree, ClassElementTree {
	/**
	 * Get the declared type of this property.
	 * 
	 * @return declared type, else {@code null} if not declared
	 */
	TypeTree getType();
	
	ExpressionTree getInitializer();
	
	@Override
	default Kind getKind() {
		return Kind.PROPERTY_DECLARATION;
	}
}
