package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.LoopTree;
import com.mindlin.nautilus.tree.StatementTree;

//TODO deprecate?
public abstract class AbstractLoopTree extends AbstractControlStatementTree implements LoopTree {
	protected AbstractLoopTree(SourcePosition start, SourcePosition end, StatementTree statement) {
		super(start, end, statement);
	}
}
