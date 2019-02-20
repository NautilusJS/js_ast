package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.StatementTree;
import com.mindlin.nautilus.tree.WithTree;

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
