package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.VariableDeclarationTree.VariableDeclarationKind;

public class VariableDeclarationTest {
	
	@Test
	public void testVariableDeclaration() {
		VariableDeclarationTree declaration = parseStatement("var x;", Kind.VARIABLE_DECLARATION);
		assertEquals(VariableDeclarationKind.VAR, declaration.getDeclarationStyle());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertIdentifier("x", declarator.getName());
		assertNull(declarator.getInitializer());
	}
	
	@Test
	public void testScopedVariableDeclaration() {
		VariableDeclarationTree declaration = parseStatement("let x;", Kind.VARIABLE_DECLARATION);
		assertEquals(VariableDeclarationKind.LET, declaration.getDeclarationStyle());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertIdentifier("x", declarator.getName());
		assertNull(declarator.getInitializer());
	}
	
	@Test
	public void testConstVariableDeclaration() {
		
		//We have to have an initializer
		assertExceptionalExpression("const x;", "Did not throw exception for const declaration with no initializer");
		
		VariableDeclarationTree declaration = parseStatement("const x = 5;", Kind.VARIABLE_DECLARATION);
		assertEquals(VariableDeclarationKind.CONST, declaration.getDeclarationStyle());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertIdentifier("x", declarator.getName());
		assertLiteral(5, declarator.getInitializer());
	}
	
	@Test
	public void testComplexVariableDeclaration() {
		VariableDeclarationTree decl = (VariableDeclarationTree) parseStatement("var foo : number = 5, bar = foo + 1;");
		assertEquals(2, decl.getDeclarations().size());

		VariableDeclaratorTree declarator0 = decl.getDeclarations().get(0);
		assertIdentifier("foo", declarator0.getName());
		assertSpecialType(SpecialType.NUMBER, declarator0.getType());
		assertNotNull(declarator0.getInitializer());
		assertLiteral(5, declarator0.getInitializer());

		VariableDeclaratorTree declarator1 = decl.getDeclarations().get(1);
		assertIdentifier("bar", declarator1.getName());
		assertNull(declarator1.getType());
		BinaryExpressionTree bin1 = assertKind(Kind.ADDITION, declarator1.getInitializer());
		assertIdentifier("foo", bin1.getLeftOperand());
		assertLiteral(1, bin1.getRightOperand());
	}
}
