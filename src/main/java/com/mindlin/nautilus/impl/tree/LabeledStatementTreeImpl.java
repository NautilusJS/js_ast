package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.LabeledStatementTree;
import com.mindlin.nautilus.tree.StatementTree;

public class LabeledStatementTreeImpl extends AbstractControlStatementTree implements LabeledStatementTree {
	protected final IdentifierTree name;
	
	public LabeledStatementTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree name, StatementTree statement) {
		super(start, end, statement);
		this.name = name;
	}
	
	@Override
	public IdentifierTree getName() {
		return this.name;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getStatement());
	}
	
}
