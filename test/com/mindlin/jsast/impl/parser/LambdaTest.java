package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.ParameterTree;
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
		assertExceptionalExpression("x?=>()", "Optional parameters not allowed without parentheses");
		assertExceptionalExpression("(x?=5)=>()", "Initializers not allowed for optional parameters");
		assertExceptionalExpression("(x?, y)=>()", "Required parameters may not follow an optional parameter");
	}
	
	@Test
	public void testSingleParamNoParen() {
		FunctionExpressionTree lambda = parseExpression("x=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1,params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		//TODO check body
	}
	
	@Test
	public void testSingleParanWithParen() {
		FunctionExpressionTree lambda = parseExpression("(x)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1,params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testSingleRestParam() {
		FunctionExpressionTree lambda = parseExpression("(...x)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1,params.size());
		
		ParameterTree param0 = params.get(0);
		assertTrue(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testMultipleParams() {
		FunctionExpressionTree lambda = parseExpression("(x,y)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(2, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		ParameterTree param1 = params.get(1);
		assertFalse(param1.isRest());
		assertFalse(param1.isOptional());
		assertNull(param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());
	}
	
	@Test
	public void testBracketedBody() {
		FunctionExpressionTree lambda = parseExpression("()=>{}", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(0, params.size());
	}
	
	@Test
	public void testMultiParamsWithRest() {
		FunctionExpressionTree lambda = parseExpression("(x,...y)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(2, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		ParameterTree param1 = params.get(1);
		assertTrue(param1.isRest());
		assertFalse(param1.isOptional());
		assertNull(param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());
	}
	
	@Test
	public void testSingleParamWithType() {
		FunctionExpressionTree lambda = parseExpression("(x:string)=>x", Kind.FUNCTION_EXPRESSION);

		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		//TODO check type
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testMultipleParamsWithType() {
		FunctionExpressionTree lambda = parseExpression("(x:string, y:number)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(2, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		ParameterTree param1 = params.get(1);
		assertFalse(param1.isRest());
		assertFalse(param1.isOptional());
		assertNull(param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());
	}
	
	@Test
	public void testSingleParamDefaultValue() {
		FunctionExpressionTree lambda = parseExpression("(x = 5)=>x", Kind.FUNCTION_EXPRESSION);

		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertLiteral(5, param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testSingleParamOptionalTyped() {
		FunctionExpressionTree lambda = parseExpression("(x?:string)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertTrue(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testSingleParamTypedDefault() {
		FunctionExpressionTree lambda = parseExpression("(x:number=5)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertLiteral(5, param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testSingleParamOptional() {
		FunctionExpressionTree lambda = parseExpression("(x?)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertTrue(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
	}
	
	@Test
	public void testMultipleParamsOptional() {
		FunctionExpressionTree lambda = parseExpression("(x?, y?)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(2, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertTrue(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		ParameterTree param1 = params.get(1);
		assertFalse(param1.isRest());
		assertTrue(param1.isOptional());
		assertNull(param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());
		
		
		lambda = parseExpression("(x, y?)=>x", Kind.FUNCTION_EXPRESSION);
		
		params = lambda.getParameters();
		assertEquals(2, params.size());
		
		param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		param1 = params.get(1);
		assertFalse(param1.isRest());
		assertTrue(param1.isOptional());
		assertNull(param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());
	}
	
	@Test
	public void testMultiParamsDefaultValue() {
		FunctionExpressionTree lambda = parseExpression("(x = 5, y = 6)=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(2, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertLiteral(5, param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		ParameterTree param1 = params.get(1);
		assertFalse(param1.isRest());
		assertFalse(param1.isOptional());
		assertLiteral(6, param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());
	}
	
	@Test
	public void testNested() {
		FunctionExpressionTree lambda = parseExpression("x=>y=>x", Kind.FUNCTION_EXPRESSION);
		
		List<ParameterTree> params = lambda.getParameters();
		assertEquals(1, params.size());
		
		ParameterTree param0 = params.get(0);
		assertFalse(param0.isRest());
		assertFalse(param0.isOptional());
		assertNull(param0.getInitializer());
		assertIdentifier("x", param0.getIdentifier());
		
		//TODO Finish
		/*FunctionExpressionTree lambda1 = (FunctionExpressionTree) lambda.getBody();
		List<ParameterTree> params1 = lambda1.getParameters();
		assertEquals(1, params1.size());
		
		ParameterTree param1 = params1.get(0);
		assertFalse(param1.isRest());
		assertFalse(param1.isOptional());
		assertLiteral(6, param1.getInitializer());
		assertIdentifier("y", param1.getIdentifier());*/
	}
}
