package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.SpreadTree;

public class SpreadTreeImpl extends AbstractExpressiveExpressionTree implements SpreadTree {

	public SpreadTreeImpl(long start, long end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
