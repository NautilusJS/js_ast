package com.mindlin.jsast.tree;

public interface BinaryTree extends ExpressionTree {
	ExpressionTree getLeftOperand();

	ExpressionTree getRightOperand();
}
