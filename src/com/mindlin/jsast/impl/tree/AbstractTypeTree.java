package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.TypeTree;

public abstract class AbstractTypeTree extends AbstractTree implements TypeTree {
	protected final boolean implicit;
	
	public AbstractTypeTree(SourcePosition start, SourcePosition end, boolean implicit) {
		super(start, end);
		this.implicit = implicit;
	}
	
	@Override
	public boolean isImplicit() {
		return this.implicit;
	}
	
}
