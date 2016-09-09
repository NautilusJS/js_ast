package js_ast;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.parser.JSKeyword;
import com.mindlin.jsast.impl.parser.JSSpecialGroup;

public class JSLexerTest {
	
	
	@Test
	public void testParseSimpleStringLiteral() {
		JSLexer lexer = new JSLexer("\"Hello, world\"");
		String parsed = lexer.parseStringLiteral();
		assertEquals("Hello, world", parsed);
//		fail("Not yet implemented");
	}
	
	@Test
	public void parseStringLiteralEscapeSequence() {
		JSLexer lexer = new JSLexer("'\\r\\n\\\\\\\nf\\'\\\"\\``'");
		String parsed = lexer.parseStringLiteral();
		assertEquals("\r\n\\f'\"``", parsed);
	}
	
	@Test
	public void parseStringLiteralComplexNewline() {
		JSLexer lexer = new JSLexer(new StringBuilder()
				.append('"').append('\\').append('\n').append('"')
				.append('"').append('\\').append('\r').append('"')
				.append('"').append('\\').append('\r').append('\n').append('"')
				.append('"').append('\\').append('\n').append('\r').append('"')
				.toString());
		assertEquals("", lexer.parseStringLiteral());
//		String s = lexer.getCharacters().copy(lexer.getCharacters().position(), 4);
//		System.out.println(s);
		assertEquals("", lexer.parseStringLiteral());
		assertEquals("", lexer.parseStringLiteral());
		assertEquals("", lexer.parseStringLiteral());
	}
	@Test
	public void parseStringLiteralComplexQuotes() {
		JSLexer lexer = new JSLexer(new StringBuilder(10)
				.append('"').append('\'').append('f').append('\'').append('"')
				.append('\'').append('"').append('g').append('"').append('\'')
				.toString());
		assertEquals("'f'", lexer.parseStringLiteral());
		assertEquals("\"g\"", lexer.parseStringLiteral());
	}
	
	
	
	@Test
	public void parseNumberLiteralBinary() {
		{
			JSLexer lexer = new JSLexer("0b1010");
			assertEquals(0b1010, lexer.parseNumberLiteral().intValue());
		}
		{
			JSLexer lexer = new JSLexer("0B1010");
			Number n = lexer.parseNumberLiteral();
			assertEquals(0b1010, n.intValue());
		}
		{
			//Check higher numbers
			JSLexer lexer = new JSLexer("0b102");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check decimals (unsupported in binary numbers)
			JSLexer lexer = new JSLexer("0b1010.0");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check termination
			JSLexer lexer = new JSLexer("0b0001 0b0010;0b0011\n0b0100\r0b0101\r0b0110");
			assertEquals(0b0001, lexer.parseNumberLiteral().intValue());
			assertEquals(0b0010, lexer.parseNumberLiteral().intValue());
			assertEquals(0b0011, lexer.parseNumberLiteral().intValue());
			assertEquals(0b0100, lexer.parseNumberLiteral().intValue());
			assertEquals(0b0101, lexer.parseNumberLiteral().intValue());
			assertEquals(0b0110, lexer.parseNumberLiteral().intValue());
		}
	}
	
	@Test
	public void parseNumberLiteralDecimal() {
		{
			JSLexer lexer = new JSLexer("1010");
			assertEquals(1010, lexer.parseNumberLiteral().intValue());
		}
		{
			//Check invalid other characters numbers
			JSLexer lexer = new JSLexer("10A");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check multiple decimals
			JSLexer lexer = new JSLexer("10.0.0");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check octal upgrade
			JSLexer lexer = new JSLexer("01019");
			assertEquals(1019L, lexer.parseNumberLiteral());
		}
		{
			//Check all digits are supported
			JSLexer lexer = new JSLexer("1234567890");
			assertEquals(1234567890L, lexer.parseNumberLiteral());
		}
		{
			//Check decimals
			JSLexer lexer = new JSLexer("32.5");
			assertEquals(32.5, lexer.parseNumberLiteral());
		}
		{
			//Check termination
			JSLexer lexer = new JSLexer("1 10;11\n100\r101\r110");
			assertEquals(1, lexer.parseNumberLiteral().intValue());
			assertEquals(10, lexer.parseNumberLiteral().intValue());
			assertEquals(11, lexer.parseNumberLiteral().intValue());
			assertEquals(100, lexer.parseNumberLiteral().intValue());
			assertEquals(101, lexer.parseNumberLiteral().intValue());
			assertEquals(110, lexer.parseNumberLiteral().intValue());
		}
	}
	
	@Test
	public void testOctalUpgrade() {
		//Test implicit octal
		assertEquals("Incorrectly parsed implicit octal", 076543210, new JSLexer("076543210").parseNumberLiteral().intValue());
		//Test implicit upgrade
		assertEquals("Failed to upgrade implicit octal", 876543210, new JSLexer("0876543210").parseNumberLiteral().intValue());
		//Test explicit octal
		assertEquals("Incorrectly parsed explicit octal", 076543210, new JSLexer("0o76543210").parseNumberLiteral().intValue());
		//Test explicit upgrade fail
		try {
			if (new JSLexer("0o876543210").parseNumberLiteral().intValue() == 876543210)
				fail("Incorrectly upgraded explicit octal literal");
			fail("Failed to throw exception upon illegal explicit octal upgrade");
		} catch (JSSyntaxException e){}
	}
	@Test
	public void parseNumberLiteralHexdecimal() {
		{
			JSLexer lexer = new JSLexer("0x0123456789ABCDEF");
			assertEquals(0x0123456789ABCDEFL, lexer.parseNumberLiteral());
		}
		{
			//Check case insensitivity
			JSLexer lexer = new JSLexer("0xabcdefABCDEF");
			assertEquals(0xABCDEFABCDEFL, lexer.parseNumberLiteral());
		}
		{
			//Check invalid other characters numbers
			JSLexer lexer = new JSLexer("0x10AG");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check decimals (unsupported in hex numbers)
			JSLexer lexer = new JSLexer("0x1010.0");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check termination
			JSLexer lexer = new JSLexer("0x1 0x10;0x11\n0x100\r0x101\r0x110");
			assertEquals(0x1, lexer.parseNumberLiteral().intValue());
			assertEquals(0x10, lexer.parseNumberLiteral().intValue());
			assertEquals(0x11, lexer.parseNumberLiteral().intValue());
			assertEquals(0x100, lexer.parseNumberLiteral().intValue());
			assertEquals(0x101, lexer.parseNumberLiteral().intValue());
			assertEquals(0x110, lexer.parseNumberLiteral().intValue());
		}
	}
	@Test
	public void testEOF() {
		JSLexer lexer = new JSLexer("\"foo\"");
		//Skip string literal
		System.out.println(lexer.parseStringLiteral());
		assertTrue(lexer.isEOF());
		Token eofToken = lexer.nextToken();
		System.out.println(eofToken);
		assertEquals(TokenKind.SPECIAL, eofToken.getKind());
		assertEquals(JSSpecialGroup.EOF, eofToken.getValue());
	}
	@Test
	public void testTokenize() {
		JSLexer lexer = new JSLexer("'bar' for asdd 0xFF");
		
		Token fooStringToken = lexer.nextToken();
		System.out.println(fooStringToken);
		assertEquals(TokenKind.STRING_LITERAL, fooStringToken.getKind());
		assertEquals("bar", fooStringToken.getValue());
		
		Token forKeywordToken = lexer.nextToken();
		System.out.println(forKeywordToken);
		assertEquals(TokenKind.KEYWORD, forKeywordToken.getKind());
		assertEquals(JSKeyword.FOR, forKeywordToken.getValue());
		
		Token asddIdentifierToken = lexer.nextToken();
		System.out.println(asddIdentifierToken);
		assertEquals(TokenKind.IDENTIFIER, asddIdentifierToken.getKind());
		assertEquals("asdd", asddIdentifierToken.getValue());
		
		Token FFNumberToken = lexer.nextToken();
		System.out.println(FFNumberToken);
		assertEquals(TokenKind.NUMERIC_LITERAL, FFNumberToken.getKind());
		assertEquals(0xFF, ((Number)FFNumberToken.getValue()).intValue());
		
		Token EofNumberToken = lexer.nextToken();
		System.out.println(EofNumberToken);
		assertEquals(TokenKind.SPECIAL, EofNumberToken.getKind());
		assertEquals(JSSpecialGroup.EOF, EofNumberToken.getValue());
	}
	
	@Test
	public void testTemplateLiteralNewline() {
		assertEquals("foo\nbar", new JSLexer("`foo\nbar`").parseStringLiteral());
	}
	
	/**
	 * Test that the lexer correctly throws an exception upon an EOF in the
	 * middle of the string literal
	 */
	@Test
	public void testStringLiteralEOF() {
		try {
			new JSLexer("'foo").parseStringLiteral();
			fail("Failed to throw exception upon unexpected EOF");
		} catch (JSSyntaxException e) {}
		try {
			new JSLexer("'foo\\'").parseStringLiteral();
			fail("Failed to throw exception upon unexpected EOF");
		} catch (JSSyntaxException e) {}
	}
	
	@Test
	public void testBooleanLiteral() {
		JSLexer lexer = new JSLexer("true false");
		Token trueToken = lexer.nextToken();
		assertEquals(TokenKind.BOOLEAN_LITERAL, trueToken.getKind());
		assertEquals(true, trueToken.getValue());
		assertEquals("true", trueToken.getText());
		
		Token falseToken = lexer.nextToken();
		assertEquals(TokenKind.BOOLEAN_LITERAL, falseToken.getKind());
		assertEquals(false, falseToken.getValue());
		assertEquals("false", falseToken.getText());
	}
}
