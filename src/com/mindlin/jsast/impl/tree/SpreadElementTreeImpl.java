package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.SpreadElementTree;

public class SpreadElementTreeImpl extends AbstractTree implements SpreadElementTree {
	protected final ExpressionTree expr;
	
	public SpreadElementTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expr) {
		super(start, end);
		this.expr = expr;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expr;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression());
	}
}
