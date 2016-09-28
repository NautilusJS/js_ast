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

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitIdentifier(this, data);
	}
}