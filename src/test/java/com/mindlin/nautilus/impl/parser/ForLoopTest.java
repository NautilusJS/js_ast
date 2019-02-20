package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclarationTree.VariableDeclarationKind;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

public class ForLoopTest {
	@Test
	public void testForInLoop() {
		ForEachLoopTree loop = parseStatement("for(var i in [1,2,3]);", Kind.FOR_IN_LOOP);
		
		VariableDeclarationTree declaration = (VariableDeclarationTree) loop.getVariable();
		assertEquals(VariableDeclarationKind.VAR, declaration.getDeclarationStyle());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertNull(declarator.getInitializer());
		assertIdentifier("i", declarator.getName());
		
		// Check expression
		ArrayLiteralTree value = assertKind(Kind.ARRAY_LITERAL, loop.getExpression());
		assertEquals(3, value.getElements().size());
		assertLiteral(1, value.getElements().get(0));
		assertLiteral(2, value.getElements().get(1));
		assertLiteral(3, value.getElements().get(2));
	}
	
	@Test
	public void testForLoop() {
		ForLoopTree loop = parseStatement("for(var i = 0; i < 10; i++);", Kind.FOR_LOOP);
		
		//TODO check initializer
		
		BinaryExpressionTree condition = assertKind(Kind.LESS_THAN, loop.getCondition());
		assertIdentifier("i", condition.getLeftOperand());
		assertLiteral(10, condition.getRightOperand());
		
		assertEquals(Kind.POSTFIX_INCREMENT, loop.getUpdate().getKind());
		assertIdentifier("i", ((UnaryTree)loop.getUpdate()).getExpression());
	}
	
	@Test
	public void testForLoopBreak() {
		ForLoopTree loop = parseStatement("for(;;)break;", Kind.FOR_LOOP);
		assertEquals(Kind.EMPTY_STATEMENT, loop.getInitializer().getKind());
	}
	
	@Test
	public void testNamedForLoopBreak() {
		LabeledStatementTree loop = parseStatement("foo:for(;;)break foo;", Kind.LABELED_STATEMENT);
		assertIdentifier("foo", loop.getName());
	}
	
	@Test
	public void testForLoopEmpty() {
		ForLoopTree loop = parseStatement("for(;;);", Kind.FOR_LOOP);
		//Initializer not null (should be empty statement)
		assertNull(loop.getCondition());
		assertNull(loop.getUpdate());
	}
	
	@Test
	public void testForOfLoop() {
		ForEachLoopTree loop = parseStatement("for(var i of [1, 2, 3]);", Kind.FOR_OF_LOOP);
		
		VariableDeclarationTree declaration = assertKind(Kind.VARIABLE_DECLARATION, loop.getVariable());
		assertEquals(VariableDeclarationKind.VAR, declaration.getDeclarationStyle());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertNull(declarator.getInitializer());
		assertIdentifier("i", declarator.getName());
		
		ArrayLiteralTree value = assertKind(Kind.ARRAY_LITERAL, loop.getExpression());
		assertEquals(3, value.getElements().size());
		assertLiteral(1, value.getElements().get(0));
		assertLiteral(2, value.getElements().get(1));
		assertLiteral(3, value.getElements().get(2));
	}
	
	@Test
	public void testInvalidForLoop() {
		assertExceptionalExpression("for(var x;);", "Incomplete for loop");
	}
}
