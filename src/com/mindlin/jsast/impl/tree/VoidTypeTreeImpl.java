package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.type.VoidTypeTree;

public class VoidTypeTreeImpl extends AbstractTypeTree implements VoidTypeTree {
	public VoidTypeTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), false);
	}

	public VoidTypeTreeImpl(long start, long end, boolean implicit) {
		super(start, end, implicit);
	}
}
