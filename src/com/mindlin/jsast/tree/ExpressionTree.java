package com.mindlin.jsast.tree;

/**
 * A generic super-interface that's used for trees that represent expressions
 * @author mailmindlin
 */
public interface ExpressionTree extends Tree {
	<R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data);
}
