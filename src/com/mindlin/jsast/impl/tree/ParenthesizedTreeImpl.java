package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ParenthesizedTree;

public class ParenthesizedTreeImpl extends AbstractExpressiveExpressionTree implements ParenthesizedTree {
	
	public ParenthesizedTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
