package com.mindlin.jsast.impl.tree;

import java.util.Optional;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;

public class VariableTreeImpl extends AbstractTree implements VariableDeclarationTree {
	protected final boolean isScoped, isConst;
	protected final IdentifierTree name;
	protected final ExpressionTree initializer;
	protected final TypeTree type;
	public VariableTreeImpl(long start, long end, boolean isScoped, boolean isConst, IdentifierTree name, TypeTree type, ExpressionTree initializer) {
		super(start, end);
		this.isScoped = isScoped;
		this.isConst = isConst;
		this.name = name;
		this.initializer = initializer;
		this.type = type;
	}

	@Override
	public Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATION;
	}
	
	@Override
	public IdentifierTree getName() {
		return name;
	}
	
	@Override
	public Optional<? extends ExpressionTree> getInitializer() {
		return Optional.ofNullable(initializer);
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
	public TypeTree getType() {
		return type;
	}
	
}
