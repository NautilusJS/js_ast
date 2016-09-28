package com.mindlin.jsast.tree;

import java.util.Set;

public interface EnumDeclarationTree extends ExpressionTree {

	IdentifierTree getIdentifier();

	Set<ObjectPropertyTree> getValues();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ENUM_DECLARATION;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitEnumDeclaration(this, data);
	}
}
