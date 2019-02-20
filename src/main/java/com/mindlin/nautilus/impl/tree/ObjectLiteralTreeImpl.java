package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ObjectLiteralElement;
import com.mindlin.nautilus.tree.ObjectLiteralTree;

public class ObjectLiteralTreeImpl extends AbstractTree implements ObjectLiteralTree {
	List<? extends ObjectLiteralElement> properties;
	
	public ObjectLiteralTreeImpl(SourcePosition start, SourcePosition end, List<? extends ObjectLiteralElement> properties) {
		super(start, end);
		this.properties = properties;
	}
	
	@Override
	public List<? extends ObjectLiteralElement> getProperties() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getProperties());
	}
	
}
