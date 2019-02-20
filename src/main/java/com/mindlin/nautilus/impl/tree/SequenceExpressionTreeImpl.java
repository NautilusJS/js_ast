package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.SequenceExpressionTree;

public class SequenceExpressionTreeImpl extends AbstractTree implements SequenceExpressionTree {
	protected final List<ExpressionTree> elements;
	
	public SequenceExpressionTreeImpl(List<ExpressionTree> expressions) {
		this(expressions.get(0).getStart(), expressions.get(expressions.size() - 1).getEnd(), expressions);
	}
	
	public SequenceExpressionTreeImpl(SourcePosition start, SourcePosition end, List<ExpressionTree> expressions) {
		super(start, end);
		this.elements = expressions;
	}
	
	@Override
	public List<ExpressionTree> getElements() {
		return this.elements;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getElements());
	}
	
}
