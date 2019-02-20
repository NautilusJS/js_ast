package com.mindlin.nautilus.tree;

public interface DecoratorTree extends Tree, UnvisitableTree {
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.DECORATOR;
	}
}
