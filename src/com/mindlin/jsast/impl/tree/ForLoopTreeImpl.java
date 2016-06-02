package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.StatementTree;

public class ForLoopTreeImpl extends AbstractConditionalLoopTree implements ForLoopTree {
	protected final ExpressionTree initializer, update;
	public ForLoopTreeImpl(long start, long end, ExpressionTree initializer, ExpressionTree condition, ExpressionTree update, StatementTree statement) {
		super(start, end, condition, statement);
		this.initializer = initializer;
		this.update = update;
	}

	@Override
	public ExpressionTree getInitializer() {
		return initializer;
	}

	@Override
	public ExpressionTree getUpdate() {
		return update;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("for (")
			.append(getInitializer())
			.append("; ").append(getCondition())
			.append("; ").append(getUpdate())
			.append(')')
			.append(getStatement())
			.toString();
	}
}
