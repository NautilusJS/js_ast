package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

public class VariableDeclarationTreeImpl extends AbstractTree implements VariableDeclarationTree {
	protected final boolean isScoped, isConst;
	protected final List<VariableDeclaratorTree> declarations;
	public VariableDeclarationTreeImpl(long start, long end, boolean isScoped, boolean isConst, List<VariableDeclaratorTree> declarations) {
		super(start, end);
		this.isScoped = isScoped;
		this.isConst = isConst;
		this.declarations = declarations;
	}

	@Override
	public Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATION;
	}
	
	@Override
	public boolean isScoped() {
		return isScoped;
	}
	
	@Override
	public boolean isConst() {
		return isConst;
	}
	
	@Override
	public List<VariableDeclaratorTree> getDeclarations() {
		return declarations;
	}
	
}
