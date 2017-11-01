package com.mindlin.jsast.tree;

public interface AssignmentTree extends BinaryTree {
	default ExpressionTree getVariable() {
		return getLeftOperand();
	}

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ASSIGNMENT;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitAssignment(this, data);
	}
}
