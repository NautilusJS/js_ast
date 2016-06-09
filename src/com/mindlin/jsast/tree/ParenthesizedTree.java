package com.mindlin.jsast.tree;

public interface ParenthesizedTree extends ExpressiveExpressionTree, ExpressionTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PARENTHESIZED;
	}
}