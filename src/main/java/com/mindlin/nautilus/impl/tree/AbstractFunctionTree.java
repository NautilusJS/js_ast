package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.DecoratorTree;
import com.mindlin.nautilus.tree.FunctionDeclarationTree;
import com.mindlin.nautilus.tree.FunctionExpressionTree;
import com.mindlin.nautilus.tree.FunctionTree;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.MethodDeclarationTree;
import com.mindlin.nautilus.tree.Modifiers;
import com.mindlin.nautilus.tree.ParameterTree;
import com.mindlin.nautilus.tree.PropertyName;
import com.mindlin.nautilus.tree.StatementTree;
import com.mindlin.nautilus.tree.type.TypeParameterDeclarationTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public abstract class AbstractFunctionTree extends AbstractTree implements FunctionTree {
	protected final Modifiers modifiers;
	protected final StatementTree body;
	protected final PropertyName name;
	protected final List<TypeParameterDeclarationTree> typeParameters;
	protected final List<ParameterTree> parameters;
	protected final TypeTree returnType;

	public AbstractFunctionTree(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyName name,
			List<TypeParameterDeclarationTree> typeParams, List<ParameterTree> parameters, TypeTree returnType,
			StatementTree body) {
		super(start, end);
		this.modifiers = modifiers;
		this.name = name;
		this.typeParameters = typeParams;
		this.parameters = parameters;
		this.returnType = returnType;
		this.body = body;
	}

	@Override
	public StatementTree getBody() {
		return body;
	}

	@Override
	public PropertyName getName() {
		return this.name;
	}
	
	@Override
	public List<TypeParameterDeclarationTree> getTypeParameters() {
		return this.typeParameters;
	}

	@Override
	public List<ParameterTree> getParameters() {
		return parameters;
	}

	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	protected int hash() {
		//TODO hash isStrict()?
		return Objects.hash(getKind(), getModifiers(), getName(), getParameters(), getReturnType(), isArrow(), getBody());
	}
	
	public static class FunctionExpressionTreeImpl extends AbstractFunctionTree implements FunctionExpressionTree {
		protected final boolean arrow;
		
		public FunctionExpressionTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				PropertyName name, List<TypeParameterDeclarationTree> typeParams, List<ParameterTree> parameters,
				TypeTree returnType, boolean arrow,	StatementTree body) {
			super(start, end, modifiers, name, typeParams, parameters, returnType, body);
			
			this.arrow = arrow;
		}

		@Override
		public boolean isArrow() {
			return arrow;
		}
	}
	
	public static class FunctionDeclarationTreeImpl extends AbstractFunctionTree implements FunctionDeclarationTree {
		public FunctionDeclarationTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				IdentifierTree name, List<TypeParameterDeclarationTree> typeParams, List<ParameterTree> parameters,
				TypeTree returnType, StatementTree body) {
			super(start, end, modifiers, name, typeParams, parameters, returnType, body);
		}
		
		@Override
		public IdentifierTree getName() {
			return (IdentifierTree) super.getName();
		}

		@Override
		public List<DecoratorTree> getDecorators() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class MethodDeclarationTreeImpl extends AbstractFunctionTree implements MethodDeclarationTree {

		public MethodDeclarationTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				PropertyName name, List<TypeParameterDeclarationTree> typeParams, List<ParameterTree> parameters,
				TypeTree returnType, StatementTree body) {
			super(start, end, modifiers, name, typeParams, parameters, returnType, body);
		}

		@Override
		public List<DecoratorTree> getDecorators() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isArrow() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
