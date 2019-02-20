package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.type.MappedTypeTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class MappedTypeTreeImpl extends AbstractTree implements MappedTypeTree {
	protected final Modifiers modifiers;
	protected final TypeParameterDeclarationTree parameter;
	protected final TypeTree type;
	
	public MappedTypeTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, TypeParameterDeclarationTree parameter, TypeTree type) {
		super(start, end);
		this.modifiers = modifiers;
		this.parameter = parameter;
		this.type = type;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	public TypeParameterDeclarationTree getParameter() {
		return this.parameter;
	}
	
	@Override
	public TypeTree getType() {
		return this.type;
	}
	
}
