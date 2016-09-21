package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class AssignmentTreeImpl extends BinaryTreeImpl implements AssignmentTree {
	
	public AssignmentTreeImpl(Kind kind, ExpressionTree variable, ExpressionTree right) {
		super(kind, variable, right);
	}
	
	public AssignmentTreeImpl(long start, long end, Kind kind, ExpressionTree variable, ExpressionTree right) {
		super(start, end, kind, variable, right);
	}
	
}
