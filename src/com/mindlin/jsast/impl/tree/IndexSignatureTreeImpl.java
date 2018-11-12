package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class IndexSignatureTreeImpl extends AbstractTypeTree implements IndexSignatureTree {
	protected final TypeTree idxType;
	protected final TypeTree returnType;
	
	public IndexSignatureTreeImpl(SourcePosition start, SourcePosition end, boolean implicit, TypeTree idxType, TypeTree returnType) {
		super(start, end, implicit);
		this.idxType = idxType;
		this.returnType = returnType;
	}
	
	@Override
	public TypeTree getIndexType() {
		return this.idxType;
	}
	
	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getIndexType(), getReturnType());
	}
	
}
