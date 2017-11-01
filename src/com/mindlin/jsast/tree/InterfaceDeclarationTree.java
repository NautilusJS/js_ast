package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.TypeTree;

public interface InterfaceDeclarationTree extends StatementTree {
	
	IdentifierTree getIdentifier();
	
	List<TypeTree> getSupertypes();
	
	List<? extends InterfacePropertyTree> getProperties();

	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_DECLARATION;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitInterfaceDeclaration(this, data);
	}
}
