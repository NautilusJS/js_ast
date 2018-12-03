package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.UnaryTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class UnaryTypeTreeImpl extends AbstractTypeTree implements UnaryTypeTree {
	protected final Kind kind;
	protected final TypeTree baseType;
	
	public UnaryTypeTreeImpl(SourcePosition start, SourcePosition end, Kind kind, TypeTree baseType) {
		super(start, end);
		this.kind = kind;
		this.baseType = baseType;
	}
	
	@Override
	public Kind getKind() {
		return kind;
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
