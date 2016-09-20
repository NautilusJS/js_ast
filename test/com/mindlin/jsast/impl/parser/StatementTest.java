package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;

public class StatementTest {
	
	@Test
	public void testConsumeSemicolons() {
		JSParser parser = new JSParser();
		JSLexer lexer = new JSLexer("a;");
		assertNotNull(parser.parseStatement(lexer, new Context()));
		assertNull(parser.parseStatement(lexer, new Context()));
	}
	
}
