package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionStatementTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ExpressionStatementTreeImpl extends AbstractTree implements ExpressionStatementTree {
	protected final ExpressionTree expression;
	
	public ExpressionStatementTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expr) {
		super(start, end);
		this.expression = expr;
	}
	public ExpressionStatementTreeImpl(ExpressionTree expr) {
		this(expr.getStart(), expr.getEnd(), expr);
	}

	@Override
	public ExpressionTree getExpression() {
		return expression;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression());
	}
	
}
