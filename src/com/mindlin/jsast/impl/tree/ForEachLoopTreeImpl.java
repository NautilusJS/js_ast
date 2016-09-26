package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.StatementTree;

public class ForEachLoopTreeImpl extends AbstractLoopTree implements ForEachLoopTree {
	protected final PatternTree variable;
	protected final boolean of;
	protected final ExpressionTree expression;
	public ForEachLoopTreeImpl(long start, long end, PatternTree variable, boolean of, ExpressionTree expression, StatementTree statement) {
		super(start, end, statement);
		this.variable = variable;
		this.of = of;
		this.expression = expression;
	}

	@Override
	public Kind getKind() {
		return of ? Kind.FOR_OF_LOOP : Kind.FOR_IN_LOOP;
	}

	@Override
	public PatternTree getVariable() {
		return variable;
	}

	@Override
	public ExpressionTree getExpression() {
		return expression;
	}
}
