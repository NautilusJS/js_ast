package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;

public class IdentifierTreeImpl extends AbstractTree implements IdentifierTree {
	protected final String name;
	
	public IdentifierTreeImpl(long start, long end, String name) {
		super(start, end);
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
