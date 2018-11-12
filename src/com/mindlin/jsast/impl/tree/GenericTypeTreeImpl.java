package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.type.GenericParameterTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class GenericTypeTreeImpl extends AbstractTypeTree implements GenericParameterTree {
	protected final IdentifierTree name;
	protected final TypeTree supertype;
	protected final TypeTree defaultValue;
	
	public GenericTypeTreeImpl(SourcePosition start, SourcePosition end, boolean implicit, IdentifierTree name, TypeTree supertype, TypeTree defaultValue) {
		super(start, end, implicit);
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
