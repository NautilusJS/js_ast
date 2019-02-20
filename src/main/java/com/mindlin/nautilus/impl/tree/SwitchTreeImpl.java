package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.SwitchCaseTree;
import com.mindlin.nautilus.tree.SwitchTree;

public class SwitchTreeImpl extends AbstractTree implements SwitchTree {
	protected final List<? extends SwitchCaseTree> cases;
	protected final ExpressionTree expression;
	public SwitchTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, List<? extends SwitchCaseTree> cases) {
		super(start, end);
		this.expression = expression;
		this.cases = cases;
	}

	@Override
	public List<? extends SwitchCaseTree> getCases() {
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
