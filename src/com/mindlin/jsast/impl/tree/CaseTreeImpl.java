package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;

public class CaseTreeImpl extends AbstractTree implements CaseTree {
	protected final ExpressionTree expression;
	protected final List<? extends StatementTree> statements;
	
	public CaseTreeImpl(long start, long end, ExpressionTree expression, List<? extends StatementTree> statements) {
		super(start, end);
		this.expression = expression;
		this.statements = statements;
	}
	
	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	public List<? extends StatementTree> getStatements() {
		return this.statements;
	}
	
	@Override
	public boolean isDefault() {
		return expression == null;
	}
}
