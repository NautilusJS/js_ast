package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.SequenceTree;

public class SequenceTreeImpl extends AbstractTree implements SequenceTree {
	protected final List<ExpressionTree> expressions;

	public SequenceTreeImpl(List<ExpressionTree> expressions) {
		this(expressions.get(0).getStart(), expressions.get(expressions.size() - 1).getEnd(), expressions);
	}

	public SequenceTreeImpl(long start, long end, List<ExpressionTree> expressions) {
		super(start, end);
		this.expressions = expressions;
	}

	@Override
	public List<ExpressionTree> getExpressions() {
		return expressions;
	}

}
