package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class FunctionTypeTreeImpl extends AbstractTypeTree implements FunctionTypeTree {
	protected final List<ParameterTree> parameters;
	protected final List<TypeParameterDeclarationTree> typeParameters;
	protected final TypeTree returnType;
	
	public FunctionTypeTreeImpl(SourcePosition start, SourcePosition end, List<ParameterTree> parameters, List<TypeParameterDeclarationTree> typeParameters, TypeTree returnType) {
		super(start, end);
		this.parameters = parameters;
		this.typeParameters = typeParameters;
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
	public List<TypeParameterDeclarationTree> getGenerics() {
		return this.typeParameters;
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
