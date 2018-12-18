package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ArrayPatternTree;
import com.mindlin.jsast.tree.PatternTree;

public class ArrayPatternTreeImpl extends AbstractTree implements ArrayPatternTree {
	protected final List<PatternTree> elements;
	
	public ArrayPatternTreeImpl(SourcePosition start, SourcePosition end, List<PatternTree> elements) {
		super(start, end);
		this.elements = elements;
	}
	
	@Override
	public List<PatternTree> getElements() {
		return elements;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getElements());
	}
	
}
