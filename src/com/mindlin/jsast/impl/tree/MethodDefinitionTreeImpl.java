package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;

public class MethodDefinitionTreeImpl extends ObjectLiteralPropertyTreeImpl implements MethodDefinitionTree {
	protected final boolean isStatic;
	protected final MethodDefinitionType type;
	public MethodDefinitionTreeImpl(long start, long end, IdentifierTree name, boolean isStatic, FunctionExpressionTree value, MethodDefinitionType type) {
		super(start, end, name, value);
		this.isStatic = isStatic;
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
	
	@Override
	public boolean isStatic() {
		return isStatic;
	}
	
}
