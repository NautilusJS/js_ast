package com.mindlin.jsast.tree;

import java.util.List;

public interface EnumDeclarationTree extends StatementTree {

	IdentifierTree getIdentifier();

	List<ObjectPropertyTree> getValues();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ENUM_DECLARATION;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitEnumDeclaration(this, data);
	}
}
