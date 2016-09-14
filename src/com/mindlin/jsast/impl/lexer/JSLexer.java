package com.mindlin.jsast.impl.lexer;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mindlin.jsast.exception.JSEOFException;
import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.parser.JSKeyword;
import com.mindlin.jsast.impl.parser.JSOperator;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;
import com.mindlin.jsast.impl.parser.NumericLiteralType;
import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.impl.util.Characters;

public class JSLexer implements Supplier<Token> {
	protected final CharacterStream chars;
	
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
				throw new JSSyntaxException("Unexpected EOF while parsing a string literal", getPosition());
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
						sb.append(Characters.NULL);
						break;
					case '\n':
						if (chars.hasNext() && chars.peekNext() == '\r')
							chars.skip(1);
						break;
					case '\r':
						if (chars.hasNext() && chars.peekNext() == '\n')
							chars.skip(1);
						break;
					case 'u':
						//Unicode escape
						//TODO finish
						//See mathiasbynens.be/notes/javascript-escapes
						throw new UnsupportedOperationException("Unicode escape sequences aren't supported yet.");
					case 'x':
						//Latin-1 character escape
						if (!chars.hasNext(2))
							throw new JSSyntaxException("Invalid Latin-1 escape sequence (EOF)", getPosition() - 2);
						try {
							sb.append(new String(new byte[]{(byte)Integer.parseInt(chars.copyNext(2), 16)}, 0, 1, StandardCharsets.ISO_8859_1));
						} catch (NumberFormatException e) {
							throw new JSSyntaxException("Invalid Latin-1 escape sequence (\\x" + chars.copy(chars.position() - 2, 2) + ")", getPosition() - 4);
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
					if (chars.hasNext() && ((c == '\r' && chars.peekNext() == '\n') || chars.peekNext() == '\r'))
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
					type = NumericLiteralType.HEXDECIMAL;
					c = chars.next();
					break;
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
				if (type == NumericLiteralType.DECIMAL && !hasDecimal) {
					hasDecimal = true;
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
			return (int) result;
		return result;
	}
	
	public JSOperator peekOperator() {
		char c = chars.peekNext(),
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
	
	public String nextComment(boolean singleLine) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (!chars.hasNext()) {
				if (singleLine)
					break;
				throw new JSEOFException(chars.position());
			}
			char c = chars.next();
			if (singleLine && (c == '\n' ||c == '\r'))
				break;
			else if (c == '*' && chars.hasNext() && chars.peekNext() == '/') {
				chars.next();
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public Token nextToken() {
		chars.skipWhitespace();
		if (isEOF())
			return new Token(chars.position(), TokenKind.SPECIAL, null, JSSpecialGroup.EOF);
		final long start = Math.max(chars.position(), -1);
		char c = chars.peekNext();
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
		} else if (c == '/') {
			//Could be: '/', '//', '/*', '/=', or regex
			if (!chars.hasNext(2))
				throw new JSEOFException(getPosition());
			c = chars.next(2);
			if (c == '/' || c == '*') {
				kind = TokenKind.COMMENT;
				value = this.nextComment(c == '/');
			} else if (c == '=') {
				kind = TokenKind.OPERATOR;
				value = JSOperator.DIVISION_ASSIGNMENT;
			} else {
				kind = TokenKind.OPERATOR;
				value = JSOperator.DIVISION;
			}
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
					if (chars.hasNext() && chars.peekNext() == '*' && (keyword == JSKeyword.FUNCTION || keyword == JSKeyword.YIELD)) {
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
		return new Token(start, kind, chars.copy(start + 1, chars.position() - start), value);
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
	
	public Token peek() {
		chars.mark();
		Token result = nextToken();
		chars.resetToMark();
		return result;
	}
	
	public Token skip(Token token) {
		//chars.skip(token.getLength());
		chars.position(token.getEnd());
		return token;
	}

	@Override
	public Token get() {
		return nextToken();
	}
}