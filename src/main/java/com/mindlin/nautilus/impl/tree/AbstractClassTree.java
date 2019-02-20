package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ClassElementTree;
import com.mindlin.jsast.tree.ClassTreeBase;
import com.mindlin.jsast.tree.DecoratorTree;
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;

public abstract class AbstractClassTree extends AbstractTree implements ClassTreeBase {
	protected final Modifiers modifiers;
	protected final IdentifierTree name;
	protected final List<TypeParameterDeclarationTree> typeParameters;
	protected final List<HeritageClauseTree> heritage;
	protected final List<ClassElementTree> members;
	
	public AbstractClassTree(SourcePosition start, SourcePosition end, Modifiers modifiers, IdentifierTree name,
			List<TypeParameterDeclarationTree> typeParams, List<HeritageClauseTree> heritage,
			List<ClassElementTree> members) {
		super(start, end);
		this.modifiers = modifiers;
		this.name = name;
		this.typeParameters = typeParams;
		this.heritage = heritage;
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
	public List<HeritageClauseTree> getHeritage() {
		return this.heritage;
	}
	
	@Override
	public List<ClassElementTree> getProperties() {
		return this.members;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getName(), getTypeParameters(), getHeritage(), getProperties());
	}
	
	public static class ClassDeclarationTreeImpl extends AbstractClassTree implements ClassDeclarationTree {
		public ClassDeclarationTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				IdentifierTree name, List<TypeParameterDeclarationTree> typeParams, List<HeritageClauseTree> heritage,
				List<ClassElementTree> members) {
			super(start, end, modifiers, name, typeParams, heritage, members);
		}

		@Override
		public List<DecoratorTree> getDecorators() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class ClassExpressionTreeImpl extends AbstractClassTree implements ClassExpressionTree {
		public ClassExpressionTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				IdentifierTree name, List<TypeParameterDeclarationTree> typeParams, List<HeritageClauseTree> heritage,
				List<ClassElementTree> members) {
			super(start, end, modifiers, name, typeParams, heritage, members);
		}
	}
}
