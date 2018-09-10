package com.mindlin.jsast.tree;

public interface SpreadElementTree extends ExpressionTree, ObjectLiteralElement {
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Tree.Kind.SPREAD;
	}
}
