package com.mindlin.jsast.impl.tree;

public class SSAIdentifierTreeImpl extends RenamedIdentifierTreeImpl {
	protected final int id;
	public SSAIdentifierTreeImpl(long start, long end, String name, String sourceName, int id) {
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
