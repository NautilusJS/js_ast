package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.type.InterfaceTypeTree;

public class InterfaceTypeTreeImpl extends AbstractTypeTree implements InterfaceTypeTree {
	protected final List<InterfacePropertyTree> properties;
	
	public InterfaceTypeTreeImpl(long start, long end, boolean implicit, List<InterfacePropertyTree> properties) {
		super(start, end, implicit);
		this.properties = properties;
	}
	
	@Override
	public List<InterfacePropertyTree> getProperties() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getProperties());
	}
	
}
