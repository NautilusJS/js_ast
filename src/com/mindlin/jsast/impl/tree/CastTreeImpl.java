package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.CastTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class CastTreeImpl extends AbstractExpressiveExpressionTree implements CastTree {
	protected final TypeTree type;
	
	public CastTreeImpl(long start, long end, ExpressionTree expression, TypeTree type) {
		super(start, end, expression);
		this.type = type;
	}
	
	public CastTreeImpl(ExpressionTree expression, TypeTree type) {
		this(Math.min(expression.getStart(), type.getStart()), Math.max(expression.getEnd(), type.getEnd()), expression, type);
	}

	@Override
	public TypeTree getType() {
		return this.type;
	}
	
	@Override
	public int hash() {
		return Objects.hash(getKind(), getType(), getExpression());
	}
}
