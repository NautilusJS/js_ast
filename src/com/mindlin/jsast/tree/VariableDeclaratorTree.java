package com.mindlin.jsast.tree;

public interface VariableDeclaratorTree extends IdentifierTree {
	ExpressionTree getIntitializer();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATOR;
	}
}
