package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.type.AnyTypeTree;

public class AnyTypeTreeImpl extends AbstractTypeTree implements AnyTypeTree {
	
	public AnyTypeTreeImpl(long start, long end, boolean implicit) {
		super(start, end, implicit);
	}
	
}
