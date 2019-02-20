package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.SuperExpressionTree;

public class SuperExpressionTreeImpl extends AbstractTree implements SuperExpressionTree {
	public SuperExpressionTreeImpl(Token t) {
		this(t.getStart(), t.getEnd());
	}
	
	public SuperExpressionTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
}
