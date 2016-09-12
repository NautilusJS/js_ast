package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.impl.tree.AbstractTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.StringLiteralTree;
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
	protected void assertLiteral(ExpressionTree expr, String value) {
		assertEquals(Kind.STRING_LITERAL, expr.getKind());
		assertEquals(value, ((StringLiteralTree)expr).getValue());
	}
	protected void assertIdentifier(ExpressionTree expr, String name) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	protected ExpressionTree parseExpression(String expr) {
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
		try {
			parseExpression("++'foo'");
			fail("Did not throw error");
		} catch (JSSyntaxException e) {
			
		}
	}
	
	@Test
	public void testUnaryOnLiteral() {
		ExpressionTree expr = parseExpression("typeof 'foo'");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertLiteral(((UnaryTree)expr).getExpression(), "foo");
		System.out.println(((AbstractTree)expr).toJSON());
	}
	
	@Test
	public void testBinaryExpression() {
		ExpressionTree expr = parseExpression("a+b");
		System.out.println(((AbstractTree)expr).toJSON());
	}
	
}
