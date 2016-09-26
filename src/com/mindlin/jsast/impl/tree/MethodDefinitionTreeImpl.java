package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;

public class MethodDefinitionTreeImpl extends ObjectLiteralPropertyTreeImpl implements MethodDefinitionTree {
	protected final MethodDefinitionType type;
	
	public MethodDefinitionTreeImpl(long start, long end, ObjectPropertyKeyTree name, FunctionExpressionTree value,
			MethodDefinitionType type) {
		super(start, end, name, value);
		this.type = type;
	}
	
	@Override
	public FunctionExpressionTree getValue() {
		return (FunctionExpressionTree) super.getValue();
	}
	
	@Override
	public MethodDefinitionType getPropertyType() {
		return type;
	}
}
