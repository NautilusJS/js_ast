package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.ThisExpressionTree;

public class ThisExpressionTreeImpl extends AbstractTree implements ThisExpressionTree {
	public ThisExpressionTreeImpl(Token t) {
		this(t.getStart(), t.getEnd());
	}
	
	public ThisExpressionTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
}
