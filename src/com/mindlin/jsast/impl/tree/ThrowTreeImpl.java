package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ThrowTree;

public class ThrowTreeImpl extends AbstractExpressiveStatementTree implements ThrowTree {
	
	public ThrowTreeImpl(long start, long end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
