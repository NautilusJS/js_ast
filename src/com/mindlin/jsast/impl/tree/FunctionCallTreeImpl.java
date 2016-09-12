package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.FunctionCallTree;

public class FunctionCallTreeImpl extends AbstractTree implements FunctionCallTree {
	protected final List<? extends ExpressionTree> arguments;
	protected final ExpressionTree functionSelect;
	public FunctionCallTreeImpl(long start, long end, List<? extends ExpressionTree> args, ExpressionTree functionSelect) {
		super(start, end);
		this.arguments = args;
		this.functionSelect = functionSelect;
	}

	@Override
	public List<? extends ExpressionTree> getArguments() {
		return this.arguments;
	}

	@Override
	public ExpressionTree getFunctionSelect() {
		return this.functionSelect;
	}
	
}
