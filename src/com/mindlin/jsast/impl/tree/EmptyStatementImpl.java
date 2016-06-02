package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.EmptyStatementTree;

public class EmptyStatementImpl extends AbstractTree implements EmptyStatementTree {
	
	public EmptyStatementImpl(long start, long end) {
		super(start, end);
	}
	
}
