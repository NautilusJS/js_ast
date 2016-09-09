package com.mindlin.jsast.impl.lexer;

import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;
import com.sun.istack.internal.NotNull;

public class Token {
	public static Token expect(Token t, TokenKind kind, @NotNull Object o, JSLexer src) {
		if (t == null)
			t = src.nextToken();
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		if (t.getValue() != o && (!o.equals(t.getValue())))
			throw new JSUnexpectedTokenException(t, o);
		return t;
	}
	public static Token expectKind(Token t, TokenKind kind, JSLexer src) {
		if (t == null)
			t = src.nextToken();
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		return t;
	}
	
	protected final TokenKind kind;
	protected final long position;
	protected final String text;
	protected final Object value;
	
	public Token(long position, TokenKind kind, String text, Object value) {
		this.position = position;
		this.kind = kind;
		this.text = text;
		this.value = value;
	}
	
	public long getStart() {
		return position;
	}
	
	public long getLength() {
		return text == null ? 0 : text.length();
	}
	
	public long getEnd() {
		return getStart() + getLength();
	}
	
	public String getText() {
		return text;
	}
	
	public TokenKind getKind() {
		return kind;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) value;
	}
	
	public boolean isSpecial() {
		return getKind() == TokenKind.SPECIAL;
	}
	
	public boolean isOperator() {
		return getKind() == TokenKind.OPERATOR;
	}
	
	public boolean isKeyword() {
		return getKind() == TokenKind.KEYWORD;
	}
	
	public boolean isBracket() {
		return getKind() == TokenKind.BRACKET;
	}
	
	public boolean isIdentifier() {
		return getKind() == TokenKind.IDENTIFIER;
	}
	
	public boolean isLiteral() {
		return getKind() == TokenKind.LITERAL;
	}
	
	/**
	 * Is end of statement. EOL or EOF
	 * @return
	 */
	public boolean isEOS() {
		return getKind() == TokenKind.SPECIAL && (getValue() == JSSpecialGroup.EOF || getValue() == JSSpecialGroup.EOL || getValue() == JSSpecialGroup.SEMICOLON);
	}
	
	public boolean matches(TokenKind kind, Object value) {
		return getKind() == kind && getValue() == value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(70)//High end of expected outputs
				.append("Token{kind=").append(getKind())
				.append(",value=").append(value)
				.append(",start=").append(getStart())
				.append(",end=").append(getEnd());
		
		if (getText() == null)
			sb.append(",text=null");
		else
			sb.append(",text=\"").append(getText()).append('"');
		
		sb.append('}');
		return sb.toString();
	}
}