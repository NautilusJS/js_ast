package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

import org.junit.Test;

import static org.junit.Assert.*;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree;

public class IdentifierTest {
	
	@Test
	public void testSimpleIdentifier() {
		assertIdentifier("a", parseExpression("a"));
	}
	
	@Test
	public void testNumbersInIdentifier() {
		assertIdentifier("s1234567890", parseExpression("s1234567890"));
	}
	
	@Test
	public void test$InIdentifier() {
		assertIdentifier("$", parseExpression("$"));
		assertIdentifier("$foo", parseExpression("$foo"));
		assertIdentifier("bar$", parseExpression("bar$"));
	}
	
	@Test
	public void testInvalidIdentifiers() {
		ExpressionTree expr = parseExpression("null");
		assertNotEquals(Tree.Kind.IDENTIFIER, expr.getKind());
		
		//TODO expound
	}
}
