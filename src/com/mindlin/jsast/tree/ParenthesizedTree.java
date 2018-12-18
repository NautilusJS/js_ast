package com.mindlin.jsast.tree;

public interface ParenthesizedTree extends ExpressionTree {
	
	ExpressionTree getExpression();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PARENTHESIZED;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitParentheses(this, data);
	}
}