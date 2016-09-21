package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class LambdaTest {
	
	@Test
	public void testInvalid() {
		assertExceptionalExpression("()=>()", "Empty parentheses aren't a valid lambda body");
		assertExceptionalExpression("x=>()", "Empty parentheses aren't a valid lambda body");
		assertExceptionalExpression("(x,y)=>()", "Empty parentheses aren't a valid lambda body");
		assertExceptionalExpression("...x=>()", "Rest parameters aren't allowed without parentheses");
		assertExceptionalExpression("x:void=>()", "Types aren't allowed without parentheses");
	}
	
	@Test
	public void testSingleParamNoParen() {
		FunctionExpressionTree lambda = parseExpression("x=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testSingleParanWithParen() {
		FunctionExpressionTree lambda = parseExpression("(x)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testSingleRestParam() {
		FunctionExpressionTree lambda = parseExpression("(...x)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testMultipleParams() {
		FunctionExpressionTree lambda = parseExpression("(x,y)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testBracketedBody() {
		FunctionExpressionTree lambda = parseExpression("()=>{}", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void multiParamsWithRest() {
		ExpressionTree lambda = parseExpression("(x,...y)=>x");
	}
}
