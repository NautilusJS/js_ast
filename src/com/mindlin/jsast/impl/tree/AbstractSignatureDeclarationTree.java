package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PropertyName;
import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.type.ConstructorTypeTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public abstract class AbstractSignatureDeclarationTree extends AbstractTree implements SignatureDeclarationTree {
	protected final PropertyName name;
	protected final List<TypeParameterDeclarationTree> generics;
	protected final List<ParameterTree> parameters;
	protected final TypeTree returnType;
	
	public AbstractSignatureDeclarationTree(SourcePosition start, SourcePosition end, PropertyName name, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType) {
		super(start, end);
		this.name = name;
		this.generics = generics;
		this.parameters = parameters;
		this.returnType = returnType;
	}

	@Override
	public PropertyName getName() {
		return this.name;
	}

	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<TypeParameterDeclarationTree> getTypeParameters() {
		return this.generics;
	}

	@Override
	public List<ParameterTree> getParameters() {
		return parameters;
	}

	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
	public static class CallSignatureTreeImpl extends AbstractSignatureDeclarationTree implements CallSignatureTree {
		public CallSignatureTreeImpl(SourcePosition start, SourcePosition end, PropertyName name, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType) {
			super(start, end, name, generics, parameters, returnType);
		}
	}
	
	public static class ConstructSignatureTreeImpl extends AbstractSignatureDeclarationTree implements ConstructSignatureTree {
		public ConstructSignatureTreeImpl(SourcePosition start, SourcePosition end, PropertyName name, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType) {
			super(start, end, name, generics, parameters, returnType);
		}
	}
	
	public static class FunctionTypeTreeImpl extends AbstractSignatureDeclarationTree implements FunctionTypeTree {
		public FunctionTypeTreeImpl(SourcePosition start, SourcePosition end, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType) {
			super(start, end, null, generics, parameters, returnType);
		}
	}
	
	public static class ConstructorTypeTreeImpl extends AbstractSignatureDeclarationTree implements ConstructorTypeTree {
		public ConstructorTypeTreeImpl(SourcePosition start, SourcePosition end, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType) {
			super(start, end, null, generics, parameters, returnType);
		}
	}
}
