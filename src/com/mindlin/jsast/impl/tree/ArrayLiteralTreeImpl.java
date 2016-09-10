package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ArrayLiteralTreeImpl extends AbstractTree implements ArrayLiteralTree {
	List<? extends ExpressionTree> elements;

	public ArrayLiteralTreeImpl(long start, long end, List<? extends ExpressionTree> elements) {
		super(start, end);
		this.elements = elements;
	}

	@Override
	public List<? extends ExpressionTree> getElements() {
		return elements;
	}

}
