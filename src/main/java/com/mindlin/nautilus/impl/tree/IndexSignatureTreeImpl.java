package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.Modifiers;
import com.mindlin.nautilus.tree.type.IndexSignatureTree;
import com.mindlin.nautilus.tree.type.TypeParameterDeclarationTree;
import com.mindlin.nautilus.tree.type.TypeTree;

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
