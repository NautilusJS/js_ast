package js_ast;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.parser.JSKeyword;

public class JSKeywordTest {
	
	@Test
	public void testLookup() {
		assertEquals(JSKeyword.lookup("if"), JSKeyword.IF);
		//TODO add more
	}
	
	@Test
	public void testLookupUpperCase() {
		assertEquals(JSKeyword.lookup("IF"), JSKeyword.IF);
	}
}
