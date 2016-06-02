package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.StatementTree;

public class ForEachLoopTreeImpl extends AbstractLoopTree implements ForEachLoopTree {
	protected final boolean of;
	protected final ExpressionTree variable, expression;
	public ForEachLoopTreeImpl(long start, long end, boolean of, ExpressionTree variable, ExpressionTree expression, StatementTree statement) {
		super(start, end, statement);
		this.of = of;
		this.variable = variable;
		this.expression = expression;
	}

	@Override
	public Kind getKind() {
		return of ? Kind.FOR_OF_LOOP : Kind.FOR_IN_LOOP;
	}

	@Override
	public ExpressionTree getVariable() {
		return variable;
	}

	@Override
	public ExpressionTree getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("for (")
			.append(getVariable())
			.append(of ? " of " : " in ")
			.append(getExpression())
			.append(") ")
			.append(getStatement())
			.toString();
	}
}