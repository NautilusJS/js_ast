package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.type.ArrayTypeTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class ArrayTypeTreeImpl extends AbstractTypeTree implements ArrayTypeTree {
	protected final TypeTree baseType;
	
	public ArrayTypeTreeImpl(SourcePosition start, SourcePosition end, TypeTree baseType) {
		super(start, end);
		this.baseType = baseType;
	}

	@Override
	public TypeTree getBaseType() {
		return this.baseType;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getBaseType());
	}
	
}
