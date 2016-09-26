package com.mindlin.jsast.tree;

public interface IdentifierTree extends ExpressionTree, ObjectPropertyKeyTree, PatternTree {
	String getName();
	
	@Override
	default boolean isComputed() {
		return false;
	}

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IDENTIFIER;
	}
}