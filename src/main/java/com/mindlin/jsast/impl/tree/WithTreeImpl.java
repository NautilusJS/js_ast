package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.WithTree;

public class WithTreeImpl extends AbstractControlStatementTree implements WithTree {
	protected final ExpressionTree scope;
	
	public WithTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree scope, StatementTree statement) {
		super(start, end, statement);
		this.scope = scope;
	}
	
	@Override
	public ExpressionTree getScope() {
		return this.scope;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getScope(), getStatement());
	}
	
}
