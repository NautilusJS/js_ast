package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.AssignmentPatternTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.PatternTree;

public class AssignmentPatternTreeImpl extends AbstractTree implements AssignmentPatternTree {
	protected final PatternTree left;
	protected final ExpressionTree right;
	
	public AssignmentPatternTreeImpl(SourcePosition start, SourcePosition end, PatternTree left, ExpressionTree right) {
		super(start, end);
		this.left = left;
		this.right = right;
	}
	
	@Override
	public PatternTree getValue() {
		return left;
	}
	
	@Override
	public ExpressionTree getInitializer() {
		return right;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue(), getInitializer());
	}
	
}
