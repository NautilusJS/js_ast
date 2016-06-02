package com.mindlin.jsast.tree;

public interface CompoundAssignmentTree extends ExpressiveExpressionTree, ExpressionTree {
	ExpressionTree getVariable();
}
