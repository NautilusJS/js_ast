package com.mindlin.jsast.tree;

public interface ObjectPropertyKeyTree extends ExpressionTree {
	/**
	 * Whether this tree, as a property key, is computed
	 */
	boolean isComputed();
}
