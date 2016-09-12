package com.mindlin.jsast.impl.tree;

import java.util.Collections;
import java.util.List;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.StatementTree;

public class FunctionExpressionTreeImpl extends AbstractTree implements FunctionExpressionTree {
	final StatementTree body;
	final String name;
	final List<ParameterTree> parameters;
	final boolean strict;
	final boolean arrow;

	public FunctionExpressionTreeImpl(long start, long end, List<ParameterTree> parameters, String name, boolean arrow,
			StatementTree body, boolean strict) {
		super(start, end);
		this.parameters = Collections.unmodifiableList(parameters);
		this.name = name;
		this.arrow = arrow;
		this.body = body;
		this.strict = strict;
	}

	@Override
	public StatementTree getBody() {
		return body;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<ParameterTree> getParameters() {
		return parameters;
	}

	@Override
	public boolean isStrict() {
		return strict;
	}

	@Override
	public boolean isArrow() {
		return arrow;
	}

}
