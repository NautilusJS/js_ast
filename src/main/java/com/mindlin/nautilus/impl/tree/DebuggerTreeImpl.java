package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.DebuggerTree;

public class DebuggerTreeImpl extends AbstractTree implements DebuggerTree {
	public DebuggerTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
}
