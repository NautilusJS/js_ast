package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.RegExpLiteralTree;

public class RegExpLiteralTreeImpl extends AbstractTree implements RegExpLiteralTree {
	protected final String body;
	protected final String flags;
	public RegExpLiteralTreeImpl(Token regexToken) {
		super(regexToken.getStart(), regexToken.getEnd());
		String[] data = regexToken.getValue();
		this.body = data[0];
		this.flags = data[1];
	}
	public RegExpLiteralTreeImpl(long start, long end, String body, String flags) {
		super(start, end);
		this.body = body;
		this.flags = flags;
	}
	@Override
	public String[] getValue() {
		return new String[]{body, flags};
	}
	
}
