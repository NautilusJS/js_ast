package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ObjectLiteralElement;
import com.mindlin.jsast.tree.ObjectLiteralTree;

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
