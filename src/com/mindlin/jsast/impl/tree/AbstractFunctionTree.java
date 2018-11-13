package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.FunctionDeclarationTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.FunctionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public abstract class AbstractFunctionTree extends AbstractTree implements FunctionTree {
	protected final StatementTree body;
	protected final IdentifierTree name;
	protected final List<TypeParameterDeclarationTree> generics;
	protected final List<ParameterTree> parameters;
	protected final TypeTree returnType;
	protected final boolean strict;
	protected final boolean generator;
	protected final boolean isAsync;

	public AbstractFunctionTree(SourcePosition start, SourcePosition end, boolean isAsync, IdentifierTree name, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType,
			StatementTree body, boolean strict, boolean generator) {
		super(start, end);
		this.isAsync = isAsync;
		this.name = name;
		this.generics = generics;
		this.parameters = parameters;
		this.returnType = returnType;
		this.body = body;
		this.strict = strict;
		this.generator = generator;
	}

	@Override
	public StatementTree getBody() {
		return body;
	}

	@Override
	public IdentifierTree getName() {
		return name;
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
	public boolean isStrict() {
		return strict;
	}

	@Override
	public boolean isGenerator() {
		return generator;
	}

	@Override
	public boolean isAsync() {
		return this.isAsync;
	}

	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
	@Override
	protected int hash() {
		//TODO hash isStrict()?
		return Objects.hash(getKind(), isAsync(), getName(), getParameters(), getReturnType(), isArrow(), getBody(), isStrict(), isGenerator());
	}
	
	public static class FunctionExpressionTreeImpl extends AbstractFunctionTree implements FunctionExpressionTree {
		protected final boolean arrow;
		
		public FunctionExpressionTreeImpl(SourcePosition start, SourcePosition end, boolean isAsync, IdentifierTree name, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType, boolean arrow,
				StatementTree body, boolean strict, boolean generator) {
			super(start, end, isAsync, name, generics, parameters, returnType, body, strict, generator);
			
			this.arrow = arrow;
		}

		@Override
		public boolean isArrow() {
			return arrow;
		}
	}
	
	public static class FunctionDeclarationTreeImpl extends AbstractFunctionTree implements FunctionDeclarationTree {
		public FunctionDeclarationTreeImpl(SourcePosition start, SourcePosition end, boolean isAsync, IdentifierTree name, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType,
				StatementTree body, boolean strict, boolean generator) {
			super(start, end, isAsync, name, generics, parameters, returnType, body, strict, generator);
		}
	}
}
