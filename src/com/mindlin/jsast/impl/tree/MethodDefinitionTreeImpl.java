package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.TypeTree;

public class MethodDefinitionTreeImpl extends ClassPropertyTreeImpl<FunctionExpressionTree> implements MethodDefinitionTree {
	
	public MethodDefinitionTreeImpl(long start, long end, boolean readonly, boolean isStatic, AccessModifier access, PropertyDeclarationType declaration, ObjectPropertyKeyTree key, TypeTree type, FunctionExpressionTree value) {
		super(start, end, readonly, isStatic, access, declaration, key, type, value);
	}
}
