package com.mindlin.jsast.tree;

public interface AssignmentTree extends BinaryTree, StatementTree {
	default ExpressionTree getVariable() {
		return getLeftOperand();
	}
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ASSIGNMENT;
	}
}
