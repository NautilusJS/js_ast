package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ExpressionTree;

import static com.mindlin.jsast.impl.parser.JSParserTest.*
;
public class LambdaTest {

	@Test
	public void testInvalid() {
		assertExceptionalExpression("()=>()", "Invalid lambda was parsed");
	}
	
	@Test
	public void testSingleParamNoParen() {
		ExpressionTree lambda = parseExpression("x=>x");
	}
	
	@Test
	public void testSingleParanWithParen() {
		ExpressionTree lambda = parseExpression("(x)=>x");
	}
	
	@Test
	public void testSingleRestParam() {
		ExpressionTree lambda = parseExpression("(...x)=>x");
	}

	@Test
	public void testMultipleParams() {
		ExpressionTree lambda = parseExpression("(x,y)=>x");
	}
}
