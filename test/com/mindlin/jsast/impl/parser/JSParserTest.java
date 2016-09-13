package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.impl.tree.AbstractTree;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;

public class JSParserTest {
	
	/*@Test
	public void testForLoop() {
		JSParser parser = new JSParser();
		CompilationUnitTree result = parser.apply("foo.js", "for(var i = 0; i < 100; i++){}");
		System.out.println(result);
		fail("Not yet implemented");
	}*/
	protected static final void assertLiteral(ExpressionTree expr, String value) {
		assertEquals(Kind.STRING_LITERAL, expr.getKind());
		assertEquals(value, ((StringLiteralTree)expr).getValue());
	}
	protected static final void assertIdentifier(ExpressionTree expr, String name) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	protected static void assertExceptionalExpression(String expr, String errorMsg) {
		try {
			parseExpression(expr);
			fail(errorMsg);
		} catch (JSSyntaxException e) {
			
		}
	}
	
	protected static void assertExceptionalStatement(String stmt, String errorMsg) {
		try {
			parseStatement(stmt);
			fail(errorMsg);
		} catch (JSSyntaxException e) {
		}
	}
	
	protected static  StatementTree parseStatement(String stmt) {
		return new JSParser().parseStatement(new JSLexer(stmt), new Context());
	}
	
	protected static ExpressionTree parseExpression(String expr) {
		return new JSParser().parseNextExpression(new JSLexer(expr), new Context());
	}
	
	@Test
	public void testIdentifier() {
		ExpressionTree expr = parseExpression("a");
		assertIdentifier(expr, "a");
	}
	
	@Test
	public void testUnaryExpression() {
		ExpressionTree expr = parseExpression("++a");
		assertEquals(Kind.PREFIX_INCREMENT, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
		System.out.println(((AbstractTree)expr).toJSON());
		
		/*expr = parseExpression("+a");
		assertEquals(Kind.UNARY_PLUS, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");*/
		
		expr = parseExpression("-a");
		assertEquals(Kind.UNARY_MINUS, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
		
		expr = parseExpression("typeof a");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
		
		expr = parseExpression("delete a");
		assertEquals(Kind.DELETE, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
	}
	
	@Test
	public void testInvalidUnary() {
		final String msg = "Did not throw error on illegal expression";
		assertExceptionalExpression("++'foo'", msg);
		assertExceptionalExpression("++4", msg);
		assertExceptionalExpression("++true", msg);
		assertExceptionalExpression("++false", msg);
	}
	
	@Test
	public void testUnaryOnLiteral() {
		ExpressionTree expr = parseExpression("typeof 'foo'");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertLiteral(((UnaryTree)expr).getExpression(), "foo");
	}
	
	@Test
	public void testBinaryExpression() {
		ExpressionTree expr = parseExpression("a+b");
		assertEquals(Kind.ADDITION, expr.getKind());
		BinaryTree binary = (BinaryTree) expr;
		assertIdentifier(binary.getLeftOperand(), "a");
		assertIdentifier(binary.getRightOperand(), "b");
	}
	
	@Test
	public void testVariableDeclaration() {
		StatementTree stmt = parseStatement("var foo : void = 5, bar = foo + 1;");
		//TODO assert that it was parsed correctly
		System.out.println(((AbstractTree)stmt).toJSON());
	}
	
	@Test
	public void testImportStatement() {
		final String msg = "Failed to throw error on illegal import statement";
		{
			StatementTree stmt = parseStatement("import 'foo.js';");
			assertEquals(Tree.Kind.IMPORT, stmt.getKind());
			ImportTree impt = (ImportTree) stmt;
			assertLiteral(impt.getSource(), "foo.js");
			assertTrue(impt.getSpecifiers().isEmpty());
		}
		assertExceptionalStatement("import foo;", msg);
		assertExceptionalStatement("import from 'foo.js';", msg);
		assertExceptionalStatement("import def 'foo.js';", msg);
		assertExceptionalStatement("import * 'foo.js';", msg);
		assertExceptionalStatement("import * as def 'foo.js';", msg);
		assertExceptionalStatement("import * from 'foo.js';", msg);
		assertExceptionalStatement("import {} from 'foo.js';", msg);
		assertExceptionalStatement("import {*} from 'foo.js';", msg);
		assertExceptionalStatement("import {* as def} from 'foo.js';", msg);
		assertExceptionalStatement("import {def} 'foo.js';", msg);
		assertExceptionalStatement("import {def, *} from 'foo.js';", msg);
		assertExceptionalStatement("import {def ghi} from 'foo.js';", msg);
		assertExceptionalStatement("import * as bar;", msg);
		assertExceptionalStatement("import * as bar, def;", msg);
		assertExceptionalStatement("import ;", msg);
		{
			ImportTree impt = (ImportTree)parseStatement("import def from 'foo.js';");
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import * as bar from 'foo.js';");
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import def, * as bar from 'foo.js';");
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import {foo as bar} from 'foo.js';");
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import def, {foo as bar} from 'foo.js';");
		}
		assertExceptionalStatement("import * as a, {foo as bar} from 'foo.js';", msg);
		{
			ImportTree impt = (ImportTree)parseStatement("import 'x';");
		}
	}
	
}
