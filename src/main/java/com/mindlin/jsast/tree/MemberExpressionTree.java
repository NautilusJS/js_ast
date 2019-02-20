package com.mindlin.jsast.tree;

public interface MemberExpressionTree extends BinaryExpressionTree, PatternTree {

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitMemberExpression(this, data);
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitBinary(this, data);//TODO is this going to be bad in the future?
	}
	
	@Override
	default <R, D> R accept(PatternTreeVisitor<R, D> visitor, D data) {
		return visitor.visitMemberExpression(this, data);
	}
	
}
