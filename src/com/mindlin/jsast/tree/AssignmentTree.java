package com.mindlin.jsast.tree;

public interface AssignmentTree extends ExpressionTree, ExpressiveExpressionTree {
	ExpressionTree getVariable();
}
