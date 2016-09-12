package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class JSParserTest {
	
	@Test
	public void testForLoop() {
		JSParser parser = new JSParser();
		CompilationUnitTree result = parser.apply("foo.js", "for(var i = 0; i < 100; i++){}");
		System.out.println(result);
		fail("Not yet implemented");
	}
	
	@Test
	public void testExpression() {
		JSParser parser = new JSParser();
		ExpressionTree expr = parser.parseNextExpression(new JSLexer("a"), new Context());
	}
	
}
