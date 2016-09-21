package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.FunctionCallTree;

public class FunctionCallTreeImpl extends AbstractTree implements FunctionCallTree {
	protected final ExpressionTree functionSelect;
	protected final List<? extends ExpressionTree> arguments;
	public FunctionCallTreeImpl(long start, long end, ExpressionTree functionSelect, List<? extends ExpressionTree> args) {
		super(start, end);
		this.functionSelect = functionSelect;
		this.arguments = args;
	}

	@Override
	public List<? extends ExpressionTree> getArguments() {
		return this.arguments;
	}

	@Override
	public ExpressionTree getCallee() {
		return this.functionSelect;
	}
	
}
