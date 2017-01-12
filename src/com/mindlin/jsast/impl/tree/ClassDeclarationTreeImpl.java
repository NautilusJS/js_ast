package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Optional;

import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public class ClassDeclarationTreeImpl extends AbstractTree implements ClassDeclarationTree {
	protected final IdentifierTree name;
	protected final TypeTree superType;
	protected final List<TypeTree> implementing;
	protected final List<ClassPropertyTree> properties;
	
	public ClassDeclarationTreeImpl(long start, long end, IdentifierTree name, TypeTree superType, List<TypeTree> implementing, List<ClassPropertyTree> properties) {
		super(start, end);
		this.name = name;
		this.superType = superType;
		this.implementing = implementing;
		this.properties = properties;
	}
	
	@Override
	public IdentifierTree getIdentifier() {
		return name;
	}
	
	@Override
	public Optional<TypeTree> getSuperType() {
		return Optional.ofNullable(superType);
	}
	
	@Override
	public List<TypeTree> getImplementing() {
		return implementing;
	}
	
	@Override
	public List<ClassPropertyTree> getProperties() {
		return properties;
	}
	
}
