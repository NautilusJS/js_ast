package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressionPatternTree;

public class ExpressionPatternTreeImpl extends BinaryTreeImpl implements ExpressionPatternTree {

	public ExpressionPatternTreeImpl(Kind kind, ExpressionTree left, ExpressionTree right) {
		super(kind, left, right);
	}

	public ExpressionPatternTreeImpl(long start, long end, Kind kind, ExpressionTree left, ExpressionTree right) {
		super(start, end, kind, left, right);
	}
	
}
