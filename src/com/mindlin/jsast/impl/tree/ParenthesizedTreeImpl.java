package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ParenthesizedTree;

public class ParenthesizedTreeImpl extends AbstractExpressiveExpressionTree implements ParenthesizedTree {
	
	public ParenthesizedTreeImpl(long start, long end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
