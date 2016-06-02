package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public abstract class AbstractTree implements Tree {
	protected final long start, end;
	protected AbstractTree(long start, long end) {
		this.start = start;
		this.end = end;
	}
	@Override
	public long getStart() {
		return this.start;
	}

	@Override
	public long getEnd() {
		return this.end;
	}

	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		// TODO Auto-generated method stub
		return null;
	}

}
