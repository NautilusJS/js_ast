package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.type.VoidTypeTree;

public class VoidTypeTreeImpl extends AbstractTree implements VoidTypeTree {
	protected final boolean implicit;

	public VoidTypeTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), false);
	}

	public VoidTypeTreeImpl(long start, long end, boolean implicit) {
		super(start, end);
		this.implicit = implicit;
	}

	@Override
	public boolean isImplicit() {
		return implicit;
	}

}
