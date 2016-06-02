package js_ast;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.tree.CompilationUnitTree;

public class JSParserTest {
	
	@Test
	public void testForLoop() {
		JSParser parser = new JSParser();
		CompilationUnitTree result = parser.apply("foo", "for(var i = 0; i < 100; i++){}");
		System.out.println(result);
		fail("Not yet implemented");
	}
	
}
