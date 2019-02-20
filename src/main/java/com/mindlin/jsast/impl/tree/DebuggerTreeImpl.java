package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.DebuggerTree;

public class DebuggerTreeImpl extends AbstractTree implements DebuggerTree {
	public DebuggerTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
}
