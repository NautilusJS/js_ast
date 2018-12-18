package com.mindlin.jsast.tree;

public interface BinaryExpressionTree extends ExpressionTree {
	ExpressionTree getLeftOperand();
	
	ExpressionTree getRightOperand();
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitBinary(this, data);
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		if (getKind() != other.getKind() || this.hashCode() != other.hashCode())
			return false;
		
		BinaryExpressionTree b = (BinaryExpressionTree) other;
		return getLeftOperand().equivalentTo(b.getLeftOperand()) && getRightOperand().equivalentTo(b.getRightOperand());
	}
}
