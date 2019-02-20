package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.ThrowTree;

public class ThrowTreeImpl extends AbstractTree implements ThrowTree {
	protected final ExpressionTree expression;
	
	public ThrowTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
		super(start, end);
		this.expression = expression;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression());
	}
}
