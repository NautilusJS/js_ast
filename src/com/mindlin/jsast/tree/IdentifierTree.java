package com.mindlin.jsast.tree;

public interface IdentifierTree extends ExpressionTree, ObjectPropertyKeyTree, PatternTree {
	String getName();
	
	/**
	 * Get the name of this identifier, as it appeared in the source.
	 * This method is designed to be used for generating source maps from AST's with
	 * variables that have been renamed.
	 * @return source name
	 */
	default String getSourceName() {
		return getName();
	}
	
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