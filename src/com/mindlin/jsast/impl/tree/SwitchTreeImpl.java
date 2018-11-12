package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;

public class SwitchTreeImpl extends AbstractTree implements SwitchTree {
	protected final List<? extends CaseTree> cases;
	protected final ExpressionTree expression;
	public SwitchTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, List<? extends CaseTree> cases) {
		super(start, end);
		this.expression = expression;
		this.cases = cases;
	}

	@Override
	public List<? extends CaseTree> getCases() {
		return cases;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression(), getCases());
	}
	
}
