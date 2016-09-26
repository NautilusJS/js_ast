package com.mindlin.jsast.tree;

public interface ForEachLoopTree extends LoopTree {
	PatternTree getVariable();

	ExpressionTree getExpression();
}