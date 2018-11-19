package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class IndexSignatureTreeImpl extends AbstractTree implements IndexSignatureTree {
	protected final Modifiers modifiers;
	protected final TypeParameterDeclarationTree idxType;
	protected final TypeTree returnType;
	
	public IndexSignatureTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, TypeParameterDeclarationTree idxType, TypeTree returnType) {
		super(start, end);
		this.modifiers = modifiers;
		this.idxType = idxType;
		this.returnType = returnType;
	}
	
	@Override
	public TypeParameterDeclarationTree getIndexType() {
		return this.idxType;
	}
	
	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}

	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getIndexType(), getReturnType());
	}
	
}
