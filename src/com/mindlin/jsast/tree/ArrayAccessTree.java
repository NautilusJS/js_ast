package com.mindlin.jsast.tree;

public interface ArrayAccessTree extends ExpressiveExpressionTree, ExpressionTree {
	ExpressionTree getIndex();
}
