package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ClassElementTree;
import com.mindlin.jsast.tree.ClassTreeBase;
import com.mindlin.jsast.tree.DecoratorTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public abstract class AbstractClassTree extends AbstractTree implements ClassTreeBase {
	protected final Modifiers modifiers;
	protected final IdentifierTree name;
	protected final TypeTree superType;
	protected final List<TypeParameterDeclarationTree> typeParameters;
	protected final List<TypeTree> implementing;
	protected final List<ClassElementTree> members;
	
	public AbstractClassTree(SourcePosition start, SourcePosition end, Modifiers modifiers, IdentifierTree name,
			List<TypeParameterDeclarationTree> typeParams, TypeTree superType, List<TypeTree> implementing,
			List<ClassElementTree> members) {
		super(start, end);
		this.modifiers = modifiers;
		this.name = name;
		this.typeParameters = typeParams;
		this.superType = superType;
		this.implementing = implementing;
		this.members = members;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	public IdentifierTree getName() {
		return this.name;
	}

	@Override
	public List<TypeParameterDeclarationTree> getTypeParameters() {
		return this.typeParameters;
	}
	
	@Override
	public TypeTree getSuperType() {
		return superType;
	}
	
	@Override
	public List<TypeTree> getImplementing() {
		return this.implementing;
	}
	
	@Override
	public List<ClassElementTree> getProperties() {
		return this.members;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getName(), getTypeParameters(), getSuperType(), getImplementing(), getProperties());
	}
	
	public static class ClassDeclarationTreeImpl extends AbstractClassTree implements ClassDeclarationTree {
		public ClassDeclarationTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				IdentifierTree name, List<TypeParameterDeclarationTree> typeParams, TypeTree superType,
				List<TypeTree> implementing, List<ClassElementTree> members) {
			super(start, end, modifiers, name, typeParams, superType, implementing, members);
		}

		@Override
		public List<DecoratorTree> getDecorators() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class ClassExpressionTreeImpl extends AbstractClassTree implements ClassExpressionTree {
		public ClassExpressionTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				IdentifierTree name, List<TypeParameterDeclarationTree> typeParams, TypeTree superType,
				List<TypeTree> implementing, List<ClassElementTree> members) {
			super(start, end, modifiers, name, typeParams, superType, implementing, members);
		}
	}
}
