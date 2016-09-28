package com.mindlin.jsast.tree;

public interface ParenthesizedTree extends ExpressiveExpressionTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PARENTHESIZED;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitParentheses(this, data);
	}
}