package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressiveStatementTree;

public abstract class AbstractExpressiveStatementTree extends AbstractTree implements ExpressiveStatementTree {
	protected final ExpressionTree expression;

	public AbstractExpressiveStatementTree(long start, long end, ExpressionTree expression) {
		super(start, end);
		this.expression = expression;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression());
	}
}