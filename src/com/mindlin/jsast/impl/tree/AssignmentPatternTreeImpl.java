package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.AssignmentPatternTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.PatternTree;

public class AssignmentPatternTreeImpl extends AbstractTree implements AssignmentPatternTree {
	protected final PatternTree left;
	protected final ExpressionTree right;
	public AssignmentPatternTreeImpl(long start, long end, PatternTree left, ExpressionTree right) {
		super(start, end);
		this.left = left;
		this.right = right;
	}

	@Override
	public PatternTree getLeft() {
		return left;
	}
	
	@Override
	public ExpressionTree getRight() {
		return right;
	}
	
}
