package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.type.TypeTree;

/**
 * 
 * @author mailmindlin
 */
public interface PropertyDeclarationTree extends PropertyTree, DecoratableTree, NamedDeclarationTree, ClassElementTree {
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
	
	@Override
	default <R, D> R accept(ClassElementVisitor<R, D> visitor, D data) {
		return visitor.visitPropertyDeclaration(this, data);
	}
}
