package com.mindlin.jsast.tree;

public interface ForEachLoopTree extends LoopTree {
	VariableDeclarationTree getVariable();

	ExpressionTree getExpression();
}