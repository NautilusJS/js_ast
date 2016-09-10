package com.mindlin.jsast.tree;

public interface ArrayAccessTree extends ExpressiveExpressionTree, ExpressionTree {
	ExpressionTree getIndex();
	

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_ACCESS;
	}
}
