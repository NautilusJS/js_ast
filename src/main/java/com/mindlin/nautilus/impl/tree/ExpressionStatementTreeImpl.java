package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionStatementTree;
import com.mindlin.nautilus.tree.ExpressionTree;

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
