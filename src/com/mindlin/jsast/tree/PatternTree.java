package com.mindlin.jsast.tree;

/**
 * A generic super-interface for trees representing ES6 destructuring operators.
 * @see ArrayPatternTree
 * @see ObjectPatternTree
 * @see AssignmentPatternTree
 * @author mailmindlin
 */
public interface PatternTree extends ExpressionTree {
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return this.accept((TreeVisitor<R, D>) visitor, data);
	}
}
