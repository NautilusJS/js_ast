package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ReturnTree;

public class ReturnTreeImpl extends AbstractTree implements ReturnTree {
	protected final ExpressionTree expression;
	
	public ReturnTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
		super(start, end);
		this.expression = expression;
	}
	
	/**
	 * For wrapping expression lambdas
	 * 
	 * @param expression
	 */
	public ReturnTreeImpl(ExpressionTree expression) {
		this(expression.getStart(), expression.getEnd(), expression);
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
