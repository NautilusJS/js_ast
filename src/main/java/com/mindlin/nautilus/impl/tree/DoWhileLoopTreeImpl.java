package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;

public class DoWhileLoopTreeImpl extends AbstractConditionalLoopTree implements DoWhileLoopTree {
	public DoWhileLoopTreeImpl(SourcePosition start, SourcePosition end, StatementTree statement, ExpressionTree condition) {
		super(start, end, condition, statement);
	}
}
