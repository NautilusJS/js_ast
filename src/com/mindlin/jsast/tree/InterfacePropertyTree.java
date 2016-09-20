package com.mindlin.jsast.tree;

public interface InterfacePropertyTree extends Tree {
	
	IdentifierTree getIdentifier();
	
	boolean isOptional();

	TypeTree getType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_PROPERTY;
	}
}