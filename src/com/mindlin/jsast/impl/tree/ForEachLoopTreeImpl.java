package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.VariableDeclarationOrPatternTree;

public class ForEachLoopTreeImpl extends AbstractLoopTree implements ForEachLoopTree {
	protected final VariableDeclarationOrPatternTree variable;
	protected final boolean of;
	protected final ExpressionTree expression;
	public ForEachLoopTreeImpl(SourcePosition start, SourcePosition end, VariableDeclarationOrPatternTree variable, boolean of, ExpressionTree expression, StatementTree statement) {
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
	public VariableDeclarationOrPatternTree getVariable() {
		return variable;
	}

	@Override
	public ExpressionTree getExpression() {
		return expression;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getVariable(), getExpression(), getStatement());
	}
}
