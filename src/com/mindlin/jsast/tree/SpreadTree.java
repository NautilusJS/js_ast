package com.mindlin.jsast.tree;

public interface SpreadTree extends ExpressiveExpressionTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SPREAD;
	}
}
