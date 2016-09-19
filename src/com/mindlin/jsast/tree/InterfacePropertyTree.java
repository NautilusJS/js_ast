package com.mindlin.jsast.tree;

public interface InterfacePropertyTree {
	
	IdentifierTree getIdentifier();
	
	boolean isOptional();

	TypeTree getType();
}