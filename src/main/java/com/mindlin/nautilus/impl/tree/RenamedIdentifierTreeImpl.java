package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;

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
