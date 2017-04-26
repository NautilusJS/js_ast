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
		chars.skipWhitespace();
		if (chars.hasNext())
			return nextStringLiteral(chars.next());
		return null;
	}
	
	/**
	 * Read an escape sequence in the form of either {@code uXXXX} or {@code u{X...XXX}}.
	 * 
	 * It is expected that the 'u' at the start of the escape sequence has already been consumed.
	 * 
	 * @return character read
	 * @see <a href="https://tc39.github.io/ecma262/#prod-UnicodeEscapeSequence">ECMAScript 262 &sect; 11.8.4</a>
	 */
	protected char readUnicodeEscapeSequence() {
		if (chars.peek() == '{') {
			chars.skip(1);
			//Max unicode code point is 0x10FFFF, which fits within a 32-bit integer
			int val = 0;
			char c = chars.next();
			do {
				int digit = Characters.asHexDigit(c);
				if (c < 0)
					throw new JSSyntaxException("Invalid character '" + c + "' in unicode code point escape sequence", chars.position());
				//Overflow (value >= 0x10FFFF)
				if ((val << 4) < val)
					throw new JSSyntaxException("Invalid Unicode code point " + val, chars.position());
				val = (val << 4) | digit;
			} while ((c = chars.next()) != '}');
			
			return (char) val;
		} else {
			//Escape in uXXXX form
			int value = 0;
			for (int i = 0; i < 4; i++) {
				char c = chars.next();
				int digit = Characters.asHexDigit(c);
				if (digit < 0)
					throw new JSSyntaxException("Invalid character '" + c + "' in unicode code point escape sequence", chars.position());
				value = (value << 4) | digit;
			}
			return (char) value;
		}
	}
	
	public String nextStringLiteral(final char startChar) {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (true) {
			if (!chars.hasNext())
				throw new JSEOFException("Unexpected EOF while parsing a string literal (" + sb + ")", getPosition());
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
								int digit = Characters.asHexDigit(d);
								if (d < 0)
									throw new JSSyntaxException("Invalid character '" + d + "' in unicode code point escape sequence", chars.position());
								if (val<<4 < val)
									throw new JSSyntaxException("Invalid Unicode code point " + val, chars.position());
								val = (val << 4) | digit;
							} while ((d = chars.next()) != '}');
							sb.append(new String(new int[] { val }, 0, 1));
						} else {
							int val = Characters.asHexDigit(d);
							if (val < 0)
								throw new JSSyntaxException("Invalid character '" + d + "' in unicode code point escape sequence", chars.position());
							for (int i = 0; i < 3; i++) {
								int digit = Characters.asHexDigit(d = chars.next());
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
							String s = chars.copyNext(2);
							sb.append(Charset.forName("EASCII").decode(ByteBuffer.wrap(new byte[]{(byte) Integer.parseInt(s, 16)})).toString());
						} catch (NumberFormatException e) {
							throw new JSSyntaxException("Invalid Extended ASCII escape sequence (\\x" + chars.copy(chars.position() - 1, 2) + ")", getPosition() - 4, e);
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
	
	//Numeric literals
	
	protected long nextHexLiteral() throws JSSyntaxException {
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (!Characters.isHexDigit(lookahead)) {
				if (Characters.canStartIdentifier(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected token", chars.position());
				break;
			}
			isEmpty = false;
			result = (result << 4) | Characters.asHexDigit(chars.next());
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", chars.position());
		return result;
	}
	
	protected long nextBinaryLiteral() throws JSSyntaxException {
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead != '0' && lookahead != '1') {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected token", chars.position());
				break;
			}
			isEmpty = false;
			result = (result << 1) | (chars.next() - '0');
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", chars.position());
		return result;
	}
	
	protected long nextOctalLiteral() throws JSSyntaxException {
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead < '0' || '7' < lookahead) {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected token", chars.position());
				break;
			}
			isEmpty = false;
			result = (result << 3) | (chars.next() - '0');
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", chars.position());
		return result;
	}
	
	protected Number nextDecimalLiteral() throws JSSyntaxException {
		return null;
	}
	
	public Number nextNumericLiteral() throws JSSyntaxException {
		NumericLiteralType type = NumericLiteralType.DECIMAL;
		
		boolean isPositive = true;
		if (!chars.hasNext())
			throw new JSEOFException(chars.position());
		
		char c;
		//skip non-number stuff
		//TODO check if this is correct
		while (!Characters.canStartNumber(chars.peek()))
			chars.next();
		
		if ((c = chars.peek()) == '-') {
			isPositive = false;
			chars.next();
		} else if (c == '+') {
			//Ignore
			chars.next();
		}
		
		if (chars.peek() == '0') {
			if (!chars.hasNext(2))
				//Is '0'
				return 0;
			
			switch (chars.peek(2)) {
				case 'X':
				case 'x':
					type = NumericLiteralType.HEXDECIMAL;
					chars.next(2);
					return nextHexLiteral();
				case 'b':
				case 'B':
					type = NumericLiteralType.BINARY;
					chars.next(2);
					return nextBinaryLiteral();
				case 'o':
				case 'O':
					type = NumericLiteralType.OCTAL;
					chars.next(2);
					return nextOctalLiteral();
				default:
					//Number starts with a '0', but can be upgraded to a DECIMAL type
					type = NumericLiteralType.OCTAL_IMPLICIT;
					break;
			}
		}
		
		
		chars.mark();
		boolean hasDecimal = false;
		boolean hasExponent = false;
		
		outer:
		while (chars.hasNext()) {
			c = chars.next();
			if (c == '.') {
				if ((type == NumericLiteralType.DECIMAL || type == NumericLiteralType.OCTAL_IMPLICIT)) {
					if (!hasDecimal) {
						hasDecimal = true;
						//Possibly upgrade from OCTAL_IMPLICIT
						type = NumericLiteralType.DECIMAL;
						continue;
					} else {
						//TODO try to remove backwards skips
						chars.skip(-1);
						break;
					}
				}
				chars.unmark();
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
						} else if (c == 'e' || c == 'E') {
							hasExponent = true;
							//Upgrade to decimal, parse exponent
							//TODO refactor with other exponent-parsing code
							//TODO optimize with single pass
							//Read optional '+' or '-' at start of exponent
							if ((c = chars.peek()) == '-')
								chars.next();
							else if (c == '+')
								chars.next();
							
							while (chars.hasNext() && (c = chars.peek()) >= '0' && c <= '9')
								chars.next();
							break outer;
						}
						//Fallthrough intentional
					case OCTAL:
						isValid = c <= '7';
						break;
					case HEXDECIMAL:
						isValid = (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') || (c <= '9');
						break;
					case DECIMAL:
						if (isValid = (c <= '9'))
							break;
						if (c == 'e' || c == 'E') {
							hasExponent = true;
							//Parse exponent
							//TODO optimize with single pass
							//Read optional '+' or '-' at start of exponent
							if ((c = chars.peek()) == '-')
								chars.next();
							else if (c == '+')
								chars.next();
							
							while (chars.hasNext() && (c = chars.peek()) >= '0' && c <= '9')
								chars.next();
							break outer;
						}
				}
			}
			if (!isValid) {
				chars.unmark();
				throw new JSSyntaxException("Unexpected identifier in numeric literal (" + type + "): " + Character.getName(c), chars.position());
			}
		}
		//Build the string for Java to parse (It might be slightly faster to calculate the value of the digit while parsing,
		//but it causes problems with OCTAL_IMPLICIT upgrades.
		String nmb = (isPositive ? "" : "-") + chars.copyFromMark();
		
		//Return number. Tries to cast down to the smallest size that it can losslessly
		if (hasDecimal || hasExponent) {
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
			case '>':// '>'
				if (d == '>') {// '>>'
					// '>=' already handled
					if (e == '>') {// '>>='
						if (chars.hasNext(4) && chars.peek(4) == '=')// '>>>='
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
		if (!chars.hasNext())
			return null;
		
		StringBuilder sb = new StringBuilder();
		
		//Start character
		{
			char startChar = chars.peek();
			if (Characters.canStartIdentifier(startChar)) {
				chars.skip(1);
				sb.append(startChar);
			} else if (startChar == '\\' && chars.peek(2) == 'u') {
				chars.skip(2);
				sb.append(readUnicodeEscapeSequence());
			} else
				//Not start of identifier
				return null;
		}
		
		while (chars.hasNext()) {
			char c = chars.peek();
			if (Characters.isIdentifierPart(c)) {
				chars.skip(1);
				sb.append(c);
			} else if (c == '\\'  && chars.peek(2) == 'u') {
				chars.skip(2);
				sb.append(readUnicodeEscapeSequence());
			} else
				break;
		}
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
		chars.mark();
		while (!isEOF()) {
			char c = chars.peek();
			if (c != 'g' && c!= 'i' && c!= 'm' && c != 'y')
				break;
			chars.next();
		}
		return chars.copyFromMark();
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
				chars.skip(1);
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public Token nextToken() {
		//Return lookahead if available
		if (this.lookahead != null) {
			Token result = this.lookahead;
			if (!result.matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
				skip(result);
			return result;
		}
		
		//Skip whitespace until token
		chars.skipWhitespace();
		
		//Special EOF token
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
		//Check if it's a numeric literal (the first letter of all numbers must be /[\.0-9]/)
		} else if (Characters.isDecimalDigit(c) || (c == '.' && chars.hasNext(2) && Characters.isDecimalDigit(chars.peek(2)))) {
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
		} else if ((c == '/' && chars.hasNext(2) && (chars.peek(2) == '/' || chars.peek(2) == '*')) ||
				(c == '<' && chars.hasNext(4) && chars.peek(2) == '!' && chars.peek(3) == '-' && chars.peek(4) == '-')) {
			kind = TokenKind.COMMENT;
			value = this.nextComment(chars.peek(2) != '*');
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