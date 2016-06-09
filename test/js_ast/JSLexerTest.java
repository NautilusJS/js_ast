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
		JSLexer lexer = new JSLexer("\"\\r\\n\\\\\\\nf\"");
		String parsed = lexer.parseStringLiteral();
		assertEquals("\r\n\\f", parsed);
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
			Number n = lexer.parseNumberLiteral();
			System.out.println(n);
			assertEquals(0b1010, n.intValue());
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
			Number n = lexer.parseNumberLiteral();
			System.out.println(n);
			assertEquals(1010, n.intValue());
		}
		{
			//Check higher numbers
			JSLexer lexer = new JSLexer("10A");
			try {
				lexer.parseNumberLiteral();
				fail("Failed to throw exception on invalid syntax");
			} catch (JSSyntaxException e) {
				//Expected
			}
		}
		{
			//Check decimals (unsupported in binary numbers)
			JSLexer lexer = new JSLexer("1010.0");
			assertEquals(1010.0, lexer.parseNumberLiteral());
		}
		{
			//Check octal upgrade
			JSLexer lexer = new JSLexer("01019");
			assertEquals(1019L, lexer.parseNumberLiteral());
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
		JSLexer lexer = new JSLexer("\"bar\" for asdd 0xFF");
		Token fooStringToken = lexer.nextToken();
		System.out.println(fooStringToken);
		assertEquals(TokenKind.LITERAL, fooStringToken.getKind());
		assertEquals(fooStringToken.getValue(),"bar");
		
		Token forKeywordToken = lexer.nextToken();
		System.out.println(forKeywordToken);
		assertEquals(TokenKind.KEYWORD, forKeywordToken.getKind());
		assertEquals(forKeywordToken.getValue(), JSKeyword.FOR);
		
		Token asddIdentifierToken = lexer.nextToken();
		System.out.println(asddIdentifierToken);
		assertEquals(TokenKind.IDENTIFIER, asddIdentifierToken.getKind());
		assertEquals("asdd", asddIdentifierToken.getValue());
		
		Token FFNumberToken = lexer.nextToken();
		System.out.println(FFNumberToken);
		assertEquals(TokenKind.LITERAL, FFNumberToken.getKind());
		assertEquals(((Number)FFNumberToken.getValue()).intValue(), 0xFF);
	}
}
