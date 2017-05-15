package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.NewTree;

public class NewTreeImpl extends AbstractTree implements NewTree {
	protected final ExpressionTree callee;
	protected final List<ExpressionTree> arguments;
	
	public NewTreeImpl(long start, long end, ExpressionTree callee, List<ExpressionTree> arguments) {
		super(start, end);
		this.callee = callee;
		this.arguments = arguments;
	}

	@Override
	public ExpressionTree getCallee() {
		return callee;
	}

	@Override
	public List<ExpressionTree> getArguments() {
		return arguments;
	}

	@Override
	public int hash() {
		return Objects.hash(getKind(), getCallee(), getArguments());
	}
}
