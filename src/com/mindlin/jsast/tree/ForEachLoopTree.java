package com.mindlin.jsast.tree;
public interface ForEachLoopTree extends LoopTree {
	ExpressionTree getVariable();
	ExpressionTree getExpression();
}