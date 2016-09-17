package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.TestUtils.assertNumberEquals;
import static org.junit.Assert.*;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class VariableDeclarationTest {
	@Test
	public void testVariableDeclaration() {
		VariableDeclarationTree decl = (VariableDeclarationTree) parseStatement("var foo : void = 5, bar = foo + 1;");
		assertNumberEquals(2, decl.getDeclarations().size());

		VariableDeclaratorTree declarator0 = decl.getDeclarations().get(0);
		assertIdentifier("foo", declarator0.getIdentifier());
		assertEquals(Kind.VOID_TYPE, declarator0.getType().getKind());
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
