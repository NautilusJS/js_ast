package com.mindlin.jsast.tree;

public interface LiteralTree<T> extends ExpressionTree {
	T getValue();
}
