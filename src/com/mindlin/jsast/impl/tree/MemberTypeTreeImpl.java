package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.MemberTypeTree;

public class MemberTypeTreeImpl extends AbstractTypeTree implements MemberTypeTree {
	protected final TypeTree base;
	protected final TypeTree name;
	
	public MemberTypeTreeImpl(long start, long end, TypeTree base, TypeTree name, boolean implicit) {
		super(start, end, implicit);
		this.base = base;
		this.name = name;
	}
	
	public MemberTypeTreeImpl(TypeTree base, TypeTree name, boolean implicit) {
		this(base.getStart(), name.getEnd(), base, name, implicit);
	}
	
	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree getName() {
		return this.name;
	}

	@Override
	public TypeTree getBaseType() {
		return this.base;
	}
	
}
