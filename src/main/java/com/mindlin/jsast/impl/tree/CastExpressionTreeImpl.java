package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.CastExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class CastExpressionTreeImpl extends AbstractTree implements CastExpressionTree {
	protected final ExpressionTree expression;
	protected final TypeTree type;
	
	public CastExpressionTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, TypeTree type) {
		super(start, end);
		this.expression = expression;
		this.type = type;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	public TypeTree getType() {
		return this.type;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getType(), getExpression());
	}
}
