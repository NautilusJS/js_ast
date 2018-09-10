package com.mindlin.jsast.tree;

/**
 * A property on an object literal or something.
 * 
 * @author mailmindlin
 */
public interface PropertyTree extends NamedDeclarationTree {
	
	@Override
	PropertyName getName();
	
	Modifiers getModifiers();
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
