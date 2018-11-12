package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ObjectLiteralPropertyTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;

public class ObjectLiteralTreeImpl extends AbstractTree implements ObjectLiteralTree {
	List<? extends ObjectLiteralPropertyTree> properties;
	
	public ObjectLiteralTreeImpl(SourcePosition start, SourcePosition end, List<? extends ObjectLiteralPropertyTree> properties) {
		super(start, end);
		this.properties = properties;
	}
	
	@Override
	public List<? extends ObjectLiteralPropertyTree> getProperties() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getProperties());
	}
	
}
