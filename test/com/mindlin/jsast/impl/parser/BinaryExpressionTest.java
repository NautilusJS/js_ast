package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class BinaryExpressionTest {
	@Test
	public void testBinaryExpression() {
		ExpressionTree expr = parseExpression("a>b");
		assertEquals(Kind.GREATER_THAN, expr.getKind());
		BinaryTree binary = (BinaryTree) expr;
		assertIdentifier("a", binary.getLeftOperand());
		assertIdentifier("b", binary.getRightOperand());
	}

	@Test
	public void testNormalLTR() {
		// Non-commutative ltr associativity
		BinaryTree expr = parseExpression("a<<b<<c");
		System.out.println(expr);
		assertEquals(Kind.LEFT_SHIFT, expr.getKind());
		assertIdentifier("c", expr.getRightOperand());
		assertEquals(Kind.LEFT_SHIFT, expr.getLeftOperand().getKind());
		BinaryTree left = (BinaryTree) expr.getLeftOperand();
		assertIdentifier("a", left.getLeftOperand());
		assertIdentifier("b", left.getRightOperand());
	}

	@Test
	public void testExponentiationRTL() {
		BinaryTree expr = parseExpression("a**b**c");
		System.out.println(expr);
		assertEquals(Kind.EXPONENTIATION, expr.getKind());
		assertIdentifier("a", expr.getLeftOperand());
		assertEquals(Kind.EXPONENTIATION, expr.getRightOperand().getKind());
		BinaryTree right = (BinaryTree) expr.getRightOperand();
		assertIdentifier("b", right.getLeftOperand());
		assertIdentifier("c", right.getRightOperand());
	}
}
