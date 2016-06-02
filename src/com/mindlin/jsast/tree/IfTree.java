package com.mindlin.jsast.tree;

public interface IfTree extends StatementTree {
	ExpressionTree getExpression();
	StatementTree getThenStatement();
	StatementTree getElseStatement();
}
