package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class OperatorTest {
	
	@Test
	public void testConditional() {
		ConditionalExpressionTree expr = parseExpression("a?b:c", Kind.CONDITIONAL);
		assertIdentifier("a", expr.getCondition());
		assertIdentifier("b", expr.getTrueExpression());
		assertIdentifier("c", expr.getFalseExpression());
	}
	
	@Test
	public void testChainedNews() {
		NewTree newExpr = parseExpression("new new X()", Kind.NEW);
		assertEquals(Kind.NEW, newExpr.getCallee().getKind());
		NewTree nested = (NewTree) newExpr.getCallee();
		assertIdentifier("X", nested.getCallee());
	}
	
	@Test
	public void testNewParams() {
		NewTree newExpr = parseExpression("new X(a,b)", Kind.NEW);
		assertIdentifier("X", newExpr.getCallee());
		assertEquals(2, newExpr.getArguments().size());
		assertIdentifier("a", newExpr.getArguments().get(0));
		assertIdentifier("b", newExpr.getArguments().get(1));
	}
	
	@Test
	public void testNewWithoutParens() {
		NewTree newExpr = parseExpression("new X", Kind.NEW);
		assertIdentifier("X", newExpr.getCallee());
		assertNull(newExpr.getArguments());
	}
	
	@Test
	public void testFunctionCall() {
		FunctionCallTree expr = parseExpression("a(b)", Kind.FUNCTION_INVOCATION);
		assertIdentifier("a", expr.getCallee());
	}
	
	@Test
	public void testFunctionCallWithSelect() {
		FunctionCallTree expr = parseExpression("foo.bar(baz)", Kind.FUNCTION_INVOCATION);
		assertEquals(Kind.MEMBER_SELECT, expr.getCallee().getKind());
		BinaryExpressionTree callee = (BinaryExpressionTree) expr.getCallee();
		assertIdentifier("foo", callee.getLeftOperand());
		assertIdentifier("bar", callee.getRightOperand());
		assertEquals(1, expr.getArguments().size());
		assertIdentifier("baz", expr.getArguments().get(0));
	}
}
