package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.DebuggerTree;

public class DebuggerTreeImpl extends AbstractTree implements DebuggerTree {
	public DebuggerTreeImpl(long start, long end) {
		super(start, end);
	}
	
	@Override
	protected int hash() {
		return Objects.hashCode(getKind());
	}
}
