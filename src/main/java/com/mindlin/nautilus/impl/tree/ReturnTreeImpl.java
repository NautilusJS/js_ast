package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.ReturnTree;

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
