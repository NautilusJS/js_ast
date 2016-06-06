package com.mindlin.jsast.impl.lexer;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.parser.JSKeyword;
import com.mindlin.jsast.impl.parser.JSOperator;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;
import com.mindlin.jsast.impl.parser.NumericBase;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.impl.util.Characters;

public class JSLexer {
	protected final CharacterStream chars;
	
	public JSLexer(char[] chars) {
		this(new CharacterStream.CharacterArrayStream(chars));
	}
	
	public JSLexer(CharacterStream chars) {
		this.chars = chars;
	}
	
	public JSLexer(JSLexer origin) {
		this.chars = origin.chars;
	}
	
	public JSLexer(String src) {
		this(src.toCharArray());
	}
	
	public long getPosition() {
		return this.chars.position();
	}
	
	public String parseStringLiteral() {
		return parseStringLiteral(chars.next());
	}
	
	public String parseStringLiteral(final char startChar) {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (chars.hasNext()) {
			char c = chars.next();
			if (isEscaped) {
				isEscaped = false;
				switch (c) {
					case '\'':
					case '"':
					case '\\':
						sb.append(c);
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'b':
						sb.append('\b');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'v':
						sb.append(Characters.VT);// vertical tab
						break;
					case '0':
						sb.append(Characters.NULL);
						break;
					case '\n':
						if (chars.peekNext() == '\r')
							chars.skip(1);
						break;
					case '\r':
						if (chars.peekNext() == '\n')
							chars.skip(1);
						break;
					// TODO support unicode/octal escape sequences (see
					// https://mathiasbynens.be/notes/javascript-escapes)
					default:
						throw new JSSyntaxException("Invalid escape sequence: \\" + c, getPosition());
				}
				continue;
			}
			if (c == '\\') {
				isEscaped = true;
				continue;
			}
			if (c == '\r' || c == '\n')
				throw new JSSyntaxException("Illegal newline in the middle of a string literal", getPosition());
			if (c == startChar)
				break;
			sb.append(c);
		}
		return sb.toString();
	}
	
	public boolean isEOF() {
		return chars.isEOF();
	}
	
	public Number parseNumberLiteral() {
		char c = Character.toLowerCase(chars.peekNext());
		boolean isNegative = false;
		if (c == '-') {
			isNegative = true;
			chars.skip(1);
			c = chars.peekNext();
		}
		NumericBase base = NumericBase.DECIMAL;
		if (c == '0')
			switch (Character.toLowerCase(chars.next(2))) {
				case 'x':
					base = NumericBase.HEXDECIMAL;
					chars.skip(2);
					break;
				case 'b':
					base = NumericBase.BINARY;
					chars.skip(2);
					break;
				default:
					base = NumericBase.OCTAL;
					chars.skip(1);
			}
		long startPos = getPosition();
		long decimalPos = -1;
		while (chars.hasNext()
				&& !(Characters.isJsWhitespace(c = Character.toLowerCase(chars.next())) || chars.isEOL())) {
			if (c < '0' || c > 'f')
				throw new JSSyntaxException("Illegal identifier in number literal: '" + c + "'", getPosition());
			if (c == '.') {
				if (base != NumericBase.DECIMAL)
					break;
				decimalPos = getPosition() - 1;
				continue;
			}
			boolean isValid;
			switch (base) {
				case BINARY:
					isValid = c <= '1';
					break;
				case OCTAL:
					if (c > '9') {
						isValid = false;
						break;
					} else if (c > '7') {
						base = NumericBase.DECIMAL;
					}
				case DECIMAL:
					isValid = c <= '9';
					break;
				case HEXDECIMAL:
					isValid = c <= '9' || (c >= 'a');
					break;
				default:
					throw new RuntimeException("Unknown base :" + base);
			}
			if (!isValid)
				throw new IllegalArgumentException(
						"Illegal identifier in (" + base + ") number literal: '" + c + "' at " + (getPosition() - 1));
		}
		chars.prev();
		String s = chars.copy(startPos, chars.position() - startPos);
		System.out.println("S: " + s + " B:" + base);
		if (decimalPos < 0) {
			long value = Long.parseUnsignedLong(s, base.getExponent());
			if (isNegative)
				value = -value;
			return value;
		}
		double value = Double.parseDouble(s);
		if (isNegative)
			value = -value;
		return value;
	}
	
	public JSOperator peekOperator() {
		char c = chars.peekNext(), d = chars.peek(2), e = chars.peek(3);
		if (d == '=') {
			switch (c) {
				case '+':
					return JSOperator.ADDITION_ASSIGNMENT;
				case '-':
					return JSOperator.SUBTRACTION_ASSIGNMENT;
				case '*':
					return JSOperator.MULTIPLICATION_ASSIGNMENT;
				case '/':
					return JSOperator.DIVISION_ASSIGNMENT;
				case '%':
					return JSOperator.REMAINDER_ASSIGNMENT;
				case '&':
					return JSOperator.BITWISE_AND_ASSIGNMENT;
				case '|':
					return JSOperator.BITWISE_OR_ASSIGNMENT;
				case '^':
					return JSOperator.BITWISE_XOR_ASSIGNMENT;
				case '<':
					return JSOperator.LESS_THAN_EQUAL;
				case '>':
					return JSOperator.GREATER_THAN_EQUAL;
				case '!':
					if (e == '=')
						return JSOperator.STRICT_NOT_EQUAL;
					return JSOperator.NOT_EQUAL;
				case '=':
					if (e == '=')
						return JSOperator.STRICT_EQUAL;
					return JSOperator.EQUAL;
			}
		}
		switch (c) {
			case '+':
				if (d == '+')
					return JSOperator.INCREMENT;
				return JSOperator.ADDITION_ASSIGNMENT;
			case '-':
				if (d == '-')
					return JSOperator.DECREMENT;
				return JSOperator.MINUS;
			case '*':
				if (d == '*') {
					if (e == '=')
						return JSOperator.EXPONENTIATION_ASSIGNMENT;
					return JSOperator.EXPONENTIATION;
				}
				return JSOperator.MULTIPLICATION;
			case '/':
				return JSOperator.DIVISION;
			case '<':
				if (d == '<') {
					if (e == '=')
						return JSOperator.LEFT_SHIFT_ASSIGNMENT;
					return JSOperator.LEFT_SHIFT;
				}
				return JSOperator.LESS_THAN;
			case '>':
				if (d == '>') {
					if (e == '>') {
						if (chars.peek(4) == '=')
							return JSOperator.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
						return JSOperator.UNSIGNED_RIGHT_SHIFT;
					} else if (e == '=') {
						return JSOperator.RIGHT_SHIFT_ASSIGNMENT;
					}
					return JSOperator.RIGHT_SHIFT;
				}
				return JSOperator.GREATER_THAN;
			case '=':
				if (d == '>')
					return JSOperator.LAMBDA;
				return JSOperator.ASSIGNMENT;
			case '&':
				if (d == '&')
					return JSOperator.LOGICAL_AND;
				return JSOperator.BITWISE_AND;
			case '^':
				return JSOperator.BITWISE_XOR;
			case '|':
				if (d == '|')
					return JSOperator.LOGICAL_OR;
				return JSOperator.BITWISE_OR;
			case '!':
				return JSOperator.LOGICAL_NOT;
			case '~':
				return JSOperator.BITWISE_NOT;
			case '(':
				return JSOperator.LEFT_PARENTHESIS;
			case ')':
				return JSOperator.RIGHT_PARENTHESIS;
			case ',':
				return JSOperator.COMMA;
			case '?':
				return JSOperator.QUESTION_MARK;
			case ':':
				return JSOperator.COLON;
		}
		return null;
	}
	
	public Token expectTokenKind(TokenKind kind) {
		Token t = this.nextToken();
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		return t;
	}
	public Token expectToken(TokenKind kind, Object value) {
		Token t = this.nextToken();
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		if (t.getValue() != value)
			throw new JSUnexpectedTokenException(t, value);
		return t;
	}
	public Token expectToken(Object value) {
		Token t = this.nextToken();
		if (t.getValue() != value)
			throw new JSUnexpectedTokenException(t, value);
		return t;
	}
	
	public long getCharIndex() {
		return chars.position();
	}
	
	public JSOperator parseOperator() {
		JSOperator result = peekOperator();
		if (result != null)
			chars.skip(result.length());
		return result;
	}
	
	public String parseNextIdentifier() {
		StringBuilder sb = new StringBuilder();
		char c = chars.next();
		if (!Character.isJavaIdentifierStart(c))
			return null;
		do {
			sb.append(c);
		} while (Character.isJavaIdentifierPart(c = chars.next()));
		chars.skip(-1);
		return sb.toString();
	}
	
	public Token nextToken() {
		chars.skipWhitespace();
		long start = Math.max(chars.position(), 0);
		char c = chars.peekNext();
		Object value = null;
		TokenKind kind = null;
		if (c == '"' || c == '\'') {
			// TODO add support for template literals
			value = parseStringLiteral();
			kind = TokenKind.LITERAL;
		} else if (c > '0' && c < '9') {
			value = parseNumberLiteral();
			kind = TokenKind.LITERAL;
		} else if (c == '[' || c == ']' || c == '{' || c == '}') {
			value = c;
			kind = TokenKind.BRACKET;
			chars.skip(1);
		} else if (c == ';') {
			kind = TokenKind.SPECIAL;
			value = JSSpecialGroup.SEMICOLON;
			chars.skip(1);
		} else if ((value = this.parseOperator()) != null) {
			kind = TokenKind.OPERATOR;
		} else {
			String identifierName = this.parseNextIdentifier();
			if (identifierName != null) {
				JSKeyword keyword = JSKeyword.lookup(identifierName);
				if (keyword != null) {
					value = keyword;
					kind = TokenKind.KEYWORD;
				} else {
					value = identifierName;
					kind = TokenKind.IDENTIFIER;
				}
			}
		}
		return new Token(start, kind, chars.copy(start, chars.position() - start), value);
	}
	
	public Token peekNextToken() {
		chars.mark();
		Token result = nextToken();
		chars.resetToMark();
		return result;
	}
	
	public void skipToken(Token token) {
		chars.skip(token.getLength());
	}
}