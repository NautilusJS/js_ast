package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ControlStatementTree;
import com.mindlin.nautilus.tree.StatementTree;

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
