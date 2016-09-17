package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.ContinueTree;
import com.mindlin.jsast.tree.GotoTree;

public abstract class AbstractGotoTree extends AbstractTree implements GotoTree {
	protected final String label;

	public AbstractGotoTree(long start, long end, String label) {
		super(start, end);
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public static class ContinueTreeImpl extends AbstractGotoTree implements ContinueTree {
		public ContinueTreeImpl(long start, long end, String label) {
			super(start, end, label);
		}
	}

	public static class BreakTreeImpl extends AbstractGotoTree implements BreakTree {
		public BreakTreeImpl(long start, long end, String label) {
			super(start, end, label);
		}
	}
}
