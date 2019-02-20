package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;

public class SSAIdentifierTreeImpl extends RenamedIdentifierTreeImpl {
	protected final int id;
	public SSAIdentifierTreeImpl(SourcePosition start, SourcePosition end, String name, String sourceName, int id) {
		super(start, end, name, sourceName);
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public int hash() {
		return id;
	}
}
