package com.mindlin.jsast.tree;

public interface BinaryTree extends ExpressionTree {
	ExpressionTree getLeftOperand();

	ExpressionTree getRightOperand();

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitBinary(this, data);
	}
}
