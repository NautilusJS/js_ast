package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.NewTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class NewTreeImpl extends AbstractTree implements NewTree {
	protected final ExpressionTree callee;
	protected final List<TypeTree> typeArguments;
	protected final List<ExpressionTree> arguments;
	
	public NewTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree callee, List<TypeTree> typeArguments, List<ExpressionTree> arguments) {
		super(start, end);
		this.callee = callee;
		this.typeArguments = typeArguments;
		this.arguments = arguments;
	}

	@Override
	public ExpressionTree getCallee() {
		return callee;
	}

	@Override
	public List<TypeTree> getTypeArguments() {
		return typeArguments;
	}

	@Override
	public List<ExpressionTree> getArguments() {
		return arguments;
	}

	@Override
	protected int hash() {
		return Objects.hash(getKind(), getCallee(), getTypeArguments(), getArguments());
	}
}
