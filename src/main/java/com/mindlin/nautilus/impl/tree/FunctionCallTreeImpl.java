package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.FunctionCallTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class FunctionCallTreeImpl extends AbstractTree implements FunctionCallTree {
	protected final ExpressionTree functionSelect;
	protected final List<TypeTree> typeArguments;
	protected final List<? extends ExpressionTree> arguments;
	
	public FunctionCallTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree functionSelect, List<TypeTree> typeArguments, List<? extends ExpressionTree> args) {
		super(start, end);
		this.functionSelect = functionSelect;
		this.typeArguments = typeArguments;
		this.arguments = args;
	}
	
	@Override
	public ExpressionTree getCallee() {
		return this.functionSelect;
	}

	@Override
	public List<TypeTree> getTypeArguments() {
		return this.typeArguments;
	}
	
	@Override
	public List<? extends ExpressionTree> getArguments() {
		return this.arguments;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getCallee(), getTypeArguments(), getArguments());
	}
	
}
