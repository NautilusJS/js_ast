package com.mindlin.jsast.tree;

import java.util.List;
import java.util.Optional;

public interface ClassDeclarationTree extends ExpressionTree, StatementTree {
	IdentifierTree getIdentifier();

	Optional<TypeTree> getSuperType();

	List<TypeTree> getImplementing();

	List<ClassPropertyTree> getProperties();

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitClassDeclaration(this, data);
	}
}
