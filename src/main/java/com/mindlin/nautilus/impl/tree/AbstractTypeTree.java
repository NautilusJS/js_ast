package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.type.TypeTree;

public abstract class AbstractTypeTree extends AbstractTree implements TypeTree {
	public AbstractTypeTree(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
	
}
