package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.AnyTypeTree;

public class AnyTypeTreeImpl extends AbstractTypeTree implements AnyTypeTree {
	
	public AnyTypeTreeImpl(SourcePosition start, SourcePosition end, boolean implicit) {
		super(start, end, implicit);
	}
	
}
