package com.mindlin.jsast.tree;

/**
 * A property on an object literal or something.
 * 
 * @author mailmindlin
 */
public interface PropertyTree extends NamedDeclarationTree {
	Modifiers getModifiers();
	
	@Override
	PropertyName getName();
}
