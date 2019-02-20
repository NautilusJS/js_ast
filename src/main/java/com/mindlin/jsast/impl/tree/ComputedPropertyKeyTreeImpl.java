package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ComputedPropertyKeyTreeImpl extends AbstractTree implements ComputedPropertyKeyTree {
	protected final ExpressionTree expression;
	
	public ComputedPropertyKeyTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
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
