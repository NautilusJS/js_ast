package com.mindlin.jsast.tree;

public interface ThisExpressionTree extends ExpressionTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.THIS_EXPRESSION;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitThis(this, data);
	}
}
