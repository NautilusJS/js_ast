package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.MemberExpressionTree;

public class MemberExpressionTreeImpl extends BinaryTreeImpl implements MemberExpressionTree {

	public MemberExpressionTreeImpl(Kind kind, ExpressionTree left, ExpressionTree right) {
		super(kind, left, right);
	}

	public MemberExpressionTreeImpl(long start, long end, Kind kind, ExpressionTree left, ExpressionTree right) {
		super(start, end, kind, left, right);
	}
	
}
