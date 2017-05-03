package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.TestUtils.assertNumberEquals;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

import junit.framework.Assert;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;

@RunWith(Suite.class)
@SuiteClasses({ArrayLiteralTest.class, AssignmentTest.class, BinaryExpressionTest.class, ClassDeclarationTest.class, DoLoopTest.class, ForLoopTest.class, IdentifierTest.class, ImportStatementTest.class, InterfaceDeclarationTest.class, LambdaTest.class, OperatorTest.class, StatementTest.class, TypeTest.class, UnaryOperatorTest.class, VariableDeclarationTest.class })
public class JSParserTest {
	
	protected static final void assertLiteral(ExpressionTree expr, String value) {
		assertEquals(Kind.STRING_LITERAL, expr.getKind());
		assertEquals(value, ((StringLiteralTree)expr).getValue());
	}
	
	/**
	 * Assert if a number literal matches an expected value. Doubles are considered to be
	 * equivalent with a .0001 tolerance.
	 * @param value
	 * @param expr
	 */
	protected static final void assertLiteral(Number value, ExpressionTree expr) {
		assertEquals(Kind.NUMERIC_LITERAL, expr.getKind());
		Number actual = ((NumericLiteralTree)expr).getValue();
		assertNumberEquals(actual, value);
	}
	
	protected static final void assertIdentifier(String name, ExpressionTree expr) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	@SuppressWarnings("unchecked")
	protected static final <T extends Tree> T assertKind(Tree tree, Tree.Kind kind) {
		assertEquals(kind, tree.getKind());
		return (T) tree;
	}
	
	protected static final void assertSpecialType(SpecialType value, TypeTree type) {
		assertEquals(Kind.SPECIAL_TYPE, type.getKind());
		assertEquals(value, ((SpecialTypeTree) type).getType());
	}
	
	protected static void assertExceptionalExpression(String expr, String errorMsg) {
		assertExceptionalExpression(expr, errorMsg, false);
	}
	
	protected static void assertExceptionalExpression(String expr, String errorMsg, boolean strict) {
		try {
			JSLexer lexer = new JSLexer(expr);
			Context ctx = new Context();
			if (strict)
				ctx.enterStrict();
			new JSParser().parseNextExpression(lexer, ctx);
			if (lexer.isEOF())
				fail(errorMsg);
		} catch (JSSyntaxException e) {
			//Expected
		}
	}
	
	protected static void assertExceptionalStatement(String stmt, String errorMsg) {
		try {
			parseStatement(stmt);
			fail(errorMsg);
		} catch (JSSyntaxException e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends StatementTree> T parseStatement(String stmt) {
		return (T) new JSParser().parseStatement(new JSLexer(stmt), new Context());
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends StatementTree> T parseStatement(String stmt, Kind kind) {
		T result = (T) new JSParser().parseStatement(new JSLexer(stmt), new Context());
		assertEquals(kind, result.getKind());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends ExpressionTree> T parseExpressionIncomplete(String expr) {
		return (T) new JSParser().parseNextExpression(new JSLexer(expr), new Context());
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends ExpressionTree> T parseExpression(String expr) {
		JSLexer lexer = new JSLexer(expr);
		T result = (T) new JSParser().parseNextExpression(lexer, new Context());
		assertTrue("Not all of expression was consumed. Read until " + lexer.getPosition(), lexer.isEOF());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends ExpressionTree> T parseExpression(String expr, Kind kind) {
		T result = (T) new JSParser().parseNextExpression(new JSLexer(expr), new Context());
		assertEquals(kind, result.getKind());
		return result;
	}
}
