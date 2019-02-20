package com.mindlin.jsast.impl.lexer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mindlin.jsast.exception.JSEOFException;
import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.impl.parser.JSKeyword;
import com.mindlin.jsast.impl.parser.JSOperator;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;
import com.mindlin.jsast.impl.tree.LineMap;
import com.mindlin.jsast.impl.tree.LineMap.LineMapBuilder;
import com.mindlin.jsast.impl.util.BooleanStack;
import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.impl.util.Characters;

public class JSLexer implements Supplier<Token> {
	
	protected final CharacterStream chars;
	protected Token lookahead = null;
	protected LinkedList<Token> lookaheads = new LinkedList<>();
	//TODO: supply source file
	protected final LineMapBuilder lines;
	protected final BooleanStack templateStack = new BooleanStack();
	
	public JSLexer(String src) {
		this(new CharacterArrayStream(src));
	}
	
	public JSLexer(char[] chars) {
		this(new CharacterArrayStream(chars));
	}
	
	public JSLexer(CharacterStream chars) {
		this(null, chars);
	}
	
	public JSLexer(SourceFile source) {
		this(source, source.getSourceStream());
	}
	
	public JSLexer(SourceFile source, CharacterStream chars) {
		this.lines = new LineMapBuilder(source);
		this.chars = chars;
	}
	
	public LineMap getLines() {
		return lines;
	}
	
	public SourcePosition getPosition() {
		return this.resolvePosition(this.getPositionOffset());
	}
	
	public SourcePosition resolvePosition(long position) {
		return this.lines.lookup(position);
	}
	
	public long getPositionOffset() {
		return this.chars.position();
	}
	
	/**
	 * Get start of next token. Equivalent to {@code this.peek().getStart()}.
	 * 
	 * @return Start of next token.
	 */
	public SourcePosition getNextStart() {
		return this.peek().getStart();
	}
	
	public CharacterStream getCharacters() {
		return this.chars;
	}
	
	protected void invalidateLookaheads(long clobberIdx) {
		if (this.lookahead == null) {
			// No-op
		} else if (clobberIdx <= this.lookahead.getEnd().getOffset()) {
			this.lookahead = null;
			this.lookaheads.clear();
		} else if (!this.lookaheads.isEmpty()) {
			// Clobber some
			for (Iterator<Token> iter = this.lookaheads.descendingIterator(); iter.hasNext(); ) {
				Token current = iter.next();
				if (clobberIdx > current.getEnd().getOffset())
					break;
				iter.remove();
			}
		}
	}
	
	public String nextStringLiteral() {
		chars.skipWhitespace();
		if (chars.hasNext())
			return nextStringLiteral(chars.next());
		return null;
	}
	
	protected String readEscapeSequence(char c) {
		switch (c) {
			case '\'':
			case '"':
			case '\\':
			case '`':
				return Character.toString(c);
			case 'n':
				return "\n";
			case 'r':
				return "\r";
			case 'v':
				return Character.toString(Characters.VT);// vertical tab; it's a thing.
			case 't':
				return "\t";
			case 'b':
				return "\b";
			case 'f':
				return "\f";
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
					return Charset.forName("EASCII").decode(ByteBuffer.wrap(new byte[]{(byte)val})).toString();
				}
			case '\n':
				this.lines.putNewline(chars.position());
				if (chars.hasNext() && chars.peek() == '\r')
					chars.skip(1);
				return "";
			case '\r':
				if (chars.hasNext() && chars.peek() == '\n') {
					this.lines.putNewline(chars.position());
					chars.skip(1);
				}
				return "";
			case 'u': {
				//Unicode escape
				return this.readUnicodeEscapeSequence();
			}
			case 'x':
				//EASCII hexdecimal character escape
				if (!chars.hasNext(2))
					throw new JSEOFException("Invalid Extended ASCII escape sequence (EOF)", this.resolvePosition(this.getPositionOffset() - 2));
				try {
					String s = chars.copyNext(2);
					Charset easciiCharset = Charset.forName("EASCII");
					CharBuffer buffer = easciiCharset.decode(ByteBuffer.wrap(new byte[]{(byte) Integer.parseInt(s, 16)}));
					return buffer.toString();
				} catch (NumberFormatException e) {
					SourcePosition start = this.resolvePosition(this.getPositionOffset() - 4);
					throw new JSSyntaxException("Invalid Extended ASCII escape sequence (\\x" + chars.copy(chars.position() - 1, 2) + ")", new SourceRange(start, this.getPosition()), e);
				}
			default:
				throw new JSSyntaxException("Invalid escape sequence: \\" + c, this.resolvePosition(this.getPositionOffset() - 2));
		}
	}
	
	/**
	 * Read an escape sequence in the form of either {@code uXXXX} or <code>u{X...XXX}</code>.
	 * 
	 * It is expected that the 'u' at the start of the escape sequence has already been consumed.
	 * 
	 * @return character read
	 * @see <a href="https://tc39.github.io/ecma262/#prod-UnicodeEscapeSequence">ECMAScript 262 &sect; 11.8.4</a>
	 * @see <a href="https://mathiasbynens.be/notes/javascript-escapes">JavaScript character escape sequences · Mathias Bynens</a>
	 */
	protected String readUnicodeEscapeSequence() {
		int value = 0;
		if (chars.peek() == '{') {
			chars.skip(1);
			//Max unicode code point is 0x10FFFF, which fits within a 32-bit integer
			char c = chars.next();
			do {
				int digit = Characters.asHexDigit(c);
				if (c < 0)
					throw new JSSyntaxException("Invalid character '" + c + "' in unicode code point escape sequence", this.getPosition());
				//Overflow (value >= 0x10FFFF)
				if ((value << 4) < value)
					throw new JSSyntaxException("Invalid Unicode code point " + value, this.getPosition());
				value = (value << 4) | digit;
			} while ((c = chars.next()) != '}');
		} else {
			//Escape in uXXXX form
			for (int i = 0; i < 4; i++) {
				char c = chars.next();
				int digit = Characters.asHexDigit(c);
				if (digit < 0)
					throw new JSSyntaxException("Invalid character '" + c + "' in unicode code point escape sequence", this.getPosition());
				value = (value << 4) | digit;
			}
		}
		return new String(new int[]{value}, 0, 1);
	}
	
	protected TemplateTokenInfo nextTemplateLiteral() {
		boolean head = chars.hasNext() && chars.peek() == '`';
		chars.skip(1);//Skip start '`'/'}';
		if (!head)
			this.templateStack.pop();//Pop template marker from stack
		
		boolean tail = false;
		boolean escaped = false;
		StringBuilder cooked = new StringBuilder();
		
		loop:
		while (true) {
			if (!chars.hasNext())
				throw new JSEOFException("Unexpected EOF while parsing a template literal", this.getPosition());
			char c = chars.next();
			if (escaped) {
				escaped = false;
				cooked.append(this.readEscapeSequence(c));
				continue;
			}
			switch (c) {
				case '`':
					tail = true;
					break loop;
				case '$': {
					if (chars.hasNext() && chars.peek() == '{') {
						chars.next();
						this.templateStack.push(true);
						break loop;
					}
					cooked.append('$');
					break;
				}
				case '\\':
					escaped = true;
					continue;
				case '\r':
					// '\r\n' is treated as a single character
					if (chars.hasNext() && chars.peek() == '\n')
						chars.skip(1);
					//breakthrough intentional
				case '\n':
					cooked.append('\n');
					break;
				default:
					cooked.append(c);
					break;
			}
		}
		
		return new TemplateTokenInfo(head, tail, cooked.toString());
	}
	
	public String nextStringLiteral(final char startChar) {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (true) {
			if (!chars.hasNext())
				throw new JSEOFException("Unexpected EOF while parsing a string literal (" + sb + ")", this.getPosition());
			char c = chars.next();
			if (isEscaped) {
				isEscaped = false;
				sb.append(readEscapeSequence(c));
				continue;
			} else if (c == '\\') {
				isEscaped = true;
				continue;
			} else if (c == '\r' || c == '\n') {
				//TODO remove (template literals are handled elsewhere)
				if (startChar == '`') {
					//Newlines are allowed as part of a template literal
					if (chars.hasNext() && ((c == '\r' && chars.peek() == '\n') || chars.peek() == '\r'))
						chars.skip(1);
					sb.append('\n');
					continue;
				}
				throw new JSSyntaxException("Illegal newline in the middle of a string literal", this.getPosition());
			} else if (c == startChar)
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
					throw new JSSyntaxException("Unexpected token", this.getPosition());
				break;
			}
			isEmpty = false;
			result = (result << 4) | Characters.asHexDigit(chars.next());
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", this.getPosition());
		return result;
	}
	
	protected long nextBinaryLiteral() throws JSSyntaxException {
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead != '0' && lookahead != '1') {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected token", this.getPosition());
				break;
			}
			isEmpty = false;
			result = (result << 1) | (chars.next() - '0');
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", this.getPosition());
		return result;
	}
	
	protected long nextOctalLiteral() throws JSSyntaxException {
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead < '0' || '7' < lookahead) {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected character: " + lookahead, this.getPosition());
				break;
			}
			isEmpty = false;
			result = (result << 3) | (chars.next() - '0');
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", this.getPosition());
		return result;
	}
	
	protected Number nextDecimalLiteral() throws JSSyntaxException {
		return null;
	}
	
	public Number nextNumericLiteral() throws JSSyntaxException {
		NumericLiteralType type = NumericLiteralType.DECIMAL;
		
		boolean isPositive = true;
		if (!chars.hasNext())
			throw new JSEOFException("Unexpected EOF while parsing numeric literal", this.getPosition());
		
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
			if (!chars.hasNext(2)) {
				//Is '0'
				chars.skip(1);
				return 0;
			}
			
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
				throw new JSSyntaxException("Unexpected number", this.getPosition());
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
				throw new JSSyntaxException("Unexpected identifier in numeric literal (" + type + "): " + Character.getName(c), this.getPosition());
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
				case '!'://!=
					if (e == '=')//!==
						return JSOperator.STRICT_NOT_EQUAL;
					return JSOperator.NOT_EQUAL;
				case '='://==
					if (e == '=')//===
						return JSOperator.STRICT_EQUAL;
					return JSOperator.EQUAL;
				default:
					break;
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
				return JSOperator.ASTERISK;
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
				return JSOperator.AMPERSAND;
			case '^':
				return JSOperator.BITWISE_XOR;
			case '|':
				if (d == '|')
					return JSOperator.LOGICAL_OR;
				return JSOperator.VBAR;
			case '!':
				return JSOperator.LOGICAL_NOT;
			case '~':
				return JSOperator.BITWISE_NOT;
			case '(':
				return JSOperator.LEFT_PARENTHESIS;
			case ')':
				return JSOperator.RIGHT_PARENTHESIS;
			case '[':
				return JSOperator.LEFT_BRACKET;
			case ']':
				return JSOperator.RIGHT_BRACKET;
			case '{':
				return JSOperator.LEFT_BRACE;
			case '}':
				return JSOperator.RIGHT_BRACE;
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
		if (!Objects.equals(t.getValue(), value))
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
				throw new JSSyntaxException("Unexpected EOF while parsing a regex literal", this.getPosition());
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
						throw new JSSyntaxException("Illegal regular expression character class end", this.getPosition());
					break;
				case ')':
					if (--groups < 0)
						throw new JSSyntaxException("Illegal regular expression group end", this.getPosition());
					break;
				default:
					break;
			}
		}
		if (chars.current() != '/')
			throw new JSEOFException("Unexpected EOF in regex literal", this.getPosition());
		return sb.toString();
	}
	
	protected String scanRegExpFlags() {
		StringBuilder sb = new StringBuilder();
		while (!isEOF()) {
			char c = chars.peek();
			if (c != 'g' && c!= 'i' && c!= 'm' && c != 'y')
				break;
			sb.append(chars.next());
		}
		return sb.toString();
	}
	
	public Token finishRegExpLiteral(Token start) {
		Objects.requireNonNull(start);
		if (start.getValue() != JSOperator.DIVISION && start.getValue() != JSOperator.DIVISION_ASSIGNMENT)
			throw new JSSyntaxException("Regular expression must start with a slash", start.getRange());
		
		this.invalidateLookaheads(start.getEnd().getOffset());
		
		long intermediateStart = this.getPositionOffset();
		String body = scanRegExpBody(start.text);
		String flags = scanRegExpFlags();
		RegExpTokenInfo info = new RegExpTokenInfo(body, flags);
		
		SourceRange range = new SourceRange(start.getStart(), this.getPosition());
		return new Token(start.flags, range, TokenKind.REGEX_LITERAL, start.text + chars.copy(intermediateStart, chars.position() - intermediateStart), info);
	}
	
	public String nextComment(final boolean singleLine) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (!chars.hasNext()) {
				if (singleLine)
					break;
				throw new JSEOFException("Unexpected EOF while parsing comment", this.getPosition());
			}
			char c = chars.next();
			
			//Mark newline
			if (c == '\n')
				this.lines.putNewline(chars.position());
			
			//End conditions
			if (singleLine) {
				if (c == '\n' || c == '\r')
					break;
			} else if (c == '*' && chars.hasNext() && chars.peek() == '/') {
				chars.skip(1);
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	protected Token readToken() {
		//Skip whitespace until token
		int flags = 0;
		while (chars.hasNext() && Characters.isJsWhitespace(chars.peek())) {
			if (chars.next() == '\n') {
				flags |= Token.FLAG_PRECEDEING_NEWLINE;
				//TODO: does this cause problems if we're backtracking?
				this.lines.putNewline(chars.position());
			}
		}
		
		//Special EOF token
		if (isEOF()) {
			SourceRange range = new SourceRange(this.getPosition(), this.getPosition());
			Token result = new Token(flags, range, TokenKind.SPECIAL, null, JSSpecialGroup.EOF);
			if (this.lookahead == null)
				this.lookahead = result;
			return result;
		}
		
		final long start = Math.max(chars.position(), -1);
		chars.mark();
		char c = chars.peek();
		Object value = null;
		TokenKind kind = null;
		//TODO clean up (possibly with switch statement)
		//Drop through a various selection of possible results
		//Check if it's a string literal
		if (c == '"' || c == '\'') {
			value = nextStringLiteral();
			kind = TokenKind.STRING_LITERAL;
		} else if (c == '`' || (c == '}' && this.templateStack.getSize() != 0 && this.templateStack.peek())) {
			value = nextTemplateLiteral();
			kind = TokenKind.TEMPLATE_LITERAL;
		//Check if it's a numeric literal (the first letter of all numbers must be /[\.0-9]/)
		} else if (Characters.isDecimalDigit(c) || (c == '.' && chars.hasNext(2) && Characters.isDecimalDigit(chars.peek(2)))) {
			value = nextNumericLiteral();
			kind = TokenKind.NUMERIC_LITERAL;

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
			//TODO: how resistant is templateStack to mark-reset stuff?
			if (value == JSOperator.LEFT_BRACE)
				templateStack.push(false);
			else if (value == JSOperator.RIGHT_BRACE && templateStack.getSize() > 0)
				templateStack.pop();
		} else {
			//It's probably an identifier
			String identifierName = this.nextIdentifier();
			if (identifierName == null) {
				//Couldn't even parse an identifier
				throw new JSSyntaxException("Illegal syntax", this.resolvePosition(start));
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
					value = keyword;
					kind = TokenKind.KEYWORD;
				} else {
					value = identifierName;
					kind = TokenKind.IDENTIFIER;
				}
			}
		}
		
		SourceRange range = new SourceRange(this.resolvePosition(start + 1), this.getPosition());
		return new Token(flags, range, kind, chars.copyFromMark(), value);
	}
	
	public Token nextToken() {
		//Return lookahead if available
		if (this.lookahead != null) {
			Token result = this.lookahead;
			//EOF token is sticky
			if (!result.matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
				skip(result);
			return result;
		}
		return this.readToken();
	}
	
	/**
	 * Take & return next token if it matches the provided kind and value.
	 * @param kind
	 * @param value
	 * @return next token if it matches, else null
	 */
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
	
	/**
	 * Take the next token iff it matches the provided kind & value. If it matches, consume it
	 * @param kind
	 * @param value
	 * @return if the next token matches
	 * @see #nextTokenIf(TokenKind, Object)
	 */
	public boolean nextTokenIs(TokenKind kind, Object value) {
		return nextTokenIf(kind, value) != null;
	}
	
	public Token peek() {
		if (this.lookahead != null)
			return this.lookahead;
		chars.mark();
		this.lookahead = this.readToken();
		chars.resetToMark();
		return this.lookahead;
	}
	
	public Token peek(int ahead) {
		if (ahead == 0)
			return peek();
		if (ahead < 0)
			throw new IllegalArgumentException();
		if (this.lookaheads.size() < ahead) {
			// Get last-consumed offset
			Token last = this.lookaheads.isEmpty() ? this.lookahead : this.lookaheads.getLast();
			if (last.matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))// EOF is sticky
				return last;
			
			chars.mark();
			chars.position(last.getEnd().getOffset());
			while (this.lookaheads.size() < ahead)
				this.lookaheads.add(this.readToken());
			chars.resetToMark();
		}
		return this.lookaheads.get(ahead - 1);
	}
	
	public Token skip(Token token) {
		if (token != this.lookahead)
			throw new IllegalArgumentException("Skipped token " + token + " is not lookahead");
		if (token.matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
			throw new IllegalStateException("Cannot skip EOF token " + token);
		
		this.lookahead = this.lookaheads.isEmpty() ? null : this.lookaheads.removeFirst();
		
		chars.position(token.getEnd().getOffset());
		return token;
	}
	
	public void mark() {
		chars.mark();
	}
	
	public void reset() {
		chars.resetToMark();
		this.invalidateLookaheads(chars.position());
	}
	
	public void unmark() {
		chars.unmark();
	}
	
	@Override
	public Token get() {
		return nextToken();
	}
	
	public static class RegExpTokenInfo {
		public final String body;
		public final String flags;
		
		RegExpTokenInfo(String body, String flags) {
			this.body = body;
			this.flags = flags;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(body, flags);
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || !(other instanceof RegExpTokenInfo))
				return false;
			
			RegExpTokenInfo o = (RegExpTokenInfo) other;
			return Objects.equals(this.body, o.body) && Objects.equals(this.flags, o.flags);
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append(this.getClass().getSimpleName())
					.append("{body=\"").append(this.body)
					.append("\",flags=\"").append(this.flags)
					.append("\"}").toString();
		}
	}
	
	public static class TemplateTokenInfo {
		public final boolean head;
		public final boolean tail;
		public final String cooked;
		TemplateTokenInfo(boolean head, boolean tail, String cooked) {
			this.head = head;
			this.tail = tail;
			this.cooked = cooked;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.head, this.tail, this.cooked);
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || !(other instanceof TemplateTokenInfo))
				return false;
			
			TemplateTokenInfo o = (TemplateTokenInfo) other;
			return this.head == o.head && this.tail == o.tail && Objects.equals(this.cooked, o.cooked);
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append(this.getClass().getSimpleName())
					.append("{head=").append(this.head)
					.append(",tail=").append(this.tail)
					.append(",cooked=\"").append(this.cooked)
					.append("\"}").toString();
		}
	}
}