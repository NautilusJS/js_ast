package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.GenericTypeTree;

public class FunctionTypeTreeImpl extends AbstractTypeTree implements FunctionTypeTree {
	protected final List<ParameterTree> parameters;
	protected final List<GenericTypeTree> generics;
	protected final TypeTree returnType;
	
	public FunctionTypeTreeImpl(long start, long end, boolean implicit, List<ParameterTree> parameters, List<GenericTypeTree> generics, TypeTree returnType) {
		super(start, end, implicit);
		this.parameters = parameters;
		this.generics = generics;
		this.returnType = returnType;
	}

	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParameterTree> getParameters() {
		return this.parameters;
	}

	@Override
	public List<GenericTypeTree> getGenerics() {
		return this.generics;
	}

	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
}
