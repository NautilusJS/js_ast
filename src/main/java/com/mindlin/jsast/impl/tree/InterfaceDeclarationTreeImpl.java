package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.TypeElementTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;

public class InterfaceDeclarationTreeImpl extends AbstractTree implements InterfaceDeclarationTree {
	protected final IdentifierTree name;
	protected final List<TypeParameterDeclarationTree> generics;
	protected final List<HeritageClauseTree> heritage;
	protected final List<TypeElementTree> properties;
	
	public InterfaceDeclarationTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree name, List<TypeParameterDeclarationTree> generics,
			List<HeritageClauseTree> heritage, List<TypeElementTree> properties) {
		super(start, end);
		this.name = name;
		this.generics = generics;
		this.heritage = heritage;
		this.properties = properties;
	}
	
	@Override
	public IdentifierTree getName() {
		return this.name;
	}

	@Override
	public List<TypeParameterDeclarationTree> getTypeParameters() {
		return this.generics;
	}
	
	@Override
	public List<HeritageClauseTree> getHeritage() {
		return this.heritage;
	}
	
	@Override
	public List<TypeElementTree> getDeclaredMembers() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getHeritage(), getDeclaredMembers());
	}
}
