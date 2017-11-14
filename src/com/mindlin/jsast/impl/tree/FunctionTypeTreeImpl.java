package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.GenericParameterTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class FunctionTypeTreeImpl extends AbstractTypeTree implements FunctionTypeTree {
	protected final List<ParameterTree> parameters;
	protected final List<GenericParameterTree> generics;
	protected final TypeTree returnType;
	
	public FunctionTypeTreeImpl(long start, long end, boolean implicit, List<ParameterTree> parameters, List<GenericParameterTree> generics, TypeTree returnType) {
		super(start, end, implicit);
		this.parameters = parameters;
		this.generics = generics;
		this.returnType = returnType;
	}
	
	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<ParameterTree> getParameters() {
		return this.parameters;
	}
	
	@Override
	public List<GenericParameterTree> getGenerics() {
		return this.generics;
	}
	
	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getParameters(), getGenerics(), getReturnType());
	}
}
