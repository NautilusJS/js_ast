package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ComputedPropertyKeyTreeImpl extends AbstractExpressiveExpressionTree implements ComputedPropertyKeyTree {
	
	public ComputedPropertyKeyTreeImpl(long start, long end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
