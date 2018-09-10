package com.mindlin.jsast.tree;

public interface DecoratorTree extends Tree {
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.DECORATOR;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
