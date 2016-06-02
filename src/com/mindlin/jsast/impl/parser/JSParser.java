package com.mindlin.jsast.impl.parser;

import java.util.LinkedList;
import java.util.List;

import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.WhileLoopTree;

public class JSParser {
	public CompilationUnitTree apply(String unitName, String src) {
		return apply(unitName, new JSLexer(src));
	}

	public CompilationUnitTree apply(String unitName, JSLexer src) {
		LinkedList<Tree> elements = new LinkedList<>();
		Tree value;
		while ((value = parseNext(src, false)) != null)
			elements.add(value);
		//TODO fix isStrict
		return new CompilationUnitTreeImpl(0, src.chars.length, unitName, null, elements, false);
	}
	
	protected ExpressionTree parseNextExpression(JSLexer src, boolean isStrict) {
		throw new UnsupportedOperationException();
	}

	public Tree parseNext(JSLexer src, boolean isStrict) {
		Token t = src.nextToken();
		switch (t.getKind()) {
			case KEYWORD:
				switch((JSKeyword)t.getValue()) {
					case WHILE:
						return parseWhileLoop(t, src, isStrict);
					case DO:
						return parseDoWhileLoop(t, src, isStrict);
					case FOR:
						return parseUnknownForLoop(t, src, isStrict);
					case IF:
					case SWITCH:
					case TRY:
					case FUNCTION:
					case WITH:
					case CONST:
					case VAR:
					case LET:
					case CLASS:
						//TODO finish
						break;
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
					case TYPEOF:
					case YIELD:
					default:
						throw new JSSyntaxException("Unexpected keyword " + t.getValue(), t.getStart());
				}
			default:
				break;
		}
		return null;
	}
	private Token ensureToken(JSLexer src, Object value) {
		Token token = src.nextToken();
		ensureToken(token, value);
		return token;
	}
	private void ensureTokenKind(Token token, TokenKind value) {
		if (token.getKind() != value)
			throw new JSSyntaxException("Illegal token " + token + ": expected kind " + value, token.getStart());
	}
	private void ensureToken(Token token, Object value) {
		if (token.getValue() != value)
			throw new JSSyntaxException("Illegal token " + token + ": expected " + value, token.getStart());
	}
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
		if (doKeywordToken == null)
			doKeywordToken = ensureToken(src, JSKeyword.DO);
		StatementTree statement = parseStatement(src, isStrict);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree condition = parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		return new DoWhileLoopTreeImpl(doKeywordToken.getStart(), src.getPosition(), statement, condition);
	}
	
	/**
	 * Parses a for loop if you know that it *is* a for loop, but not what kind (normal, in, of).
	 * @param forKeywordToken
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected LoopTree parseUnknownForLoop(Token forKeywordToken, JSLexer src, boolean isStrict) {
		if (forKeywordToken == null)
			forKeywordToken = ensureToken(src, JSKeyword.FOR);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression0 = parseNextExpression(src, isStrict);
		Token separator = src.nextToken();
		if (separator.isSpecial()) {
			ensureToken(separator, JSSpecialGroup.SEMICOLON);
			return parsePartialForLoopTree(forKeywordToken, expression0, src, isStrict);
		} else if (separator.isKeyword()) {
			JSKeyword keyword = (JSKeyword) separator.getValue();
			if (keyword == JSKeyword.IN || keyword == JSKeyword.OF)
				return this.parsePartialForEachLoopTree(forKeywordToken, keyword == JSKeyword.OF, expression0, src, isStrict);
		}
		throw new JSSyntaxException("Invalid 'for' loop", src.getPosition());
	}
	
	protected ForLoopTree parseForLoopTree(Token forKeywordToken, JSLexer src, boolean isStrict) {
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
	protected ForLoopTree parsePartialForLoopTree(Token forKeywordToken, ExpressionTree initializer, JSLexer src, boolean isStrict) {
		ExpressionTree condition = this.parseNextExpression(src, isStrict);
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		ExpressionTree update = this.parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, isStrict);
		return new ForLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), initializer, condition, update, statement);
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
	protected ForEachLoopTree parsePartialForEachLoopTree(Token forKeywordToken, boolean isForEach, ExpressionTree variable, JSLexer src, boolean isStrict) {
		ExpressionTree expression = this.parseNextExpression(src, isStrict);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, isStrict);
		return new ForEachLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), isForEach, variable, expression, statement);
	}
	protected StatementTree parseStatement(JSLexer src, boolean isStrict) {
		return this.parseStatement(src.nextToken(), src, isStrict);
	}
	protected StatementTree parseStatement(Token token, JSLexer src, boolean isStrict) {
		//TODO finish
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
					case CASE:
						break;
					case CATCH:
						break;
					case CLASS:
						break;
					case CONST:
						break;
					case DEBUGGER:
						return this.parseDebugger(token, src, isStrict);
					case DEFAULT:
						break;
					case DELETE:
						break;
					case DO:
						break;
					case ELSE:
						break;
					case ENUM:
						break;
					case EXPORT:
						break;
					case EXTENDS:
						break;
					case FINALLY:
						break;
					case FOR:
						break;
					case FUNCTION:
						break;
					case IF:
						break;
					case IMPLEMENTS:
						break;
					case IMPORT:
						break;
					case IN:
						break;
					case INSTANCEOF:
						break;
					case INTERFACE:
						break;
					case LET:
						break;
					case NEW:
						break;
					case OF:
						break;
					case PACKAGE:
						break;
					case PRIVATE:
						break;
					case PROTECTED:
						break;
					case PUBLIC:
						break;
					case RETURN:
						break;
					case STATIC:
						break;
					case SUPER:
						break;
					case SWITCH:
						break;
					case THIS:
						break;
					case THROW:
						break;
					case TRY:
						break;
					case TYPEOF:
						break;
					case VAR:
						break;
					case VOID:
						break;
					case WHILE:
						break;
					case WITH:
						break;
					case YIELD:
						break;
				}
				break;
			case FUTURE:
				break;
			case FUTURESTRICT:
				break;
			case IDENTIFIER:
				break;
			case IR:
				break;
			case LITERAL:
				break;
			case OPERATOR:
				break;
			default:
				break;
			
		}
		throw new UnsupportedOperationException();
	}
	
	protected GotoTree parseGotoStatement(Token keywordToken, JSLexer src, boolean isStrict) {
		if (keywordToken == null) {
			keywordToken = src.nextToken();
			ensureTokenKind(keywordToken, TokenKind.KEYWORD);
		}
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
	
	protected DebuggerTree parseDebugger(Token debuggerKeywordToken, JSLexer src, boolean isStrict) {
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	protected BlockTree parseBlock(Token openBraceToken, JSLexer src, boolean isStrict) {
		if (openBraceToken == null)
			openBraceToken = src.nextToken();
		ensureToken(openBraceToken, '{');
		List<? extends StatementTree> statements = new LinkedList<>();
		throw new UnsupportedOperationException();
//		Token t = src.peekNextToken();
		//TODO finish
	}
}