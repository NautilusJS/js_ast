package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.SuperExpressionTree;

public class SuperExpressionTreeImpl extends AbstractTree implements SuperExpressionTree {
	public SuperExpressionTreeImpl(Token t) {
		this(t.getStart(), t.getEnd());
	}
	
	public SuperExpressionTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
}
