package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ComputedPropertyKeyTreeImpl extends AbstractExpressiveExpressionTree implements ComputedPropertyKeyTree {
	
	public ComputedPropertyKeyTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
