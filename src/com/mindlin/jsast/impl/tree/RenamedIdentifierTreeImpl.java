package com.mindlin.jsast.impl.tree;

public class RenamedIdentifierTreeImpl extends IdentifierTreeImpl {
	protected final String sourceName;
	
	public RenamedIdentifierTreeImpl(long start, long end, String name, String sourceName) {
		super(start, end, name);
		this.sourceName = sourceName;
	}
	
	@Override
	public String getSourceName() {
		return this.sourceName;
	}
}
