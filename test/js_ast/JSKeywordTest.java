package js_ast;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.parser.JSKeyword;

public class JSKeywordTest {
	
	@Test
	public void testLookup() {
		assertEquals(JSKeyword.IF, JSKeyword.lookup("if"));
		
		assertEquals(JSKeyword.FUNCTION, JSKeyword.lookup("function"));
		assertEquals(JSKeyword.FUNCTION_GENERATOR, JSKeyword.lookup("function*"));
		
		assertEquals(JSKeyword.YIELD, JSKeyword.lookup("yield"));
		assertEquals(JSKeyword.YIELD_GENERATOR, JSKeyword.lookup("yield*"));
	}
	
	@Test
	public void testLookupMixedCase() {
		assertEquals(null, JSKeyword.lookup("IF"));
		assertEquals(null, JSKeyword.lookup("If"));
		assertEquals(null, JSKeyword.lookup("If"));
	}
}
