package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.WhileLoopTree;

public class WhileLoopTreeImpl extends AbstractConditionalLoopTree implements WhileLoopTree {

	public WhileLoopTreeImpl(long start, long end, ExpressionTree condition, StatementTree statement) {
		super(start, end, condition, statement);
	}
	
	@Override
	public String toString() {
		return new StringBuilder("while (")
			.append(getCondition())
			.append(')')
			.append(getStatement())
			.toString();
	}
}
