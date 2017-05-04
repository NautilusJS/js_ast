package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class InterfacePropertyTreeImpl extends AbstractTypedPropertyTree implements InterfacePropertyTree {
	protected final boolean optional;
	
	public InterfacePropertyTreeImpl(long start, long end, boolean readonly, IdentifierTree name, boolean optional, TypeTree type) {
		super(start, end, readonly, name, type);
		this.optional = optional;
	}
	
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}
