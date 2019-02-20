package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.BreakTree;
import com.mindlin.nautilus.tree.ContinueTree;
import com.mindlin.nautilus.tree.GotoTree;
import com.mindlin.nautilus.tree.IdentifierTree;

public abstract class AbstractGotoTree extends AbstractTree implements GotoTree {
	protected final IdentifierTree label;

	public AbstractGotoTree(SourcePosition start, SourcePosition end, IdentifierTree label) {
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
		public ContinueTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree label) {
			super(start, end, label);
		}
	}

	public static class BreakTreeImpl extends AbstractGotoTree implements BreakTree {
		public BreakTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree label) {
			super(start, end, label);
		}
	}
}
