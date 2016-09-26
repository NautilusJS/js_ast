package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ObjectLiteralPropertyTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;

public class ObjectLiteralPropertyTreeImpl extends AbstractTree implements ObjectLiteralPropertyTree {
	protected final ObjectPropertyKeyTree name;
	protected final ExpressionTree value;
	
	public ObjectLiteralPropertyTreeImpl(long start, long end, ObjectPropertyKeyTree name, ExpressionTree value) {
		super(start, end);
		this.name = name;
		this.value = value;
	}
	
	@Override
	public ObjectPropertyKeyTree getKey() {
		return name;
	}
	
	@Override
	public ExpressionTree getValue() {
		return value;
	}
	
}
