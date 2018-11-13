package com.mindlin.jsast.tree;

public interface DecoratorTree extends Tree, UnvisitableTree {
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.DECORATOR;
	}
}
