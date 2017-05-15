package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.type.KeyofTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class KeyofTypeTreeImpl extends AbstractTypeTree implements KeyofTypeTree {
	protected final TypeTree baseType;
	
	public KeyofTypeTreeImpl(long start, long end, boolean implicit, TypeTree baseType) {
		super(start, end, implicit);
		this.baseType = baseType;
	}
	
	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public TypeTree getBaseType() {
		return baseType;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getBaseType());
	}
	
}
