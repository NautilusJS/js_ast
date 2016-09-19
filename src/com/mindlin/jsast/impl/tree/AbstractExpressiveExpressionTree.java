package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressiveExpressionTree;

public abstract class AbstractExpressiveExpressionTree extends AbstractTree implements ExpressiveExpressionTree {
	protected final ExpressionTree expression;

	public AbstractExpressiveExpressionTree(long start, long end, ExpressionTree expression) {
		super(start, end);
		this.expression = expression;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
}