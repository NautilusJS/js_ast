package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.StatementTree;

public class ForLoopTreeImpl extends AbstractConditionalLoopTree implements ForLoopTree {
	protected final StatementTree initializer;
	protected final ExpressionTree update;
	
	public ForLoopTreeImpl(SourcePosition start, SourcePosition end, StatementTree initializer, ExpressionTree condition, ExpressionTree update, StatementTree statement) {
		super(start, end, condition, statement);
		this.initializer = initializer;
		this.update = update;
	}
	
	@Override
	public StatementTree getInitializer() {
		return initializer;
	}
	
	@Override
	public ExpressionTree getUpdate() {
		return update;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getInitializer(), getCondition(), getUpdate(), getStatement());
	}
}
