package com.mindlin.jsast.tree;

import java.util.Objects;

public interface IdentifierTree extends ExpressionTree, PropertyName, PatternTree {
	String getName();
	
	/**
	 * Get the name of this identifier, as it appeared in the source. This
	 * method is designed to be used for generating source maps from AST's with
	 * variables that have been renamed.
	 * 
	 * @return source name
	 */
	default String getSourceName() {
		return getName();
	}
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IDENTIFIER;
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		if (other == null || this.getKind() != other.getKind() || !(other instanceof IdentifierTree))
			return false;
		return Objects.equals(this.getName(), ((IdentifierTree) other).getName());
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitIdentifier(this, data);
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitIdentifier(this, data);
	}
	
	@Override
	default <R, D> R accept(PatternTreeVisitor<R, D> visitor, D data) {
		return visitor.visitIdentifier(this, data);
	}
}