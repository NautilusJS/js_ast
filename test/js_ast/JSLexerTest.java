package js_ast;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.parser.JSLexer;
import com.mindlin.jsast.impl.parser.JSSyntaxException;

public class JSLexerTest {
	
	@Test
	public void testNextPrev() {
		JSLexer lexer = new JSLexer("123456789");
		assertEquals(lexer.next(), '1');
		System.out.println(lexer.getPosition());
		assertEquals(lexer.next(), '2');
		System.out.println(lexer.getPosition());
		assertEquals(lexer.prev(), '1');
		System.out.println(lexer.getPosition());
		assertEquals(lexer.next(), '2');
		System.out.println(lexer.getPosition());
	}
	@Test
	public void testParseSimpleStringLiteral() {
		JSLexer lexer = new JSLexer("\"Hello, world\"");
		String parsed = lexer.parseStringLiteral();
		assertEquals(parsed, "Hello, world");
//		fail("Not yet implemented");
	}
	
	@Test
	public void parseStringLiteralEscapeSequence() {
		JSLexer lexer = new JSLexer("\"\\r\\n\\\\\\\nf\"");
		String parsed = lexer.parseStringLiteral();
		assertEquals(parsed, "\r\n\\f");
	}
	
	@Test
	public void parseStringLiteralComplexNewline() {
		JSLexer lexer = new JSLexer(new StringBuilder()
				.append('"').append('\\').append('\n').append('"')
				.append('"').append('\\').append('\r').append('"')
				.append('"').append('\\').append('\r').append('\n').append('"')
				.append('"').append('\\').append('\n').append('\r').append('"')
				.toString());
		assertTrue(lexer.parseStringLiteral().isEmpty());
		assertTrue(lexer.parseStringLiteral().isEmpty());
		assertTrue(lexer.parseStringLiteral().isEmpty());
		assertTrue(lexer.parseStringLiteral().isEmpty());
	}
	@Test
	public void parseStringLiteralComplexQuotes() {
		JSLexer lexer = new JSLexer(new StringBuilder()
				.append('"').append('\'').append('f').append('\'').append('"')
				.append('\'').append('"').append('g').append('"').append('\'')
				.toString());
		assertEquals(lexer.parseStringLiteral(), "'f'");
		assertEquals(lexer.parseStringLiteral(), "\"g\"");
	}
	
	@Test
	public void parseNumberLiteralBinary() {
		{
			JSLexer lexer = new JSLexer("0b1010");
			Number n = lexer.parseNumberLiteral();
			assertEquals(n.intValue(), 0b1010);
		}
		{
			JSLexer lexer = new JSLexer("0B1010");
			Number n = lexer.parseNumberLiteral();
			assertEquals(n.intValue(), 0b1010);
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
			//Check decimals
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
			assertEquals(lexer.parseNumberLiteral().intValue(), 0b0001);
			assertEquals(lexer.parseNumberLiteral().intValue(), 0b0010);
			assertEquals(lexer.parseNumberLiteral().intValue(), 0b0011);
			assertEquals(lexer.parseNumberLiteral().intValue(), 0b0100);
			assertEquals(lexer.parseNumberLiteral().intValue(), 0b0101);
			assertEquals(lexer.parseNumberLiteral().intValue(), 0b0110);
		}
	}
	
}
