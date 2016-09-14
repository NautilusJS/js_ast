package com.mindlin.jsast.impl.lexer;

import com.mindlin.jsast.impl.parser.JSOperator;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;

public class Token {
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
		final TokenKind kind = getKind();
		return kind == TokenKind.STRING_LITERAL || kind == TokenKind.BOOLEAN_LITERAL || kind == TokenKind.NULL_LITERAL
				|| kind == TokenKind.REGEX_LITERAL || kind == TokenKind.NUMERIC_LITERAL
				|| kind == TokenKind.TEMPLATE_LITERAL;
	}

	/**
	 * Is end of statement. EOL or EOF
	 * 
	 * @return
	 */
	public boolean isEOS() {
		return getKind() == TokenKind.SPECIAL && (getValue() == JSSpecialGroup.EOF || getValue() == JSSpecialGroup.EOL
				|| getValue() == JSSpecialGroup.SEMICOLON);
	}

	public boolean matches(TokenKind kind, Object value) {
		return getKind() == kind && getValue() == value;
	}
	
	@Override
	public String toString() {
		//@formatter:off
		StringBuilder sb = new StringBuilder(70)//High end of expected outputs
				.append("Token{kind=").append(getKind())
				.append(",value=").append(value)
				.append(",start=").append(getStart())
				.append(",end=").append(getEnd());
		//@formatter:on

		if (getText() == null)
			sb.append(",text=null");
		else
			sb.append(",text=\"").append(getText()).append('"');

		sb.append('}');
		return sb.toString();
	}
}