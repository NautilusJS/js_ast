package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.MemberExpressionTree;

public class MemberExpressionTreeImpl extends BinaryTreeImpl implements MemberExpressionTree {

	public MemberExpressionTreeImpl(Kind kind, ExpressionTree left, ExpressionTree right) {
		super(kind, left, right);
	}

	public MemberExpressionTreeImpl(SourcePosition start, SourcePosition end, Kind kind, ExpressionTree left, ExpressionTree right) {
		super(start, end, kind, left, right);
	}
	
}
