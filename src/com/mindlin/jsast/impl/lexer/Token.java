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
	public static boolean isKeyword(char[] chars, boolean isStrict) {
		//TODO finish
		final int SQ_TH = ((('t' & 0xFF) << 8) | ('h' & 0xFF));
		int[] wide = new int[chars.length / 2];
		for (int i = 0; i < chars.length / 2; i++)
			wide[i] = ((chars[i * 2] & 0xFF) << 8) | (chars[i * 2 + 1] & 0xFF);
		switch (chars.length) {
			case 0:
			case 1:
				break;
			case 2:
				// Candidates: do, if, in, of
				if (chars[0] == 'i' && (chars[1] == 'f' || chars[1] == 'n'))
					return true;
				else if (wide[0] == ((('d' & 0xFF) << 8) | ('o' & 0xFF)))
					return true;
				else if (wide[0] == ((('o' & 0xFF) << 8) | ('f' & 0xFF)))
					return true;
				break;
			case 3:
				// Candidates: for, new, try, var, (strict) let
				// Test for 'for', 'var'
				if (chars[2] == 'r' && (wide[0] == ((('f' & 0xFF) << 8) | ('o' & 0xFF))
						|| wide[0] == ((('v' & 0xFF) << 8) | ('a' & 0xFF))))
					return true;
				// Test for 'new', (strict) 'let'
				else if (chars[1] == 'e'
						&& ((chars[0] == 'n' && chars[2] == 'w') || (isStrict && chars[0] == 'l' && chars[2] == 't')))
					return true;
				else if (wide[0] == ((('t' & 0xFF) << 8) | ('r' & 0xFF)) && chars[2] == 'y')
					return true;
				break;
			case 4:
				// Candidates: case, else, this, void, with,
				// Test else, case
				if (wide[1] == ((('s' & 0xFF) << 8) | ('e' & 0xFF)) && (wide[0] == ((('c' & 0xFF) << 8) | ('a' & 0xFF))
						|| wide[0] == ((('e' & 0xFF) << 8) | ('l' & 0xFF))))
					return true;
				// Test void, this
				else if (chars[2] == 'i' && ((wide[0] == SQ_TH && chars[3] == 's')
						|| (wide[0] == ((('v' & 0xFF) << 8) | ('o' & 0xFF)) && chars[3] == 'd')))
					return true;
				// Test with
				else if (wide[0] == ((('w' & 0xFF) << 8) | ('i' & 0xFF)) && wide[1] == SQ_TH)
					return true;
			case 5:
				// Candidates: break, catch, class, const, super, throw, while,
				// yield
				
			case 6:
				// Candiates: return, switch, delete, export, import, typeof
			case 7:
				// Candidates: extends, finally, default
				// Test for 'finally', 'default'
				if (chars[3] == 'a' && chars[5] == 'l') {
					if (chars[0] == 'f' && chars[1] == 'i' && chars[2] == 'n' && chars[4] == 'l' && chars[6] == 'y')
						return true;
					if (chars[0] == 'd' && chars[1] == 'e' && chars[2] == 'f' && chars[4] == 'u' && chars[6] == 't')
						return true;
				}
				return testChars(chars, "extends".toCharArray());
		}
		return false;
	}
	
	protected static boolean testChars(char[] a, char... b) {
		for (int i = 0; i < a.length; i++)
			if (a[i] != b[i])
				return false;
		return true;
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