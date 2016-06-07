package com.mindlin.jsast.impl.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.CaseTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.SwitchTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WithTreeImpl;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.VoidTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;

public class JSParser {
	private static Token ensureToken(JSLexer src, Object value) {
		Token token = src.nextToken();
		ensureToken(token, value);
		return token;
	}
	
	private static void ensureTokenKind(Token token, TokenKind value) {
		if (token.getKind() != value)
			throw new JSSyntaxException("Illegal token " + token + ": expected kind " + value, token.getStart());
	}
	
	private static void ensureToken(Token token, Object value) {
		if (token.getValue() != value)
			throw new JSSyntaxException("Illegal token " + token + ": expected " + value, token.getStart());
	}
	
	private static Token expect(Token t, TokenKind kind, Object value, JSLexer lexer, boolean isStrict) {
		if (t == null)
			t = lexer.nextToken();
		ensureTokenKind(t, kind);
		ensureToken(t, value);
		return t;
	}
	
	public CompilationUnitTree apply(String unitName, String src) {
		return apply(unitName, new JSLexer(src));
	}
	
	public CompilationUnitTree apply(String unitName, JSLexer src) {
		LinkedList<Tree> elements = new LinkedList<>();
		Tree value;
		while ((value = parseNext(src, false)) != null)
			elements.add(value);
		// TODO fix isStrict
		return new CompilationUnitTreeImpl(0, src.getCharIndex(), unitName, null, elements, false);
	}
	
	protected ExpressionTree parseNextExpression(JSLexer src, boolean isStrict) {
		throw new UnsupportedOperationException();
	}
	
	public Tree parseNext(JSLexer src, boolean isStrict) {
		Token t = src.nextToken();
		switch (t.getKind()) {
			case KEYWORD:
				switch ((JSKeyword) t.getValue()) {
					case WHILE:
						return this.parseWhileLoop(t, src, isStrict);
					case DO:
						return this.parseDoWhileLoop(t, src, isStrict);
					case FOR:
						return this.parseUnknownForLoop(t, src, isStrict);
					case IF:
						return this.parseIfStatement(t, src, isStrict);
					case SWITCH:
						return this.parseSwitchStatement(t, src, isStrict);
					case TRY:
						return this.parseTryStatement(t, src, isStrict);
					case FUNCTION:
						return this.parseFunctionKeyword(t, src, isStrict);
					case WITH:
						return this.parseWithStatement(t, src, isStrict);
					case CONST:
					case VAR:
					case LET:
						return this.parseVariableDeclaration(t, src, isStrict);
					case CLASS:
						return this.parseClassUnknown(t, src, isStrict);
					case TYPEOF:
						return this.parseUnaryExpression(t, src, isStrict);
					case VOID:
						return this.parseVoid(t, src, isStrict);
					case CASE:
					case BREAK:
					case CONTINUE:
					case CATCH:
					case FINALLY:
					case DEFAULT:
					case ELSE:
					case EXTENDS:
					case IN:
					case INSTANCEOF:
					case RETURN:
					case SUPER:
					case THIS:
					case YIELD:
					default:
						throw new JSSyntaxException("Unexpected keyword " + t.getValue(), t.getStart());
				}
			case IDENTIFIER:
			
			default:
				break;
		}
		return null;
	}
	protected StatementTree parseStatement(JSLexer src, boolean isStrict) {
		return this.parseStatement(src.nextToken(), src, isStrict);
	}
	
	protected StatementTree parseStatement(Token token, JSLexer src, boolean isStrict) {
		// TODO finish
		switch (token.getKind()) {
			case SPECIAL:
				ensureToken(token, JSSpecialGroup.SEMICOLON);
				return new EmptyStatementImpl(token.getStart(), token.getEnd());
			case BRACKET:
				ensureToken(token, '{');
				return this.parseBlock(token, src, isStrict);
			case KEYWORD:
				switch ((JSKeyword) token.getValue()) {
					case BREAK:
					case CONTINUE:
						return this.parseGotoStatement(token, src, isStrict);
					case DEBUGGER:
						return this.parseDebugger(token, src, isStrict);
					case RETURN:
					case DELETE:
					case THROW:
					case TYPEOF: {
						ExpressionTree expr = this.parsePrefixUnary(token, src, isStrict);
						if (expr instanceof StatementTree)
							return (StatementTree) expr;
						return new UnaryTreeImpl.VoidTreeImpl(expr);
					}
					case AWAIT:
						//TODO impl
						break;
					case VOID:
						return this.parseVoid(token, src, isStrict);
					case DO:
						return this.parseDoWhileLoop(token, src, isStrict);
					case FOR:
						return this.parseUnknownForLoop(token, src, isStrict);
					case WHILE:
						return this.parseWhileLoop(token, src, isStrict);
					case IMPORT:
						return this.parseImportStatement(token, src, isStrict);
					case EXPORT:
						return this.parseExportStatement(token, src, isStrict);
					case IF:
						return this.parseIfStatement(token, src, isStrict);
					case CLASS:
						return this.parseClassUnknown(token, src, isStrict);
					case LET:
					case VAR:
					case CONST:
						return this.parseVariableDeclaration(token, src, isStrict);
					case FUNCTION:
						return this.parseFunctionKeyword(token, src, isStrict);
					case INTERFACE:
						return this.parseInterface(token, src, isStrict);
					case NEW:
						// Insert 'void' to make this a statement
						return UnaryOperator.make(JSKeyword.VOID, this.parseNextExpression(token, src, isStrict));
					case SWITCH:
						return this.parseSwitchStatement(token, src, isStrict);
					case TRY:
						return this.parseTryStatement(token, src, isStrict);
					case WITH:
						return this.parseWithStatement(token, src, isStrict);
					case ELSE:
					case ENUM:
					case EXTENDS:
					case FINALLY:
					case CASE:
					case DEFAULT:
					case CATCH:
					case IMPLEMENTS:
					case IN:
					case INSTANCEOF:
					case OF:
					case PACKAGE:
					case PRIVATE:
					case PROTECTED:
					case PUBLIC:
					case STATIC:
					case SUPER:
					case THIS:
					case YIELD:
						break;
				}
				break;
			case IDENTIFIER:
				break;
			case LITERAL:
				break;
			case OPERATOR:
				break;
			case FUTURE:
			case FUTURESTRICT:
			case IR:
			default:
				break;
		}
		throw new UnsupportedOperationException();
	}
	
	@JSKeywordParser({JSKeyword.CONST, JSKeyword.LET, JSKeyword.VAR})
	protected StatementTree parseVariableDeclaration(Token keywordToken, JSLexer lexer, boolean isStrict) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	@JSKeywordParser(JSKeyword.CLASS)
	protected StatementTree parseClassUnknown(Token classKeywordToken, JSLexer src, boolean isStrict) {
		classKeywordToken = Token.expect(classKeywordToken, TokenKind.KEYWORD, JSKeyword.CLASS, src);
		IdentifierTree classIdentifier;
		
		Token next = src.nextToken();
		if (next.isIdentifier()) {
			classIdentifier = this.parseIdentifier(next, src, isStrict);
		}
		// TODO finish
		throw new UnsupportedOperationException();
	}
	protected IterfaceTree parseInterface(Token interfaceKeywordToken, JSLexer src, boolean isStrict) {
		
	}
	protected IdentifierTree parseIdentifier(Token identifierToken, JSLexer src, boolean isStrict) {
		identifierToken = Token.expectKind(identifierToken, TokenKind.IDENTIFIER, src);
		return new IdentifierTreeImpl(identifierToken.getStart(), identifierToken.getEnd(), identifierToken.getValue());
	}
	
	protected StatementTree parseFunctionKeyword(Token functionKeywordToken, JSLexer lexer, boolean isStrict) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected IfTree parseIfStatement(Token ifKeywordToken, JSLexer src, boolean isStrict) {
		ifKeywordToken = Token.expect(ifKeywordToken, TokenKind.KEYWORD, JSKeyword.IF, src);
		src.expectToken(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, isStrict);
		src.expectToken(JSOperator.RIGHT_PARENTHESIS);
		StatementTree thenStatement = this.parseStatement(null, src, isStrict);
		StatementTree elseStatement = null;
		Token next = src.peekNextToken();
		if (next.getKind() == TokenKind.KEYWORD && next.getValue() == JSKeyword.ELSE) {
			src.skipToken(next);
			next = src.nextToken();
			//This if statement isn't really needed, but it speeds up 'else if' statements
			//by a bit, and else if statements are actually more common than else statements
			if (next.getKind() == TokenKind.KEYWORD && next.getValue() == JSKeyword.IF)
				elseStatement = parseIfStatement(next, src, isStrict);
			else
				elseStatement = this.parseStatement(next, src, isStrict);
		}
		if (elseStatement == null)
			elseStatement = new EmptyStatementImpl(src.getPosition(), src.getPosition()); 
		return new IfTreeImpl(ifKeywordToken.getStart(), src.getPosition(), expression, thenStatement, elseStatement);
	}
	
	protected SwitchTree parseSwitchStatement(Token switchKeywordToken, JSLexer src, boolean isStrict) {
		switchKeywordToken = Token.expect(switchKeywordToken, TokenKind.KEYWORD, JSKeyword.SWITCH, src);
		src.expectToken(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, isStrict);
		src.expectToken(JSOperator.RIGHT_PARENTHESIS);
		src.expectToken(TokenKind.BRACKET, '{');
		List<CaseTree> cases = new LinkedList<>();
		Token next = src.nextToken();
		while (next.getKind() == TokenKind.KEYWORD) {
			ExpressionTree caseExpr;
			List<? extends StatementTree> statements = new LinkedList<>();
			if (next.getValue() == JSKeyword.CASE)
				caseExpr = this.parseNextExpression(src, isStrict);
			else if (next.getValue() == JSKeyword.DEFAULT)
				caseExpr = null;
			else
				throw new JSUnexpectedTokenException(next);
			src.expectToken(JSOperator.COLON);
			//TODO parse statements
			cases.add(new CaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	protected TryTree parseTryStatement(Token tryKeywordToken, JSLexer src, boolean isStrict) {
		tryKeywordToken = Token.expect(tryKeywordToken, TokenKind.KEYWORD, JSKeyword.TRY, src);
		BlockTree tryBlock = this.parseBlock(null, src, isStrict);
		
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected IfTree parseFunctionStatement(Token functionKeywordToken, JSLexer src, boolean isStrict) {
		functionKeywordToken = Token.expect(functionKeywordToken, TokenKind.KEYWORD, JSKeyword.FUNCTION, src);
		throw new UnsupportedOperationException();
	}
	
	protected WithTree parseWithStatement(Token withKeywordToken, JSLexer src, boolean isStrict) {
		withKeywordToken = Token.expect(withKeywordToken, TokenKind.KEYWORD, JSKeyword.WITH, src);
		src.expectToken(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, isStrict);
		src.expectToken(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, isStrict);
		return new WithTreeImpl(withKeywordToken.getStart(), src.getPosition(), expression, statement);
	}
	
	protected VoidTree parseVoid(Token voidKeywordToken, JSLexer src, boolean isStrict) {
		voidKeywordToken = Token.expect(voidKeywordToken, TokenKind.KEYWORD, JSKeyword.VOID, src);
		ExpressionTree expr = this.parseNextExpression(src, isStrict);
		return new UnaryTreeImpl.VoidTreeImpl(voidKeywordToken.getStart(), src.getPosition(), expr);
	}
	
	protected UnaryTree parseUnaryExpression(Token operatorToken, JSLexer src, boolean isStrict) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	// Loops
	protected WhileLoopTree parseWhileLoop(Token whileKeywordToken, JSLexer src, boolean isStrict) {
		if (whileKeywordToken == null)
			whileKeywordToken = ensureToken(src, JSKeyword.WHILE);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree condition = parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = parseStatement(src, isStrict);
		return new WhileLoopTreeImpl(whileKeywordToken.getStart(), src.getPosition(), condition, statement);
	}
	
	protected DoWhileLoopTree parseDoWhileLoop(Token doKeywordToken, JSLexer src, boolean isStrict) {
		doKeywordToken = expect(doKeywordToken, TokenKind.KEYWORD, JSKeyword.DO, src, isStrict);
		StatementTree statement = parseStatement(src, isStrict);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree condition = parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		return new DoWhileLoopTreeImpl(doKeywordToken.getStart(), src.getPosition(), statement, condition);
	}
	
	/**
	 * Parses a for loop if you know that it *is* a for loop, but not what kind
	 * (normal, in, of).
	 * 
	 * @param forKeywordToken
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected LoopTree parseUnknownForLoop(Token forKeywordToken, JSLexer src, boolean isStrict) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, isStrict);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression0 = parseNextExpression(src, isStrict);
		Token separator = src.nextToken();
		if (separator.isSpecial()) {
			ensureToken(separator, JSSpecialGroup.SEMICOLON);
			return parsePartialForLoopTree(forKeywordToken, expression0, src, isStrict);
		} else if (separator.isKeyword()
				&& (separator.getValue() == JSKeyword.IN || separator.getValue() == JSKeyword.OF)) {
			return this.parsePartialForEachLoopTree(forKeywordToken, separator.getValue() == JSKeyword.OF, expression0,
					src, isStrict);
		}
		throw new JSSyntaxException("Invalid 'for' loop", src.getPosition());
	}
	
	protected ForLoopTree parseForLoopTree(Token forKeywordToken, JSLexer src, boolean isStrict) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, isStrict);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree initializer = parseNextExpression(src, isStrict);
		
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		return parsePartialForLoopTree(forKeywordToken, initializer, src, isStrict);
	}
	
	/**
	 * Parses a (normal) for loop for which the identifier has already been
	 * parsed. This is useful, because, in order to determine the type of a for
	 * loop (normal, in, of), the first expression has to be parsed, and this
	 * method speeds up the parsing by not passing over the initializer again.
	 * Note that the semicolon after the initializer is also parsed.
	 * 
	 * @param forKeywordToken
	 * @param initializer
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected ForLoopTree parsePartialForLoopTree(Token forKeywordToken, ExpressionTree initializer, JSLexer src,
			boolean isStrict) {
		ExpressionTree condition = this.parseNextExpression(src, isStrict);
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		ExpressionTree update = this.parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, isStrict);
		return new ForLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), initializer, condition, update,
				statement);
	}
	
	/**
	 * Parses a for each loop for which the variable identifier has already been
	 * parsed. This is useful, because, in order to determine the type of a for
	 * loop (normal, in, of), the first expression has to be parsed, and this
	 * method speeds up the parsing by not passing over the initializer again.
	 * Note that the <kbd>in</kbd> or <kbd>of</kbd> after the initializer is
	 * also parsed.
	 * 
	 * @param forKeywordToken
	 * @param isForEach
	 * @param variable
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected ForEachLoopTree parsePartialForEachLoopTree(Token forKeywordToken, boolean isForEach,
			ExpressionTree variable, JSLexer src, boolean isStrict) {
		ExpressionTree expression = this.parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, isStrict);
		return new ForEachLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), isForEach, variable, expression,
				statement);
	}
	
	protected ExpressionTree parseIncompleteExpression(Token t, JSLexer src, boolean isStrict) {
		if (t == null)
			t = src.nextToken();
		ExpressionTree expr = this.parseNextExpression(t, src, isStrict);
		Token next = src.nextToken();
		switch (next.getKind()) {
			case OPERATOR: {
				
			}
			case KEYWORD:
				switch ((JSKeyword) t.getValue()) {
					case VAR:
					
				}
		}
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected ExpressionTree parseNextExpression(Token t, JSLexer src, boolean isStrict) {
		// TODO finish
		return null;
	}
	
	protected FunctionCallTree parseFunctionCall(ExpressionTree functionSelectExpression, Token openParenToken,
			JSLexer src, boolean isStrict) {
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, isStrict);
		ensureTokenKind(openParenToken, TokenKind.OPERATOR);
		ensureToken(openParenToken, JSOperator.LEFT_PARENTHESIS);
		throw new UnsupportedOperationException();
	}
	
	protected List<? extends ExpressionTree> parseParentheticalSeries(Token openParenToken, JSLexer src,
			boolean isStrict) {
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, isStrict);
		ensureToken(openParenToken, JSOperator.LEFT_PARENTHESIS);
		List<? extends ExpressionTree> result = new LinkedList<>();
		Token next = src.nextToken();
		do {
			
		} while ((next = src.nextToken()).getValue() == JSOperator.COMMA);
		ensureToken(next, JSOperator.RIGHT_PARENTHESIS);
		throw new UnsupportedOperationException();
	}
	
	protected GotoTree parseGotoStatement(Token keywordToken, JSLexer src, boolean isStrict) {
		keywordToken = Token.expectKind(keywordToken, TokenKind.KEYWORD, src);
		String label = null;
		Token next = src.nextToken();
		if (next.isIdentifier()) {
			label = next.getValue();
			next = src.nextToken();
		}
		ensureToken(src, JSSpecialGroup.EOL);
		final long start = keywordToken.getStart();
		final long end = src.getPosition();
		if (keywordToken.getValue() == JSKeyword.BREAK)
			return new AbstractGotoTree.BreakTreeImpl(start, end, label);
		else if (keywordToken.getValue() == JSKeyword.CONTINUE)
			return new AbstractGotoTree.ContinueTreeImpl(start, end, label);
		throw new JSSyntaxException("Unexpected token " + keywordToken, start);
	}
	
	protected UnaryTree parsePrefixUnary(Token keywordToken, JSLexer src, boolean isStrict) {
		if (keywordToken == null)
			keywordToken = src.nextToken();
		Tree.Kind kind = null;
		switch (keywordToken.getKind()) {
			case KEYWORD:
				switch ((JSKeyword) keywordToken.getValue()) {
					case VOID:
						{
							Token next = src.peekNextToken();
							if ((next = src.nextToken()).getKind() == TokenKind.SPECIAL && next.getValue() == JSSpecialGroup.SEMICOLON) {
								src.skipToken(next);
								return new UnaryTreeImpl(keywordToken.getStart(), src.getPosition(), null, Tree.Kind.VOID);
							}
						}
						kind = Tree.Kind.VOID;
						break;
					case RETURN:
						kind = Tree.Kind.RETURN;
						break;
					case THROW:
						kind = Tree.Kind.THROW;
						break;
					case TYPEOF:
						kind = Tree.Kind.TYPEOF;
						break;
					case DELETE:
						kind = Tree.Kind.DELETE;
						break;
					default:
						break;
				}
				break;
			case OPERATOR:
				switch ((JSOperator) keywordToken.getValue()) {
					case INCREMENT:
						kind = Tree.Kind.PREFIX_INCREMENT;
						break;
					case DECREMENT:
						kind = Tree.Kind.PREFIX_DECREMENT;
						break;
					case PLUS:
						kind = Tree.Kind.UNARY_PLUS;
						break;
					case MINUS:
						kind = Tree.Kind.UNARY_MINUS;
						break;
					case BITWISE_NOT:
						kind = Tree.Kind.BITWISE_NOT;
						break;
					case LOGICAL_NOT:
						kind = Tree.Kind.LOGICAL_NOT;
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
		if (kind == null)
			throw new JSUnexpectedTokenException(keywordToken);
		ExpressionTree expression = this.parseNextExpression(src, isStrict);
		if (kind == Tree.Kind.VOID)
			return new UnaryTreeImpl.VoidTreeImpl(expression);
		return new UnaryTreeImpl(keywordToken.getStart(), src.getPosition(), expression, kind);
	}
	
	protected DebuggerTree parseDebugger(Token debuggerKeywordToken, JSLexer src, boolean isStrict) {
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	protected BlockTree parseBlock(Token openBraceToken, JSLexer src, boolean isStrict) {
		openBraceToken = expect(openBraceToken, TokenKind.BRACKET, '{', src, isStrict);
		List<StatementTree> statements = new LinkedList<>();
		Token t;
		while ((t = src.nextToken()).getKind() != TokenKind.BRACKET)
			statements.add(parseStatement(t, src, isStrict));
		this.ensureToken(t, '}');
		return new BlockTreeImpl(openBraceToken.getStart(), src.getPosition(), statements);
	}
}