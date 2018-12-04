package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.parser.JSKeyword;
import com.mindlin.jsast.tree.type.SpecialTypeTree;

public class SpecialTypeTreeImpl extends AbstractTypeTree implements SpecialTypeTree {
	public static SpecialType mapType(Token t) {
		if (t.matches(TokenKind.KEYWORD, JSKeyword.VOID))
			return SpecialType.VOID;
		String name = t.<String> getValue();
		switch (name) {
			case "any":
				return SpecialType.ANY;
			case "null":
				return SpecialType.NULL;
			case "undefined":
				return SpecialType.UNDEFINED;
			case "number":
				return SpecialType.NUMBER;
			case "string":
				return SpecialType.STRING;
			case "symbol":
				return SpecialType.SYMBOL;
			case "unknown":
				return SpecialType.UNKNOWN;
			case "boolean":
				return SpecialType.BOOLEAN;
			case "never":
				return SpecialType.NEVER;
		}
		return null;
	}
	
	protected final SpecialType type;
	
	public SpecialTypeTreeImpl(SpecialType type) {
		this(null, null, type);
	}
	
	public SpecialTypeTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), mapType(t));
	}
	
	public SpecialTypeTreeImpl(SourcePosition start, SourcePosition end, SpecialType type) {
		super(start, end);
		this.type = type;
	}
	
	@Override
	public SpecialType getType() {
		return this.type;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getType());
	}
	
}
