package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ExportTreeImpl extends AbstractExpressiveStatementTree implements ExportTree {
	
	public ExportTreeImpl(long start, long end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
