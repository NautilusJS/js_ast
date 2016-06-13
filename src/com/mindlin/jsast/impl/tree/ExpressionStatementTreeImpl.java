package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionStatementTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ExpressionStatementTreeImpl extends AbstractTree implements ExpressionStatementTree {
	protected final ExpressionTree expression;
	public ExpressionStatementTreeImpl(ExpressionTree expr) {
		super(expr.getStart(), expr.getEnd());
		this.expression = expr;
	}

	@Override
	public ExpressionTree getExpression() {
		return expression;
	}
	
}
