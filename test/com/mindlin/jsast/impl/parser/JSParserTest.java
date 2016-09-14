package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static com.mindlin.jsast.impl.TestUtils.assertNumberEquals;

import org.junit.Test;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.impl.tree.AbstractTree;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

public class JSParserTest {
	
	/*@Test
	public void testForLoop() {
		JSParser parser = new JSParser();
		CompilationUnitTree result = parser.apply("foo.js", "for(var i = 0; i < 100; i++){}");
		System.out.println(result);
		fail("Not yet implemented");
	}*/
	protected static final void assertLiteral(ExpressionTree expr, String value) {
		assertEquals(Kind.STRING_LITERAL, expr.getKind());
		assertEquals(value, ((StringLiteralTree)expr).getValue());
	}
	
	/**
	 * Assert if a number literal matches an expected value. Doubles are considered to be
	 * equivalent with a .0001 tolerance.
	 * @param expr
	 * @param value
	 */
	protected static final void assertLiteral(ExpressionTree expr, Number value) {
		assertEquals(Kind.NUMERIC_LITERAL, expr.getKind());
		Number actual = ((NumericLiteralTree)expr).getValue();
		assertNumberEquals(actual, value);
	}
	
	protected static final void assertIdentifier(ExpressionTree expr, String name) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	protected static void assertExceptionalExpression(String expr, String errorMsg) {
		try {
			parseExpression(expr);
			fail(errorMsg);
		} catch (JSSyntaxException e) {
			
		}
	}
	
	protected static void assertExceptionalStatement(String stmt, String errorMsg) {
		try {
			parseStatement(stmt);
			fail(errorMsg);
		} catch (JSSyntaxException e) {
		}
	}
	
	protected static  StatementTree parseStatement(String stmt) {
		return new JSParser().parseStatement(new JSLexer(stmt), new Context());
	}
	
	protected static ExpressionTree parseExpression(String expr) {
		return new JSParser().parseNextExpression(new JSLexer(expr), new Context());
	}
	
	@Test
	public void testIdentifier() {
		ExpressionTree expr = parseExpression("a");
		assertIdentifier(expr, "a");
	}
	
	@Test
	public void testUnaryExpression() {
		ExpressionTree expr = parseExpression("++a");
		assertEquals(Kind.PREFIX_INCREMENT, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
		System.out.println(((AbstractTree)expr).toJSON());
		
		/*expr = parseExpression("+a");
		assertEquals(Kind.UNARY_PLUS, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");*/
		
		expr = parseExpression("-a");
		assertEquals(Kind.UNARY_MINUS, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
		
		expr = parseExpression("typeof a");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
		
		expr = parseExpression("delete a");
		assertEquals(Kind.DELETE, expr.getKind());
		assertIdentifier(((UnaryTree)expr).getExpression(), "a");
	}
	
	@Test
	public void testInvalidUnary() {
		final String msg = "Did not throw error on illegal expression";
		assertExceptionalExpression("++'foo'", msg);
		assertExceptionalExpression("++4", msg);
		assertExceptionalExpression("++true", msg);
		assertExceptionalExpression("++false", msg);
	}
	
	@Test
	public void testUnaryOnLiteral() {
		ExpressionTree expr = parseExpression("typeof 'foo'");
		assertEquals(Kind.TYPEOF, expr.getKind());
		assertLiteral(((UnaryTree)expr).getExpression(), "foo");
	}
	
	@Test
	public void testBinaryExpression() {
		ExpressionTree expr = parseExpression("a+b");
		assertEquals(Kind.ADDITION, expr.getKind());
		BinaryTree binary = (BinaryTree) expr;
		assertIdentifier(binary.getLeftOperand(), "a");
		assertIdentifier(binary.getRightOperand(), "b");
	}
	
	@Test
	public void testVariableDeclaration() {
		VariableDeclarationTree decl = (VariableDeclarationTree)parseStatement("var foo : void = 5, bar = foo + 1;");
		//TODO assert that it was parsed correctly
		System.out.println(((AbstractTree)decl).toJSON());
		assertNumberEquals(2, decl.getDeclarations().size());
		
		VariableDeclaratorTree declarator0 = decl.getDeclarations().get(0);
		assertIdentifier(declarator0.getIdentifier(), "foo");
		assertEquals(Kind.VOID_TYPE, declarator0.getType().getKind());
		assertNotNull(declarator0.getIntitializer());
		assertLiteral(declarator0.getIntitializer(), 5);
		
		VariableDeclaratorTree declarator1 = decl.getDeclarations().get(1);
		assertIdentifier(declarator1.getIdentifier(), "bar");
		assertNull(declarator1.getType());
		assertEquals(Kind.ADDITION, declarator1.getIntitializer().getKind());
		BinaryTree bin1 = (BinaryTree)declarator1.getIntitializer();
		assertIdentifier(bin1.getLeftOperand(), "foo");
		assertLiteral(bin1.getRightOperand(), 1);
	}
	
	/**
	 * Pretty exhaustive tests for <code>import</code> statement parsing.
	 * I honestly can't think of any (non-trivial) test cases that could be added.
	 */
	@Test
	public void testImportStatement() {
		final String msg = "Failed to throw error on illegal import statement";
		//Things that *shouldn't* work
		assertExceptionalStatement("import ;", msg);
		assertExceptionalStatement("import foo;", msg);
		assertExceptionalStatement("import from 'foo.js';", msg);
		assertExceptionalStatement("import def 'foo.js';", msg);
		assertExceptionalStatement("import * 'foo.js';", msg);
		assertExceptionalStatement("import * as def 'foo.js';", msg);
		assertExceptionalStatement("import * from 'foo.js';", msg);
		assertExceptionalStatement("import {} from 'foo.js';", msg);
		assertExceptionalStatement("import {*} from 'foo.js';", msg);
		assertExceptionalStatement("import {* as def} from 'foo.js';", msg);
		assertExceptionalStatement("import {def} 'foo.js';", msg);
		assertExceptionalStatement("import {def, *} from 'foo.js';", msg);
		assertExceptionalStatement("import {def ghi} from 'foo.js';", msg);
		assertExceptionalStatement("import * as bar;", msg);
		assertExceptionalStatement("import * as bar, def;", msg);
		assertExceptionalStatement("import * as a, {foo as bar} from 'foo.js';", msg);
		assertExceptionalStatement("import {foo as bar from 'foo.js';", msg);
		assertExceptionalStatement("import {'hello' as world} from 'foo.js';", msg);
		assertExceptionalStatement("import {hello as 'world'} from 'foo.js';", msg);
		assertExceptionalStatement("import default from 'foo.js';", msg);
		//Things that *should* work
		//Examples taken from developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import
		{
			ImportTree impt = (ImportTree)parseStatement("import defaultMember from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(1, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier = impt.getSpecifiers().get(0);
			assertIdentifier(specifier.getImported(), "defaultMember");
			assertIdentifier(specifier.getAlias(), "defaultMember");
			assertEquals(specifier.getImported(), specifier.getAlias());
			assertTrue(specifier.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import * as name from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(1, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier = impt.getSpecifiers().get(0);
			assertIdentifier(specifier.getImported(), "*");
			assertIdentifier(specifier.getAlias(), "name");
			assertFalse(specifier.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import { member } from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(1, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier = impt.getSpecifiers().get(0);
			assertIdentifier(specifier.getImported(), "member");
			assertIdentifier(specifier.getAlias(), "member");
			assertEquals(specifier.getImported(), specifier.getAlias());
			assertFalse(specifier.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import { member as alias } from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(1, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
			assertIdentifier(specifier0.getImported(), "member");
			assertIdentifier(specifier0.getAlias(), "alias");
			assertFalse(specifier0.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import { member1 , member2 } from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(2, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
			assertIdentifier(specifier0.getImported(), "member1");
			assertIdentifier(specifier0.getAlias(), "member1");
			assertEquals(specifier0.getImported(), specifier0.getAlias());
			assertFalse(specifier0.isDefault());
			
			ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
			assertIdentifier(specifier1.getImported(), "member2");
			assertIdentifier(specifier1.getAlias(), "member2");
			assertEquals(specifier1.getImported(), specifier1.getAlias());
			assertFalse(specifier1.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import { member1 , member2 as alias2 } from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(2, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
			assertIdentifier(specifier0.getImported(), "member1");
			assertIdentifier(specifier0.getAlias(), "member1");
			assertEquals(specifier0.getImported(), specifier0.getAlias());
			assertFalse(specifier0.isDefault());
			
			ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
			assertIdentifier(specifier1.getImported(), "member2");
			assertIdentifier(specifier1.getAlias(), "alias2");
			assertFalse(specifier1.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import defaultMember, { member } from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(2, impt.getSpecifiers().size());
			
			ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
			assertIdentifier(specifier0.getImported(), "defaultMember");
			assertIdentifier(specifier0.getAlias(), "defaultMember");
			assertEquals(specifier0.getImported(), specifier0.getAlias());
			assertTrue(specifier0.isDefault());
			
			ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
			assertIdentifier(specifier1.getImported(), "member");
			assertIdentifier(specifier1.getAlias(), "member");
			assertEquals(specifier1.getImported(), specifier1.getAlias());
			assertFalse(specifier1.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import defaultMember, * as name from 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(2, impt.getSpecifiers().size());
			ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
			assertIdentifier(specifier0.getImported(), "defaultMember");
			assertIdentifier(specifier0.getAlias(), "defaultMember");
			assertEquals(specifier0.getImported(), specifier0.getAlias());
			assertTrue(specifier0.isDefault());
			
			ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
			assertIdentifier(specifier1.getImported(), "*");
			assertIdentifier(specifier1.getAlias(), "name");
			assertFalse(specifier1.isDefault());
		}
		{
			ImportTree impt = (ImportTree)parseStatement("import 'module-name';");
			assertLiteral(impt.getSource(), "module-name");
			assertEquals(0, impt.getSpecifiers().size());
		}
	}
	
}
