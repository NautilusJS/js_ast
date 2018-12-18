package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.TestUtils.assertNumberEquals;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.fs.SourceFile.NominalSourceFile;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;

@RunWith(Suite.class)
@SuiteClasses({ArrayLiteralTest.class, AssignmentTest.class, BinaryExpressionTest.class, ClassDeclarationTest.class, DoLoopTest.class, ForLoopTest.class, IdentifierTest.class, ImportStatementTest.class, InterfaceDeclarationTest.class, LambdaTest.class, OperatorTest.class, RegExpLiteralTest.class, StatementTest.class, TemplateLiteralTest.class, TypeTest.class, UnaryOperatorTest.class, VariableDeclarationTest.class })
public class JSParserTest {
	
	protected static final void assertLiteral(String value, ExpressionTree expr) {
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
	
	protected static final void assertIdentifier(String name, Tree expr) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	protected static final void assertIdentifier(String name, PatternTree expr) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	protected static final void assertIdentifier(String name, IdentifierTree expr) {
		assertEquals(Kind.IDENTIFIER, expr.getKind());
		assertEquals(name, ((IdentifierTree)expr).getName());
	}
	
	/**
	 * Helper for the (pretty common) special case where we have a list that should contain a single element
	 * of some kind.
	 */
	protected static final <T extends Tree> T assertSingleElementKind(Tree.Kind kind, List<? extends Tree> nodes) {
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		return assertKind(kind, nodes.get(0));
	}
	
	@SuppressWarnings("unchecked")
	protected static final <T extends Tree> T assertKind(Tree.Kind kind, Tree tree) {
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
			JSLexer lexer = createLexer(expr);
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
	public static <T extends StatementTree> T parseStatement(String stmt) {
		return (T) new JSParser().parseStatement(createLexer(stmt), new Context());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends StatementTree> T parseStatement(String stmt, Kind kind) {
		T result = (T) new JSParser().parseStatement(createLexer(stmt), new Context());
		assertEquals(kind, result.getKind());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends ExpressionTree> T parseExpressionIncomplete(String expr) {
		return (T) new JSParser().parseNextExpression(createLexer(expr), new Context());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ExpressionTree> T parseExpression(String expr) {
		JSLexer lexer = createLexer(expr);
		T result = (T) new JSParser().parseNextExpression(lexer, new Context());
		assertTrue("Not all of expression was consumed. Read until " + lexer.getPosition(), lexer.isEOF());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ExpressionTree> T parseExpression(String expr, Kind kind) {
		T result = (T) new JSParser().parseNextExpression(createLexer(expr), new Context());
		assertEquals(kind, result.getKind());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ExpressionTree> T parseExpressionWith(String expr, boolean in, boolean yield, boolean await, Kind kind) {
		Context context = new Context();
		if (yield)
			context.pushGenerator();
		if (in)
			context.allowIn();
		else
			context.disallowIn();
		context.allowAwait(await);
		T result = (T) new JSParser().parseNextExpression(createLexer(expr), context);
		assertEquals(kind, result.getKind());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TypeTree> T parseType(String type) {
		return (T) new JSParser().parseType(createLexer(type), new Context());
	}
	
	public static String getTestName() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (int i = 3; i < stack.length; i++)
			if (stack[i].getMethodName().startsWith("test"))
				return stack[i].getMethodName();
		return "test???";
	}
	
	static JSLexer createLexer(String code) {
		return new JSLexer(new NominalSourceFile(getTestName(), code));
	}
}
