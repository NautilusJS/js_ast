package com.mindlin.jsast.impl.lexer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mindlin.jsast.exception.JSEOFException;
import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.parser.JSKeyword;
import com.mindlin.jsast.impl.parser.JSOperator;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;
import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.impl.util.Characters;

public class JSLexer implements Supplier<Token> {
	
	/**
	 * Converts a character to a hexdecimal digit. If passed character is not a
	 * hexdecimal character (not <code>[0-9a-fA-F]</code>), this method returns
	 * -1.
	 * 
	 * @param c
	 *            Digit character to parse
	 * @return Value of hex digit (<code>0-16</code>), or -1 if invalid.
	 */
	protected static int asHexDigit(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		if (c >= 'a' && c <= 'f')
			return c - 'a' + 10;
		if (c >= 'A' && c <= 'F')
			return c - 'A' + 10;
		return -1;
	}
	
	protected final CharacterStream chars;
	protected Token lookahead = null;
	
	public JSLexer(char[] chars) {
		this(new CharacterArrayStream(chars));
	}
	
	public JSLexer(CharacterStream chars) {
		this.chars = chars;
	}
	
	public JSLexer(JSLexer origin) {
		this.chars = origin.chars;
	}
	
	public JSLexer(String src) {
		this(new CharacterArrayStream(src));
	}
	
	public long getPosition() {
		return this.chars.position();
	}
	
	public CharacterStream getCharacters() {
		return this.chars;
	}
	
	public String nextStringLiteral() {
		return nextStringLiteral(chars.next());
	}
	
	public String nextStringLiteral(final char startChar) {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (true) {
			if (!chars.hasNext())
				throw new JSEOFException("Unexpected EOF while parsing a string literal", getPosition());
			char c = chars.next();
			if (isEscaped) {
				isEscaped = false;
				switch (c) {
					case '\'':
					case '"':
					case '\\':
					case '`':
						sb.append(c);
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 'v':
						sb.append(Characters.VT);// vertical tab
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
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
						//EASCII octal escape
						{
							int val = c - '0';
							if (chars.peek() >= '0' && chars.peek() <= '7') {
								val = (val << 3) | (chars.next() - '0');
								if (val < 32 && chars.peek() >= '0' && chars.peek() <= '7')
									val = (val << 3) | (chars.next() - '0');
							}
							sb.append(Charset.forName("EASCII").decode(ByteBuffer.wrap(new byte[]{(byte)val})).toString());
						}
						break;
					case '\n':
						if (chars.hasNext() && chars.peek() == '\r')
							chars.skip(1);
						break;
					case '\r':
						if (chars.hasNext() && chars.peek() == '\n')
							chars.skip(1);
						break;
					case 'u': {
						//Unicode escape
						//See mathiasbynens.be/notes/javascript-escapes
						char d = chars.next();
						if (d == '{') {
							//Max unicode code point is 0x10FFFF, which fits within a 32-bit integer
							int val = 0;
							d = chars.next();
							do {
								int digit = asHexDigit(d);
								if (d < 0)
									throw new JSSyntaxException("Invalid character '" + d + "' in unicode code point escape sequence", chars.position());
								if (val<<4 < val)
									throw new JSSyntaxException("Invalid Unicode code point " + val, chars.position());
								val = (val << 4) | digit;
							} while ((d = chars.next()) != '}');
							sb.append(new String(new int[] { val }, 0, 1));
						} else {
							int val = asHexDigit(d);
							if (val < 0)
								throw new JSSyntaxException("Invalid character '" + d + "' in unicode code point escape sequence", chars.position());
							for (int i = 0; i < 3; i++) {
								int digit = asHexDigit(d = chars.next());
								if (digit < 0)
									throw new JSSyntaxException("Invalid character '" + d + "' in unicode code point escape sequence", chars.position());
								val = (val << 4) | digit;
							}
							String t = new String(new int[] { val }, 0, 1);
							sb.append(t);
						}
						break;
					}
					case 'x':
						//EASCII hexdecimal character escape
						if (!chars.hasNext(2))
							throw new JSEOFException("Invalid Extended ASCII escape sequence (EOF)", getPosition() - 2);
						try {
							sb.append(Charset.forName("EASCII").decode(ByteBuffer.wrap(new byte[]{Byte.parseByte(chars.copyNext(2), 16)})).toString());
						} catch (NumberFormatException e) {
							throw new JSSyntaxException("Invalid Extended ASCII escape sequence (\\x" + chars.copy(chars.position() - 2, 2) + ")", getPosition() - 4);
						}
						break;
					default:
						throw new JSSyntaxException("Invalid escape sequence: \\" + c, getPosition() - 2);
				}
				continue;
			}
			if (c == '\\') {
				isEscaped = true;
				continue;
			}
			if (c == '\r' || c == '\n') {
				if (startChar == '`') {
					//Newlines are allowed as part of a template literal
					if (chars.hasNext() && ((c == '\r' && chars.peek() == '\n') || chars.peek() == '\r'))
						chars.skip(1);
					sb.append('\n');
					continue;
				}
				throw new JSSyntaxException("Illegal newline in the middle of a string literal", getPosition());
			}
			if (c == startChar)
				break;
			sb.append(c);
		}
		return sb.toString();
	}
	
	public boolean isEOF() {
		return !chars.hasNext();
	}
	
	protected long nextHexLiteral() throws JSSyntaxException {
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (!Characters.isHexDigit(lookahead)) {
				if (Characters.canStartIdentifier(lookahead))
					throw new JSSyntaxException("Unexpected token", chars.position());
				break;
			}
			result = (result << 4) | asHexDigit(chars.next());
		}
		return result;
	}
	
	public Number nextNumericLiteral() throws JSSyntaxException {
		NumericLiteralType type = NumericLiteralType.DECIMAL;
		
		boolean isPositive = true;
		if (!chars.hasNext())
			throw new JSEOFException(chars.position());
		char c = chars.next();
		//skip non-number stuff
		//TODO check if this is correct
		while (c == ';' || !Character.isJavaIdentifierPart(c))
			c = chars.next();
		if (c == '-') {
			isPositive = false;
			c = chars.next();
		}
		if (c == '0') {
			switch (c = chars.next()) {
				case 'X':
				case 'x':
					return nextHexLiteral();
				case 'b':
				case 'B':
					type = NumericLiteralType.BINARY;
					c = chars.next();
					break;
				case 'o':
				case 'O':
					type = NumericLiteralType.OCTAL;
					c = chars.next();
					break;
				default:
					//Number starts with a '0', but can be upgraded to a DECIMAL type
					chars.skip(-1);
					type = NumericLiteralType.OCTAL_IMPLICIT;
					break;
			}
		}
		final long startNmbPos = chars.position();
		boolean hasDecimal = false;
		chars.skip(-1);
		while (chars.hasNext()) {
			c = chars.next();
			if (c == '.') {
				if ((type == NumericLiteralType.DECIMAL || type == NumericLiteralType.OCTAL_IMPLICIT) && !hasDecimal) {
					hasDecimal = true;
					//Possibly upgrade from OCTAL_IMPLICIT
					type = NumericLiteralType.DECIMAL;
					continue;
				}
				throw new JSSyntaxException("Unexpected number", chars.position());
			}
			
			if (c == ';' || !Character.isJavaIdentifierPart(c)) {
				chars.skip(-1);
				break;
			}
			
			boolean isValid = false;
			if (c >= '0') {
				switch (type) {
					case BINARY:
						isValid = c <= '1';
						break;
					case OCTAL_IMPLICIT:
						if (c > '7' && c <= '9') {
							//Upgrade to decimal if possible
							type = NumericLiteralType.DECIMAL;
							isValid = true;
							break;
						}
						//Fallthrough intentional
					case OCTAL:
						isValid = c <= '7';
						break;
					case HEXDECIMAL:
						if ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
							isValid = true;
							break;
						}
						//Fallthrough intentional
					case DECIMAL:
						isValid = c <= '9';
						break;
				}
			}
			if (!isValid)
				throw new JSSyntaxException("Unexpected identifier in numeric literal (" + type + "): " + Character.getName(c), chars.position());
		}
		//Build the string for Java to parse (It might be slightly faster to calculate the value of the digit while parsing,
		//but it causes problems with OCTAL_IMPLICIT upgrades.
		String nmb = (isPositive ? "" : "-") + chars.copy(startNmbPos, chars.position() - startNmbPos + 1);
		
		//Return number. Tries to cast down to the smallest size that it can losslessly
		if (hasDecimal) {
			double result = Double.parseDouble(nmb);
			//TODO reorder return options
			long lResult = (long) result;
			if (result > Long.MIN_VALUE && result < Long.MAX_VALUE && lResult == result) {
				if (lResult > Integer.MIN_VALUE && lResult < Integer.MAX_VALUE)
					return (int) lResult;
				return lResult;
			}
			float fResult = (float) result;
			if (result == fResult)
				return fResult;
			return result;
		}
		long result = Long.parseLong(nmb, type.getExponent());
		if (result > Integer.MIN_VALUE && result < Integer.MAX_VALUE)
			//Downgrade to an int if possible
			return (Integer) (int) result;
		return (Long) result;
	}
	
	public JSOperator peekOperator() {
		char c = chars.peek(),
				d = chars.hasNext(2) ? chars.peek(2) : '\0',
				e = chars.hasNext(3) ? chars.peek(3) : '\0';
		if (d == '=') {
			//Switch through assignment types
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
				return JSOperator.PLUS;
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
			case '%':
				return JSOperator.REMAINDER;
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
						if (chars.hasNext(4) && chars.peek(4) == '=')
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
			case '.':
				if (d == '.' && e == '.')
					return JSOperator.SPREAD;
				return JSOperator.PERIOD;
		}
		return null;
	}
	
	public Token expectKind(TokenKind kind) {
		Token t = this.nextToken();
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		return t;
	}
	
	public Token expect(Object value) {
		Token t = this.nextToken();
		if (!Objects.equals(t.getValue(), value))
			throw new JSUnexpectedTokenException(t, value);
		return t;
	}
	
	public Token expect(TokenKind kind, Object value) {
		Token t = this.nextToken();
		if (!Objects.equals(t.getValue(),value))
			throw new JSUnexpectedTokenException(t, value);
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		return t;
	}
	
	public JSOperator nextOperator() {
		JSOperator result = peekOperator();
		if (result != null)
			chars.skip(result.length());
		return result;
	}
	
	public String nextIdentifier() {
		StringBuilder sb = new StringBuilder();
		char c;
		if (!chars.hasNext() || !Character.isJavaIdentifierStart(c = chars.next()))
			return null;
		do {
			sb.append(c);
			if (!chars.hasNext())
				return sb.toString();
		} while (Character.isJavaIdentifierPart(c = chars.next()));
		chars.skip(-1);
		return sb.toString();
	}
	
	public String nextRegularExpression() {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (true) {
			if (!chars.hasNext())
				throw new JSSyntaxException("Unexpected EOF while parsing a regex literal", getPosition());
			char c = chars.next();
			
			if (isEscaped)
				isEscaped = false;
			else if (c == '\\')
				isEscaped = true;
			else if (c == '/')
				break;
			
			sb.append(c);
		}
		return sb.toString();
	}
	
	protected String scanRegExpBody(String st) {
		StringBuffer sb = new StringBuffer();
		boolean escaped = false;
		int classes = 0;
		int groups = 0;
		for (int i = 1, l = st.length(); i < l; i++) {
			char ch = st.charAt(i);
			if (ch == '\\')
				escaped = true;
			sb.append(ch);
		}
		while (!isEOF()) {
			char ch = chars.next();
			if (!escaped && ch == '/')
				break;
			sb.append(ch);
			if (escaped) {
				escaped = false;
				continue;
			}
			switch (ch) {
				case '\\':
					escaped = true;
					break;
				case '[':
					classes++;
					break;
				case '(':
					groups++;
					break;
				case ']':
					if (--classes < 0)
						throw new JSSyntaxException("Illegal regular expression character class end", chars.position());
					break;
				case ')':
					if (--groups < 0)
						throw new JSSyntaxException("Illegal regular expression group end", chars.position());
					break;
				default:
					break;
			}
		}
		if (chars.current() != '/')
			throw new JSEOFException("Unexpected EOF in regex literal", chars.position());
		return sb.toString();
	}
	
	protected String scanRegExpFlags() {
		long start = chars.position();
		while (!isEOF()) {
			char c = chars.peek();
			if (c != 'g' && c!= 'i' && c!= 'm' && c != 'y')
				break;
			chars.next();
		}
		return chars.copy(start, chars.position() - start);
	}
	
	public Token finishRegExpLiteral(Token start) {
		if (start == null)
			start = this.nextToken();
		if (start.getValue() != JSOperator.DIVISION && start.getValue() != JSOperator.DIVISION_ASSIGNMENT)
			throw new JSSyntaxException("Regular expression must start with a slash", start.getStart());
		long intermediateStart = getPosition();
		String body = scanRegExpBody(start.text);
		String flags = scanRegExpFlags();
		return new Token(start.getStart(), TokenKind.REGEX_LITERAL, start.text + chars.copy(intermediateStart, chars.position() - intermediateStart), new String[]{body, flags});
	}
	
	public String nextComment(final boolean singleLine) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (!chars.hasNext()) {
				if (singleLine)
					break;
				throw new JSEOFException(chars.position());
			}
			char c = chars.next();
			if (singleLine) {
				if (c == '\n' ||c == '\r')
					break;
			} else if (c == '*' && chars.hasNext() && chars.peek() == '/') {
				chars.next(2);
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public Token nextToken() {
		if (this.lookahead != null) {
			Token result = this.lookahead;
			if (!result.matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
				skip(result);
			return result;
		}
		chars.skipWhitespace();
		if (isEOF())
			return this.lookahead = new Token(chars.position(), TokenKind.SPECIAL, null, JSSpecialGroup.EOF);
		final long start = Math.max(chars.position(), -1);
		char c = chars.peek();
		Object value = null;
		TokenKind kind = null;
		//Drop through a various selection of possible results
		//Check if it's a string literal
		if (c == '"' || c == '\'') {
			value = nextStringLiteral();
			kind = TokenKind.STRING_LITERAL;
		} else if (c == '`') {
			value = nextStringLiteral();
			kind = TokenKind.TEMPLATE_LITERAL;
		//Check if it's a numeric literal (the first letter of all numbers must be /[0-9]/)
		} else if (c >= '0' && c <= '9') {
			value = nextNumericLiteral();
			kind = TokenKind.NUMERIC_LITERAL;
		//Check if it's a bracket
		} else if (c == '[' || c == ']' || c == '{' || c == '}') {
			value = c;
			kind = TokenKind.BRACKET;
			chars.next();
		} else if (c == ';') {
			kind = TokenKind.SPECIAL;
			value = JSSpecialGroup.SEMICOLON;
			chars.next();
		} else if (c == '/' && chars.hasNext(2) && (chars.peek(2) == '/' || chars.peek(2) == '*')) {
			kind = TokenKind.COMMENT;
			value = this.nextComment(chars.peek(2) == '/');
			return nextToken();
		} else if ((value = nextOperator()) != null) {
			kind = TokenKind.OPERATOR;
		} else {
			//It's probably an identifier
			String identifierName = this.nextIdentifier();
			if (identifierName == null) {
				//Couldn't even parse an identifier
				throw new JSSyntaxException("Illegal syntax at " + start);
			} else if (identifierName.equals("true") || identifierName.equals("false")) {
				//Boolean literal
				kind = TokenKind.BOOLEAN_LITERAL;
				value = identifierName.equals("true");
			} else if (identifierName.equals("null")) {
				//Null literal
				kind = TokenKind.NULL_LITERAL;
				value = null;
			} else {
				//An identifier was parsed, and it wasn't a boolean literal
				//Check if it's a keyword
				JSKeyword keyword = JSKeyword.lookup(identifierName);
				if (keyword != null) {
					//Because the '*' in function* and yield* is not normally considered part of an
					//identifier sequence, we have to check for it here
					if (chars.hasNext() && chars.peek() == '*' && (keyword == JSKeyword.FUNCTION || keyword == JSKeyword.YIELD)) {
						chars.next();
						if (keyword == JSKeyword.FUNCTION)
							keyword = JSKeyword.FUNCTION_GENERATOR;
						else if (keyword == JSKeyword.YIELD)
							keyword = JSKeyword.YIELD_GENERATOR;
					}
					value = keyword;
					kind = TokenKind.KEYWORD;
				} else {
					value = identifierName;
					kind = TokenKind.IDENTIFIER;
				}
			}
		}
		return new Token(start + 1, kind, chars.copy(start + 1, chars.position() - start), value);
	}
	
	public Token nextTokenIf(TokenKind kind, Object value) {
		Token lookahead = peek();
		if (lookahead.matches(kind, value)) {
			skip(lookahead);
			return lookahead;
		}
		return null;
	}
	
	public Token nextTokenIf(TokenKind kind) {
		Token lookahead = peek();
		if (lookahead.getKind() == kind) {
			skip(lookahead);
			return lookahead;
		}
		return null;
	}
	
	public Token nextTokenIf(Predicate<Token> acceptor) {
		Token lookahead = peek();
		if (acceptor.test(lookahead)) {
			skip(lookahead);
			return lookahead;
		}
		return null;
	}
	
	public boolean nextTokenIs(TokenKind kind, Object value) {
		return nextTokenIf(kind, value) != null;
	}
	
	public Token peek() {
		if (this.lookahead != null)
			return this.lookahead;
		chars.mark();
		this.lookahead = nextToken();
		chars.resetToMark();
		return this.lookahead;
	}
	
	public Token skip(Token token) {
		if (token != this.lookahead)
			throw new IllegalArgumentException("Skipped token " + token + " is not lookahead");
		if (token.matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
			throw new IllegalStateException("Cannot skip EOF token " + token);
		this.lookahead = null;
		chars.position(token.getEnd() - 1);
		return token;
	}
	
	public void mark() {
		chars.mark();
	}
	
	public void reset() {
		this.lookahead = null;
		chars.resetToMark();
	}
	
	public void unmark() {
		chars.unmark();
	}
	
	@Override
	public Token get() {
		return nextToken();
	}
}