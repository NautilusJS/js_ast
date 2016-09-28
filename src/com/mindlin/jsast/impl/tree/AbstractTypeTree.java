package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.TypeTree;

public abstract class AbstractTypeTree extends AbstractTree implements TypeTree {
	protected final boolean implicit;
	
	public AbstractTypeTree(long start, long end, boolean implicit) {
		super(start, end);
		this.implicit = implicit;
	}
	
	@Override
	public boolean isImplicit() {
		return this.implicit;
	}
	
}
