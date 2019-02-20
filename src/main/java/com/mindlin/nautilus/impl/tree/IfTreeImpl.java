package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.IfTree;
import com.mindlin.nautilus.tree.StatementTree;

public class IfTreeImpl extends AbstractTree implements IfTree {
	protected final ExpressionTree expression;
	protected final StatementTree thenStatement, elseStatement;
	public IfTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, StatementTree thenStatement, StatementTree elseStatement) {
		super(start, end);
		this.expression = expression;
		this.thenStatement = thenStatement;
		this.elseStatement = elseStatement;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}

	@Override
	public StatementTree getThenStatement() {
		return this.thenStatement;
	}

	//TODO support nicer else-if stuff (could make optimizations easier)
	@Override
	public StatementTree getElseStatement() {
		return this.elseStatement;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression(), getThenStatement(), getElseStatement());
	}
	
}
