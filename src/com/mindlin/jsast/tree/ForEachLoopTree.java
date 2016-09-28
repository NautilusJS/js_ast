package com.mindlin.jsast.tree;

public interface ForEachLoopTree extends LoopTree {
	PatternTree getVariable();

	ExpressionTree getExpression();

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitForEachLoop(this, data);
	}
}