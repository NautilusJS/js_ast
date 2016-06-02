package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.StatementTree;

public abstract class AbstractLoopTree extends AbstractTree implements LoopTree {
	protected final StatementTree statement;
	protected AbstractLoopTree(long start, long end, StatementTree statement) {
		super(start, end);
		this.statement = statement;
	}

	@Override
	public StatementTree getStatement() {
		return this.statement;
	}
	
}
