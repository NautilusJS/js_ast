package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.MemberTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class MemberTypeTreeImpl extends AbstractTypeTree implements MemberTypeTree {
	protected final TypeTree base;
	protected final TypeTree name;
	
	public MemberTypeTreeImpl(SourcePosition start, SourcePosition end, TypeTree base, TypeTree name) {
		super(start, end);
		this.base = base;
		this.name = name;
	}
	
	public MemberTypeTreeImpl(TypeTree base, TypeTree name) {
		this(base.getStart(), name.getEnd(), base, name);
	}
	
	@Override
	public TypeTree getName() {
		return this.name;
	}

	@Override
	public TypeTree getBaseType() {
		return this.base;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getBaseType(), getName());
	}
	
}
