package com.mindlin.jsast.impl.parser;

public class JSLexer {
	public static final char[] JS_WHITESPACE = new char[] {
			'\r',
			'\n',
			Characters.VT, // tabulation line
			Characters.FF, // ff (ctrl-l)
			'\u00a0', // Latin-1 space
			'\u1680', // Ogham space mark
			'\u180e', // separator, Mongolian vowel
			'\u2000', // en quad
			'\u2001', // em quad
			'\u2002', // en space
			'\u2003', // em space
			'\u2004', // three-per-em space
			'\u2005', // four-per-em space
			'\u2006', // six-per-em space
			'\u2007', // figure space
			'\u2008', // punctuation space
			'\u2009', // thin space
			'\u200a', // hair space
			'\u202f', // narrow no-break space
			'\u205f', // medium mathematical space
			'\u3000', // ideographic space
			'\ufeff' // byte order mark
	};
	public static boolean isJSWhitespace(final char c) {
		//binary search
		int lo = 0;
        int hi = JS_WHITESPACE.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
			if (c < JS_WHITESPACE[mid])
				hi = mid - 1;
			else if (c > JS_WHITESPACE[mid])
				lo = mid + 1;
			else
				return true;
        }
        return false;
	}
	public static boolean canStartIdentifier(final char c) {
		return Character.isJavaIdentifierStart(c);
	}
	protected long position;
	protected final char[] chars;
	
	public JSLexer(char[] chars) {
		this(chars, -1);
	}
	
	public JSLexer(char[] chars, long position) {
		this.chars = chars;
		this.position = position;
	}
	
	public JSLexer(JSLexer origin) {
		this.chars = origin.chars;
		this.position = origin.position;
	}
	
	public JSLexer(String src) {
		this(src.toCharArray());
	}
	
	public char current() {
		if (isEOF())
			return Characters.EOT;
		return chars[(int) position];
	}
	
	public char peekNext() {
		return peek(1);
	}
	
	public char next() {
		++position;
		return current();
	}
	
	public char peekPrev() {
		return peek(-1);
	}
	
	public char prev() {
		--position;
		return current();
	}
	
	public char peek(long offset) {
		if (position + offset > chars.length)
			return Characters.EOT;//return null
		return chars[(int)(position + offset)];
	}
	public char skipAndGet(long offset) {
		position += offset;
		return current();
	}
	public char peekLowerCase(long offset) {
		return Character.toLowerCase(peek(offset));
	}
	public void skip(long points) {
		this.position += points;
	}
	
	public void seek(long position) {
		this.position = position;
	}
	
	public long getPosition() {
		return this.position;
	}
	
	public long skipToNext(char c) {
		long startPos = getPosition();
		skip(-1);
		while (next() != c)
			;
		return getPosition() - startPos;
	}
	
	public long skipWhitespace() {
		long startPos = getPosition();
		seek(-1);
		while (isJSWhitespace(next()))
			;
		return getPosition() - startPos;
	}
	public String parseStringLiteral() {
		char startChar = next();
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (getPosition() < chars.length) {
			char c = next();
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
						sb.append(Characters.VT);//vertical tab
						break;
					case '0':
						sb.append(Characters.NULL);
						break;
					case '\n':
						if (peekNext() == '\r')
							skip(1);
						break;
					case '\r':
						if (peekNext() == '\n')
							skip(1);
						break;
						//TODO support unicode/octal escape sequences (see https://mathiasbynens.be/notes/javascript-escapes)
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
		return position >= chars.length - 1;
	}
	
	public Number parseNumberLiteral() {
		char c = Character.toLowerCase(peekNext());
		boolean isNegative = false;
		if (c == '-') {
			isNegative = true;
			skip(1);
			c = peekNext();
		}
		NumericBase base = NumericBase.DECIMAL;
		if (c == '0')
			switch (Character.toLowerCase(skipAndGet(2))) {
				case 'x':
					base = NumericBase.HEXDECIMAL;
					skip(2);
					break;
				case 'b':
					base = NumericBase.BINARY;
					skip(2);
					break;
				default:
					base = NumericBase.OCTAL;
					skip(1);
			}
		long startPos = getPosition();
		long decimalPos = -1;
		while ((c = Character.toLowerCase(next())) != Characters.EOT) {
			if (c == Characters.EOT || JSLexer.isJSWhitespace(c))
				break;
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
				throw new IllegalArgumentException("Illegal identifier in (" + base + ") number literal: '" + c + "' at " + (getPosition() - 1));
		}
		prev();
		String s = new String(chars, (int)startPos, (int)(getPosition() - startPos));
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
		char c = peekNext(), d = peek(2), e = peek(3);
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
				return JSOperator.SUBTRACTION;
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
						if (peek(4) == '=')
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
				return JSOperator.CONDITIONAL_QUESTION_MARK;
			case ':':
				return JSOperator.CONDITIONAL_COLON;
		}
		return null;
	}
	public JSOperator parseOperator() {
		JSOperator result = peekOperator();
		if (result != null)
			this.skip(result.length());
		return result;
	}
	
	public String parseNextIdentifier() {
		StringBuilder sb = new StringBuilder();
		char c = next();
		if (!Character.isJavaIdentifierStart(c))
			return null;
		do {
			sb.append(c);
		} while (Character.isJavaIdentifierPart(c = next()));
		skip(-1);
		return sb.toString();
	}
	
	public Token nextToken() {
		skipWhitespace();
		long start = Math.max(getPosition(), 0);
		char c = peekNext();
		Object value = null;
		TokenKind kind = null;
		if (c == '"' || c == '\'') {
			//TODO add support for template literals
			value = parseStringLiteral();
			kind = TokenKind.LITERAL;
		} else if (c > '0' && c < '9') {
			value = parseNumberLiteral();
			kind = TokenKind.LITERAL;
		} else if (c == '[' || c == ']' || c == '{' || c == '}') {
			value = c;
			kind = TokenKind.BRACKET;
			skip(1);
		} else if (c == ';' || c == '\r' || c == '\n') {
			
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
		String literal = new String(chars, (int)start, (int)(getPosition() - start));
		return new Token(start, kind, literal, value);
	}
	public Token peekNextToken() {
		long startPos = getPosition();
		Token result = nextToken();
		this.seek(startPos);
		return result;
	}
	public void skipToken(Token token) {
		this.skip(token.getLength());
	}
}