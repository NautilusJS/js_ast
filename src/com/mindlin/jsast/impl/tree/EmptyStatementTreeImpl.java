package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.EmptyStatementTree;

public class EmptyStatementTreeImpl extends AbstractTree implements EmptyStatementTree {
	
	public EmptyStatementTreeImpl(long start, long end) {
		super(start, end);
	}
}
