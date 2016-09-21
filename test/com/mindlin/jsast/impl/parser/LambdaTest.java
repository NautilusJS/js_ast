package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class LambdaTest {
	
	@Test
	public void testInvalid() {
		assertExceptionalExpression("()=>()", "Invalid lambda was parsed");
		assertExceptionalExpression("x=>()", "Invalid lambda was parsed");
		assertExceptionalExpression("(x,y)=>()", "Invalid lambda was parsed");
		assertExceptionalExpression("...x=>()", "Invalid lambda was parsed");
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
