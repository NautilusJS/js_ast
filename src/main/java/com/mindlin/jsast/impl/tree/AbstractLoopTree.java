package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.StatementTree;

//TODO deprecate?
public abstract class AbstractLoopTree extends AbstractControlStatementTree implements LoopTree {
	protected AbstractLoopTree(SourcePosition start, SourcePosition end, StatementTree statement) {
		super(start, end, statement);
	}
}
