package com.mindlin.jsast.tree;

public interface SuperExpressionTree extends ExpressionTree {

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SUPER_EXPRESSION;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitSuper(this, data);
	}
}
