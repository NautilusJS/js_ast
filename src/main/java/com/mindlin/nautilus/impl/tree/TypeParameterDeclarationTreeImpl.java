package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.type.TypeParameterDeclarationTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class TypeParameterDeclarationTreeImpl extends AbstractTree implements TypeParameterDeclarationTree {
	protected final IdentifierTree name;
	protected final TypeTree supertype;
	protected final TypeTree defaultValue;
	
	public TypeParameterDeclarationTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree name, TypeTree supertype, TypeTree defaultValue) {
		super(start, end);
		this.name = name;
		this.supertype = supertype;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public IdentifierTree getName() {
		return this.name;
	}
	
	@Override
	public TypeTree getSupertype() {
		return this.supertype;
	}

	@Override
	public TypeTree getDefault() {
		return this.defaultValue;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getSupertype(), getDefault());
	}
}
