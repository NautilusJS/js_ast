package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ObjectPatternPropertyTree;
import com.mindlin.jsast.tree.PatternTree;

public class ObjectPatternPropertyTreeImpl extends AbstractTree implements ObjectPatternPropertyTree {
	
	protected final IdentifierTree key;
	protected final PatternTree value;
	
	public ObjectPatternPropertyTreeImpl(long start, long end, IdentifierTree key, PatternTree value) {
		super(start, end);
		this.key = key;
		this.value = value;
	}
	
	@Override
	public IdentifierTree getKey() {
		return key;
	}
	
	@Override
	public PatternTree getValue() {
		return value;
	}
	
}
