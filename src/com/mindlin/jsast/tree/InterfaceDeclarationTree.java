package com.mindlin.jsast.tree;

import java.util.List;

public interface InterfaceDeclarationTree extends StatementTree {
	
	IdentifierTree getIdentifier();
	
	List<TypeTree> getSupertypes();
	
	List<? extends InterfacePropertyTree> getProperties();

	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_DECLARATION;
	}
}
