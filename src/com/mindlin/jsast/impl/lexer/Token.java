package com.mindlin.jsast.impl.lexer;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.impl.parser.JSOperator;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;

public class Token {
	public static final int FLAG_PRECEDEING_NEWLINE = 1 << 0;
	public static final int FLAG_PRECEDING_JSDOC = 1 << 1;
	//TODO: use EnumSet-type wrapper in the future?
	protected final int flags;
	protected final TokenKind kind;
	protected final SourceRange range;
	protected final String text;
	protected final Object value;

	public Token(int flags, SourceRange range, TokenKind kind, String text, Object value) {
		this.flags = flags;
		this.range = range;
		this.kind = kind;
		this.text = text;
		this.value = value;
	}
	
	public boolean hasPrecedingNewline() {
		return (this.flags & FLAG_PRECEDEING_NEWLINE) != 0;
	}
	
	public boolean hasPrecedingJSDoc() {
		return (this.flags & FLAG_PRECEDING_JSDOC) != 0;
	}
	
	public SourceRange getRange() {
		return this.range;
	}

	public SourcePosition getStart() {
		return this.range.getStart();
	}

	public SourcePosition getEnd() {
		return this.range.getEnd();
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
		if (getKind() != kind)
			return false;
		return Objects.equals(getValue(), value);
	}
	
	public boolean matchesOperator(JSOperator value) {
		return getKind() == TokenKind.OPERATOR && getValue() == value;
	}

	public Token reinterpretAsIdentifier() {
		Object value;
		switch (getKind()) {
			case IDENTIFIER:
				return this;
			case KEYWORD:
			case NUMERIC_LITERAL:
			case BOOLEAN_LITERAL:
				value = this.getValue().toString();
				break;
			case NULL_LITERAL:
				value = "null";
				break;
			case OPERATOR:
				value = this.<JSOperator>getValue().getText();
				break;
			case SPECIAL:
				if (this.getValue() == JSSpecialGroup.SEMICOLON)
					value = ';';
				// Fallthrough intentional
				// There is no way to possibly reinterpret these
			case REGEX_LITERAL:
			case STRING_LITERAL:
			case TEMPLATE_LITERAL:
			case COMMENT:
			default:
				throw new UnsupportedOperationException(this + " cannot be reinterpreted as an identifier");
		}
		
		return new Token(this.flags, this.range, TokenKind.IDENTIFIER, getText(), value);
	}

	@Override
	public String toString() {
		//@formatter:off
		StringBuilder sb = new StringBuilder(70)//High end of expected outputs
				.append(this.getClass().getSimpleName())
				.append("{kind=").append(getKind())
				.append(",value=").append(value)
				.append(",range=").append(getRange());
		//@formatter:on

		if (getText() == null)
			sb.append(",text=null");
		else
			sb.append(",text=\"").append(getText()).append('"');

		sb.append('}');
		return sb.toString();
	}
}