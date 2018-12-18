package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

public class VariableDeclarationTreeImpl extends AbstractTree implements VariableDeclarationTree {
	protected final VariableDeclarationKind style;
	protected final List<VariableDeclaratorTree> declarations;
	public VariableDeclarationTreeImpl(SourcePosition start, SourcePosition end, VariableDeclarationKind style, List<VariableDeclaratorTree> declarations) {
		super(start, end);
		this.style = style;
		this.declarations = declarations;
	}

	@Override
	public VariableDeclarationKind getDeclarationStyle() {
		return this.style;
	}
	
	@Override
	public List<VariableDeclaratorTree> getDeclarations() {
		return declarations;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getDeclarationStyle(), getDeclarations());
	}
	
}
