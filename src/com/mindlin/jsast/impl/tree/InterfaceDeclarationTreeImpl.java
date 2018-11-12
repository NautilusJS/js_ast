package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.type.GenericParameterTree;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class InterfaceDeclarationTreeImpl extends AbstractTree implements InterfaceDeclarationTree {
	protected final IdentifierTree name;
	protected final List<GenericParameterTree> generics;
	protected final List<TypeTree> supertypes;
	protected final List<InterfacePropertyTree> properties;
	
	public InterfaceDeclarationTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree name, List<GenericParameterTree> generics,
			List<TypeTree> supertypes, List<InterfacePropertyTree> properties) {
		super(start, end);
		this.name = name;
		this.generics = generics;
		this.supertypes = supertypes;
		this.properties = properties;
	}
	
	@Override
	public IdentifierTree getIdentifier() {
		return this.name;
	}

	@Override
	public List<GenericParameterTree> getGenerics() {
		return this.generics;
	}
	
	@Override
	public List<TypeTree> getSupertypes() {
		return this.supertypes;
	}
	
	@Override
	public List<? extends InterfacePropertyTree> getProperties() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getIdentifier(), getSupertypes(), getProperties());
	}
}
