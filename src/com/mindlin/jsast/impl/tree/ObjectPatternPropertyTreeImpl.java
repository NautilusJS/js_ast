package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ObjectPatternPropertyTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.PatternTree;

public class ObjectPatternPropertyTreeImpl extends AbstractTree implements ObjectPatternPropertyTree {
	
	protected final ObjectPropertyKeyTree key;
	protected final PatternTree value;
	
	public ObjectPatternPropertyTreeImpl(SourcePosition start, SourcePosition end, ObjectPropertyKeyTree key, PatternTree value) {
		super(start, end);
		this.key = key;
		this.value = value;
	}
	
	@Override
	public ObjectPropertyKeyTree getKey() {
		return key;
	}
	
	@Override
	public PatternTree getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getKey(), getValue());
	}
	
}
