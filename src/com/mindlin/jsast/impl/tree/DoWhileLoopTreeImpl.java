package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;

public class DoWhileLoopTreeImpl extends AbstractConditionalLoopTree implements DoWhileLoopTree {
	public DoWhileLoopTreeImpl(long start, long end, StatementTree statement, ExpressionTree condition) {
		super(start, end, condition, statement);
	}
}
