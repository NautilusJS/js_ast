package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.TypeTree;

public class InterfacePropertyTreeImpl extends AbstractTree implements InterfacePropertyTree {
	protected final boolean optional;
	protected final IdentifierTree identifier;
	protected final TypeTree type;
	public InterfacePropertyTreeImpl(long start, long end, IdentifierTree name, boolean optional, TypeTree type) {
		super(start, end);
		this.identifier = name;
		this.optional = optional;
		this.type = type;
	}

	@Override
	public IdentifierTree getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	@Override
	public TypeTree getType() {
		return this.type;
	}
	
}
