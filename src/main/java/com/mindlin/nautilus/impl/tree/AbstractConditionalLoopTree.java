package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ConditionalLoopTree;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.StatementTree;

public abstract class AbstractConditionalLoopTree extends AbstractLoopTree implements ConditionalLoopTree {
	protected final ExpressionTree condition;
	
	protected AbstractConditionalLoopTree(SourcePosition start, SourcePosition end, ExpressionTree condition, StatementTree statement) {
		super(start, end, statement);
		this.condition = condition;
	}
	
	@Override
	public ExpressionTree getCondition() {
		return this.condition;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getCondition(), getStatement());
	}
	
}
