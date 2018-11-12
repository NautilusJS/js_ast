package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;

public class ObjectTypeTreeImpl extends AbstractTypeTree implements ObjectTypeTree {
	protected final List<InterfacePropertyTree> properties;
	
	public ObjectTypeTreeImpl(SourcePosition start, SourcePosition end, boolean implicit, List<InterfacePropertyTree> properties) {
		super(start, end, implicit);
		this.properties = properties;
	}
	
	@Override
	public List<InterfacePropertyTree> getDeclaredProperties() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getDeclaredProperties());
	}

}
