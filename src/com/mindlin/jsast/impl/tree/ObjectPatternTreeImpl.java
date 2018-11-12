package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ObjectPatternPropertyTree;
import com.mindlin.jsast.tree.ObjectPatternTree;

public class ObjectPatternTreeImpl extends AbstractTree implements ObjectPatternTree {
	protected final List<ObjectPatternPropertyTree> properties;
	
	public ObjectPatternTreeImpl(SourcePosition start, SourcePosition end, List<ObjectPatternPropertyTree> properties) {
		super(start, end);
		this.properties = properties;
	}
	
	@Override
	public List<ObjectPatternPropertyTree> getProperties() {
		return properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getProperties());
	}
	
}
