package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.Tree.Kind;

public class VariableDeclarationTest {
	
	@Test
	public void testVariableDeclaration() {
		VariableDeclarationTree declaration = parseStatement("var x;", Kind.VARIABLE_DECLARATION);
		assertFalse(declaration.isConst());
		assertFalse(declaration.isScoped());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertIdentifier("x", declarator.getIdentifier());
		assertNull(declarator.getIntitializer());
	}
	
	@Test
	public void testScopedVariableDeclaration() {
		VariableDeclarationTree declaration = parseStatement("let x;", Kind.VARIABLE_DECLARATION);
		assertFalse(declaration.isConst());
		assertTrue(declaration.isScoped());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertIdentifier("x", declarator.getIdentifier());
		assertNull(declarator.getIntitializer());
	}
	
	@Test
	public void testConstVariableDeclaration() {
		
		//We have to have an initializer
		assertExceptionalExpression("const x;", "Did not throw exception for const declaration with no initializer");
		
		VariableDeclarationTree declaration = parseStatement("const x = 5;", Kind.VARIABLE_DECLARATION);
		assertTrue(declaration.isConst());
		assertTrue(declaration.isScoped());
		assertEquals(1, declaration.getDeclarations().size());
		
		VariableDeclaratorTree declarator = declaration.getDeclarations().get(0);
		assertIdentifier("x", declarator.getIdentifier());
		assertLiteral(5, declarator.getIntitializer());
	}
	
	@Test
	public void testComplexVariableDeclaration() {
		VariableDeclarationTree decl = (VariableDeclarationTree) parseStatement("var foo : number = 5, bar = foo + 1;");
		assertEquals(2, decl.getDeclarations().size());

		VariableDeclaratorTree declarator0 = decl.getDeclarations().get(0);
		assertIdentifier("foo", declarator0.getIdentifier());
		assertSpecialType(SpecialType.NUMBER, declarator0.getType());
		assertNotNull(declarator0.getIntitializer());
		assertLiteral(5, declarator0.getIntitializer());

		VariableDeclaratorTree declarator1 = decl.getDeclarations().get(1);
		assertIdentifier("bar", declarator1.getIdentifier());
		assertNull(declarator1.getType());
		assertEquals(Kind.ADDITION, declarator1.getIntitializer().getKind());
		BinaryTree bin1 = (BinaryTree) declarator1.getIntitializer();
		assertIdentifier("foo", bin1.getLeftOperand());
		assertLiteral(1, bin1.getRightOperand());
	}
}
