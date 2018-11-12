package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.SwitchCaseTree;

public class SwitchCaseTreeImpl extends AbstractTree implements SwitchCaseTree {
	protected final ExpressionTree expression;
	protected final List<? extends StatementTree> statements;
	
	public SwitchCaseTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, List<? extends StatementTree> statements) {
		super(start, end);
		this.expression = expression;
		this.statements = statements;
	}
	
	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	public List<? extends StatementTree> getBody() {
		return this.statements;
	}
	
	@Override
	public boolean isDefault() {
		return expression == null;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression(), getBody(), isDefault());
	}
}
