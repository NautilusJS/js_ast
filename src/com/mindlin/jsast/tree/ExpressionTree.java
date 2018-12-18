package com.mindlin.jsast.tree;

/**
 * A generic super-interface that's used for trees that represent expressions
 * @author mailmindlin
 */
public interface ExpressionTree extends Tree {
	<R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data);

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((ExpressionTreeVisitor<R, D>) visitor, data);
	}
}
