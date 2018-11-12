package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class MethodDefinitionTreeImpl extends ClassPropertyTreeImpl<FunctionExpressionTree> implements MethodDefinitionTree {
	
	public MethodDefinitionTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyDeclarationType declaration, ObjectPropertyKeyTree key, TypeTree type, FunctionExpressionTree value) {
		super(start, end, modifiers, declaration, key, type, value);
	}
	
	@Override
	protected int hash() {
		return super.hash() * 31 + getModifiers().hashCode();
	}
}
