package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;
import org.junit.Test;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class FunctionTest {
	
	@Test
	public void testFunctionExpression() {
		FunctionExpressionTree fn = parseExpression("function(){}", Kind.FUNCTION_EXPRESSION);
		assertEquals(0, fn.getParameters().size());
	}
	
	@Test
	public void testFunctionParameters() {
		{
			FunctionExpressionTree fn = parseExpression("function(a){}", Kind.FUNCTION_EXPRESSION);
			assertEquals(1, fn.getParameters().size());
			assertIdentifier("a", fn.getParameters().get(0).getName());
		}
		{
			FunctionExpressionTree fn = parseExpression("function(a, b, c){}", Kind.FUNCTION_EXPRESSION);
			assertEquals(3, fn.getParameters().size());
		}
	}
	
	@Test
	public void testFunctionRestParameters() {
		{
			FunctionExpressionTree fn = parseExpression("function(...c){}", Kind.FUNCTION_EXPRESSION);
			assertEquals(1, fn.getParameters().size());
		}
		{
			FunctionExpressionTree fn = parseExpression("function(a, b, ...c){}", Kind.FUNCTION_EXPRESSION);
			assertEquals(3, fn.getParameters().size());
		}
	}
}
