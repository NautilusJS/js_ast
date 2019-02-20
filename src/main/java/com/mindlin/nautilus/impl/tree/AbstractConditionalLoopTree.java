package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ConditionalLoopTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;

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
