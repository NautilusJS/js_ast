package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class MethodDefinitionTreeImpl extends ClassPropertyTreeImpl<FunctionExpressionTree> implements MethodDefinitionTree {
	protected final boolean isAbstract;
	
	public MethodDefinitionTreeImpl(long start, long end, AccessModifier access, boolean isAbstract, boolean readonly, boolean isStatic, PropertyDeclarationType declaration, ObjectPropertyKeyTree key, TypeTree type, FunctionExpressionTree value) {
		super(start, end, access, readonly, isStatic, declaration, key, type, value);
		this.isAbstract = isAbstract;
	}
	
	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
	
	@Override
	protected int hash() {
		return super.hash() * 31 + Boolean.hashCode(isAbstract());
	}
}
