package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;

public class RenamedIdentifierTreeImpl extends IdentifierTreeImpl {
	protected final String sourceName;
	
	public RenamedIdentifierTreeImpl(SourcePosition start, SourcePosition end, String name, String sourceName) {
		super(start, end, name);
		this.sourceName = sourceName;
	}
	
	@Override
	public String getSourceName() {
		return this.sourceName;
	}
}
