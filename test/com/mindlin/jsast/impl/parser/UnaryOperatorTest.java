package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class UnaryOperatorTest {
	@Test
	public void testPrefixIncrement() {
		UnaryTree expr = parseExpression("++a");
		assertEquals(Kind.PREFIX_INCREMENT, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testPrefixDecrement() {
		UnaryTree expr = parseExpression("--a");
		assertEquals(Kind.PREFIX_DECREMENT, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testPostfixIncrement() {
		UnaryTree expr = parseExpression("a++");
		assertEquals(Kind.POSTFIX_INCREMENT, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testPostfixDecrement() {
		UnaryTree expr = parseExpression("a--");
		assertEquals(Kind.POSTFIX_DECREMENT, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testUnaryPlus() {
		UnaryTree expr = parseExpression("+a");
		assertEquals(Kind.UNARY_PLUS, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testUnaryMinus() {
		UnaryTree expr = parseExpression("-a");
		assertEquals(Kind.UNARY_MINUS, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testLogicalNot() {
		UnaryTree expr = parseExpression("!a");
		assertEquals(Kind.LOGICAL_NOT, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testBitwiseNot() {
		UnaryTree expr = parseExpression("~a");
		assertEquals(Kind.BITWISE_NOT, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testYield() {
		UnaryTree expr = parseExpression("yield a");
		assertEquals(Kind.YIELD, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testYieldToGenerator() {
		UnaryTree expr = parseExpression("yield* a");
		assertEquals(Kind.YIELD_GENERATOR, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testTypeof() {
		UnaryTree expr = parseExpression("typeof a");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testTypeofLiteral() {
		UnaryTree expr = parseExpression("typeof 'foo';");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertLiteral(expr.getExpression(), "foo");
	}

	@Test
	public void testDelete() {
		UnaryTree expr = parseExpression("delete a");
		assertEquals(Kind.DELETE, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testVoid() {
		UnaryTree expr = parseExpression("void a");
		assertEquals(Kind.VOID, expr.getKind());
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testInvalidUnary() {
		final String msg = "Did not throw error on illegal expression";
		assertExceptionalExpression("++'foo'", msg);
		assertExceptionalExpression("++4", msg);
		assertExceptionalExpression("++true", msg);
		assertExceptionalExpression("++null", msg);
		assertExceptionalExpression("'foo'++", msg);
		assertExceptionalExpression("4++", msg);
		assertExceptionalExpression("true++", msg);
		assertExceptionalExpression("null++", msg);
	}
}
