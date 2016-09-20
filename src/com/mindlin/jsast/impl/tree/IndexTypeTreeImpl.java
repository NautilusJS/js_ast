package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.IndexTypeTree;

public class IndexTypeTreeImpl extends AbstractTypeTree implements IndexTypeTree {
	protected final TypeTree idxType;
	protected final TypeTree returnType;
	public IndexTypeTreeImpl(long start, long end, boolean implicit, TypeTree idxType, TypeTree returnType) {
		super(start, end, implicit);
		this.idxType = idxType;
		this.returnType = returnType;
	}

	@Override
	public TypeTree getIndexType() {
		return this.idxType;
	}
	
	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
}
