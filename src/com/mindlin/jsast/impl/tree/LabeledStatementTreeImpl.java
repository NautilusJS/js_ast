package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.StatementTree;

public class LabeledStatementTreeImpl extends AbstractTree implements LabeledStatementTree {
	protected final IdentifierTree name;
	protected final StatementTree statement;
	public LabeledStatementTreeImpl(long start, long end, IdentifierTree name, StatementTree statement) {
		super(start, end);
		this.name = name;
		this.statement = statement;
	}

	@Override
	public StatementTree getStatement() {
		return this.statement;
	}

	@Override
	public IdentifierTree getName() {
		return this.name;
	}
	
}
