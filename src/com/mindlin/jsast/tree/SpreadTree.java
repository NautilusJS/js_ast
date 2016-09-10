package com.mindlin.jsast.tree;

public interface SpreadTree extends ExpressiveExpressionTree, UnaryTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SPREAD;
	}
}
