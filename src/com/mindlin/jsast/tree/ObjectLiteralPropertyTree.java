package com.mindlin.jsast.tree;

public interface ObjectLiteralPropertyTree extends Tree {
	IdentifierTree getKey();
	
	ExpressionTree getValue();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_LITERAL_PROPERTY;
	}
}
