package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.ContinueTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.WithTree;

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
	
	@Test
	public void testDebuggerStatement() {
		parseStatement("debugger;", Tree.Kind.DEBUGGER);
		//There is literally nothing to test
	}
	
	@Test
	public void testUnlabeledBreak() {
		BreakTree breakTree = parseStatement("break;", Tree.Kind.BREAK);
		assertNull(breakTree.getLabel());
	}
	
	@Test
	public void testUnlabeledContinue() {
		ContinueTree continueTree = parseStatement("continue;", Tree.Kind.CONTINUE);
		assertNull(continueTree.getLabel());
	}
	
	@Test
	public void testLabeledBreak() {
		BreakTree breakTree = parseStatement("break everything;", Tree.Kind.BREAK);
		assertIdentifier("everything", breakTree.getLabel());
	}
	
	@Test
	public void testLabeledContinue() {
		ContinueTree continueTree = parseStatement("continue later;", Tree.Kind.CONTINUE);
		assertIdentifier("later", continueTree.getLabel());
	}
	
	@Test
	public void testWith() {
		WithTree with = parseStatement("with(0);", Tree.Kind.WITH);
		
		assertLiteral(0, with.getScope());
	}
	
	@Test
	public void testLabelledStatements() {
		LabeledStatementTree labelled = parseStatement("x:;", Tree.Kind.LABELED_STATEMENT);
		
		assertIdentifier("x", labelled.getName());
		assertKind(labelled.getStatement(), Tree.Kind.EMPTY_STATEMENT);
		
		
		labelled = parseStatement("x:y:;", Tree.Kind.LABELED_STATEMENT);
		
		assertIdentifier("x", labelled.getName());
		
		labelled = assertKind(labelled.getStatement(), Tree.Kind.LABELED_STATEMENT);
		assertIdentifier("y", labelled.getName());
		
		assertKind(labelled.getStatement(), Tree.Kind.EMPTY_STATEMENT);
	}
}
