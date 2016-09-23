package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ArrayPatternTree;
import com.mindlin.jsast.tree.PatternTree;

public class ArrayPatternTreeImpl extends AbstractTree implements ArrayPatternTree {
	protected final List<PatternTree> elements;
	
	public ArrayPatternTreeImpl(long start, long end, List<PatternTree> elements) {
		super(start, end);
		this.elements = elements;
	}
	
	@Override
	public List<PatternTree> getElements() {
		return elements;
	}
	
}
