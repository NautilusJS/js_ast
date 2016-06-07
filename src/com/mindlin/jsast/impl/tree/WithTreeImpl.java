package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.WithTree;

public class WithTreeImpl extends AbstractControlStatementTree implements WithTree {
	protected final ExpressionTree scope;
	public WithTreeImpl(long start, long end, ExpressionTree scope, StatementTree statement) {
		super(start, end, statement);
		this.scope = scope;
	}

	@Override
	public ExpressionTree getScope() {
		return this.scope;
	}
	
}
