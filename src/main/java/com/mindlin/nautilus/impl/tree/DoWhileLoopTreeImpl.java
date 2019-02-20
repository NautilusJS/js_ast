package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.DoWhileLoopTree;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.StatementTree;

public class DoWhileLoopTreeImpl extends AbstractConditionalLoopTree implements DoWhileLoopTree {
	public DoWhileLoopTreeImpl(SourcePosition start, SourcePosition end, StatementTree statement, ExpressionTree condition) {
		super(start, end, condition, statement);
	}
}
