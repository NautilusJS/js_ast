package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class InterfaceDeclarationTreeImpl extends AbstractTree implements InterfaceDeclarationTree {
	protected final IdentifierTree name;
	protected final List<TypeTree> supertypes;
	protected final List<InterfacePropertyTree> properties;
	
	public InterfaceDeclarationTreeImpl(long start, long end, IdentifierTree name, List<TypeTree> supertypes,
			List<InterfacePropertyTree> properties) {
		super(start, end);
		this.name = name;
		this.supertypes = supertypes;
		this.properties = properties;
	}
	
	@Override
	public IdentifierTree getIdentifier() {
		return this.name;
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
