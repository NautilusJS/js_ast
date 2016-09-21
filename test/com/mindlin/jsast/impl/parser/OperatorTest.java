package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
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
		assertEquals(0, newExpr.getArguments().size());
	}
}
