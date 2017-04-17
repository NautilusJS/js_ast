package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.SwitchTree;

public class StatementTest {
	
	@Test
	public void testConsumeSemicolons() {
		JSParser parser = new JSParser();
		JSLexer lexer = new JSLexer("a;");
		assertNotNull(parser.parseStatement(lexer, new Context()));
		assertNull(parser.parseStatement(lexer, new Context()));
	}
	
	@Test
	public void testEmptySwitch() {
		SwitchTree st = parseStatement("switch(foo){}");
		assertIdentifier("foo", st.getExpression());
		assertEquals(0, st.getCases().size());
	}
	
	@Test
	public void testSwitchDefault() {
		SwitchTree st = parseStatement("switch(foo){"
				+ "default:"
				+ "}");
		assertIdentifier("foo", st.getExpression());
		assertEquals(1, st.getCases().size());
	}
	
	@Test
	public void testSwitchSingleCase() {
		SwitchTree st = parseStatement("switch(foo){"
				+ "case 'a':"
				+ "bar();"
				+ "}");
		assertIdentifier("foo", st.getExpression());
		assertEquals(1, st.getCases().size());
	}
	
	@Test
	public void testSwitchInvalid() {
		assertExceptionalStatement("switch(foo){notCaseOrDefault:}", "Parsed invalid switch statement");
		assertExceptionalStatement("switch(foo){var:}", "Parsed invalid switch statement");
	}
}
