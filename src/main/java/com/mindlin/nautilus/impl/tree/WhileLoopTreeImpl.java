package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.StatementTree;
import com.mindlin.nautilus.tree.WhileLoopTree;

public class WhileLoopTreeImpl extends AbstractConditionalLoopTree implements WhileLoopTree {
	public WhileLoopTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree condition, StatementTree statement) {
		super(start, end, condition, statement);
	}
}
