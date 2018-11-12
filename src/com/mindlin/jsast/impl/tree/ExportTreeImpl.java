package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ExportTreeImpl extends AbstractExpressiveStatementTree implements ExportTree {
	
	public ExportTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
		super(start, end, expression);
	}
	
}
