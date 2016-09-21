package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
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
	public void testMultiParamsWithRest() {
		FunctionExpressionTree lambda = parseExpression("(x,...y)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testSingleParamWithType() {
		FunctionExpressionTree lambda = parseExpression("(x:string)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testMultipleParamsWithType() {
		FunctionExpressionTree lambda = parseExpression("(x:string, y:number)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testSingleParamDefaultValue() {
		FunctionExpressionTree lambda = parseExpression("(x = 5)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testSingleParamOptional() {
		FunctionExpressionTree lambda = parseExpression("(x?:string)=>x", Kind.FUNCTION_EXPRESSION);
	}
	
	@Test
	public void testMultiParamsDefaultValue() {
		FunctionExpressionTree lambda = parseExpression("(x = 5, y = 6)=>x", Kind.FUNCTION_EXPRESSION);
	}
}
