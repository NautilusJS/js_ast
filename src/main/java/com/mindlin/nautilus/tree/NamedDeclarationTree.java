package com.mindlin.nautilus.tree;

/**
 * Declarations that have a name.
 * 
 * @author mailmindlin
 */
public interface NamedDeclarationTree extends DeclarationTree {
	/**
	 * @return Declaration name
	 */
	DeclarationName getName();
}
