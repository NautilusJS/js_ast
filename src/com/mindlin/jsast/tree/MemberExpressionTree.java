package com.mindlin.jsast.tree;

public interface MemberExpressionTree extends BinaryTree, PatternTree {

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return BinaryTree.super.accept(visitor, data);
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return BinaryTree.super.accept(visitor, data);
	}
	
}
