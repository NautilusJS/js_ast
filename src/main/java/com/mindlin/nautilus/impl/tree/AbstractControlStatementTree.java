package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ControlStatementTree;
import com.mindlin.jsast.tree.StatementTree;

public abstract class AbstractControlStatementTree extends AbstractTree implements ControlStatementTree {
	protected final StatementTree statement;
	
	public AbstractControlStatementTree(SourcePosition start, SourcePosition end, StatementTree statement) {
		super(start, end);
		this.statement = statement;
	}
	
	@Override
	public StatementTree getStatement() {
		return this.statement;
	}
	
}
