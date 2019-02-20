package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.type.TypeTree;

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
