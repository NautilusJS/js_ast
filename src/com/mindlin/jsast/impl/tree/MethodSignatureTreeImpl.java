package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.DecoratorTree;
import com.mindlin.jsast.tree.MethodSignatureTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PropertyName;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class MethodSignatureTreeImpl extends AbstractFunctionTree implements MethodSignatureTree {

	public MethodSignatureTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyName name,
			List<TypeParameterDeclarationTree> typeParams, List<ParameterTree> parameters, TypeTree returnType) {
		super(start, end, modifiers, name, typeParams, parameters, returnType, null);
	}

	@Override
	public List<DecoratorTree> getDecorators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isArrow() {
		return false;
	}
	
}
