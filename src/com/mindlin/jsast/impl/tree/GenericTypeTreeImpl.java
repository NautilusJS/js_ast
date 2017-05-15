package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.type.GenericTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class GenericTypeTreeImpl extends AbstractTypeTree implements GenericTypeTree {
	protected final IdentifierTree name;
	protected final TypeTree supertype;
	
	public GenericTypeTreeImpl(long start, long end, boolean implicit, IdentifierTree name, TypeTree supertype) {
		super(start, end, implicit);
		this.name = name;
		this.supertype = supertype;
	}
	
	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
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
	protected int hash() {
		return Objects.hash(getKind(), getName(), getSupertype());
	}
	
}
