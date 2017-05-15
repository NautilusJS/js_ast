package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.TypeTree;

public class FunctionExpressionTreeImpl extends AbstractTree implements FunctionExpressionTree, StatementTree {
	protected final StatementTree body;
	protected final IdentifierTree name;
	protected final List<ParameterTree> parameters;
	protected final TypeTree returnType;
	protected final boolean strict;
	protected final boolean arrow;
	protected final boolean generator;
	protected final boolean isAsync;
	protected final boolean isStmt;

	public FunctionExpressionTreeImpl(long start, long end, boolean isAsync, IdentifierTree name, List<ParameterTree> parameters, TypeTree returnType, boolean arrow,
			StatementTree body, boolean strict, boolean generator) {
		super(start, end);
		this.isAsync = isAsync;
		this.name = name;
		this.parameters = parameters;
		this.returnType = returnType;
		this.arrow = arrow;
		this.body = body;
		this.strict = strict;
		this.generator = generator;
		this.isStmt = generator || (name != null);
	}

	@Override
	public StatementTree getBody() {
		return body;
	}

	@Override
	public IdentifierTree getName() {
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

	@Override
	public boolean isGenerator() {
		return generator;
	}

	@Override
	public Tree.Kind getKind() {
		return isStmt ? Kind.FUNCTION : Kind.FUNCTION_EXPRESSION;
	}

	@Override
	public boolean isAsync() {
		return this.isAsync;
	}

	@Override
	public TypeTree getReturnType() {
		return this.returnType;
	}
	
	@Override
	protected int hash() {
		//TODO hash isStrict()?
		return Objects.hash(getKind(), isAsync(), getName(), getParameters(), getReturnType(), isArrow(), getBody(), isStrict(), isGenerator());
	}
}
