package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.type.TypeTree;

public class InterfacePropertyTreeImpl extends AbstractTypedPropertyTree implements InterfacePropertyTree {
	protected final boolean optional;
	
	public InterfacePropertyTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, IdentifierTree name, boolean optional, TypeTree type) {
		super(start, end, modifiers, name, type);
		this.optional = optional;
	}
	
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}
