package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExportTree;
import com.mindlin.nautilus.tree.ExpressionTree;

public class ExportTreeImpl extends AbstractTree implements ExportTree {
	protected final boolean isDefault;
	protected final ExpressionTree expression;
	
	public ExportTreeImpl(SourcePosition start, SourcePosition end, boolean isDefault, ExpressionTree expression) {
		super(start, end);
		this.isDefault = isDefault;
		this.expression = expression;
	}

	@Override
	public boolean isDefault() {
		return this.isDefault;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
}
