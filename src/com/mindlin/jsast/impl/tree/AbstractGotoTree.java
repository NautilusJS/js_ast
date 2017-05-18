package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.ContinueTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.IdentifierTree;

public abstract class AbstractGotoTree extends AbstractTree implements GotoTree {
	protected final IdentifierTree label;

	public AbstractGotoTree(long start, long end, IdentifierTree label) {
		super(start, end);
		this.label = label;
	}
	
	@Override
	public IdentifierTree getLabel() {
		return this.label;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getLabel());
	}

	public static class ContinueTreeImpl extends AbstractGotoTree implements ContinueTree {
		public ContinueTreeImpl(long start, long end, IdentifierTree label) {
			super(start, end, label);
		}
	}

	public static class BreakTreeImpl extends AbstractGotoTree implements BreakTree {
		public BreakTreeImpl(long start, long end, IdentifierTree label) {
			super(start, end, label);
		}
	}
}
