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
}
