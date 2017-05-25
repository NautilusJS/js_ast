package com.mindlin.jsast.tree;

public interface BinaryTree extends ExpressionTree {
	ExpressionTree getLeftOperand();
	
	ExpressionTree getRightOperand();
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitBinary(this, data);
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		if (getKind() != other.getKind() || this.hashCode() != other.hashCode())
			return false;
		
		BinaryTree b = (BinaryTree) other;
		return getLeftOperand().equivalentTo(b.getLeftOperand()) && getRightOperand().equivalentTo(b.getRightOperand());
	}
}
