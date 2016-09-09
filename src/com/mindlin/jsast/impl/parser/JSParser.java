package com.mindlin.jsast.impl.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.exception.JSUnsupportedException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.CaseTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionCallTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.SwitchTreeImpl;
import com.mindlin.jsast.impl.tree.TryTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.impl.tree.VariableTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WithTreeImpl;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.InterfaceTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.SpreadTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.VoidTree;
import com.mindlin.jsast.tree.VariableTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;

public class JSParser {
	private static Token ensureToken(JSLexer src, Object value) {
		Token token = src.nextToken();
		ensureToken(token, value);
		return token;
	}
	
	/**
	 * A convenience method, to assert a token's kind
	 * @param token
	 * @param value
	 */
	private static void ensureTokenKind(Token token, TokenKind value) {
		if (token.getKind() != value)
			throw new JSSyntaxException("Illegal token " + token + "; expected kind " + value, token.getStart());
	}
	
	/**
	 * A convenience method, to assert a token's value
	 * @param token
	 * @param value
	 */
	private static void ensureToken(Token token, Object value) {
		if (token.getValue() != value)
			throw new JSSyntaxException("Illegal token " + token + "; expected value " + value, token.getStart());
	}
	
	private static Token expect(Token t, TokenKind kind, Object value, JSLexer lexer, Context context) {
		if (t == null)
			t = lexer.nextToken();
		ensureTokenKind(t, kind);
		ensureToken(t, value);
		return t;
	}
	
	//Parser properties
	protected JSDialect dialect;
	
	public JSParser() {
		this(JSDialect.JSStandardDialect.TYPESCRIPT);
	}
	public JSParser(JSDialect dialect) {
		this.dialect = dialect;
	}
	
	public CompilationUnitTree apply(String unitName, String src) {
		return apply(unitName, new JSLexer(src));
	}
	
	public CompilationUnitTree apply(String unitName, JSLexer src) {
		List<Tree> elements = new ArrayList<>();
		Tree value;
		Context context = new Context();
		while ((value = parseNext(src, context)) != null)
			elements.add(value);
		return new CompilationUnitTreeImpl(0, src.getCharIndex(), unitName, null, elements, false);
	}
	
	protected ExpressionTree parseNextExpression(JSLexer src, Context context) {
		throw new UnsupportedOperationException();
	}
	
	public Tree parseNext(JSLexer src, Context context) {
		Token t = src.nextToken();
		switch (t.getKind()) {
			case KEYWORD:
				switch (t.<JSKeyword>getValue()) {
					case WHILE:
						return this.parseWhileLoop(t, src, context);
					case DO:
						return this.parseDoWhileLoop(t, src, context);
					case FOR:
						return this.parseUnknownForLoop(t, src, context);
					case IF:
						return this.parseIfStatement(t, src, context);
					case SWITCH:
						return this.parseSwitchStatement(t, src, context);
					case TRY:
						return this.parseTryStatement(t, src, context);
					case FUNCTION:
						return this.parseFunctionKeyword(t, src, context);
					case WITH:
						return this.parseWithStatement(t, src, context);
					case CONST:
					case VAR:
					case LET:
						return this.parseVariableDeclaration(t, src, context);
					case CLASS:
						return this.parseClassUnknown(t, src, context);
					case TYPEOF:
						return this.parseUnaryExpression(t, src, context);
					case VOID:
						return this.parseVoid(t, src, context);
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
				return this.parseIncompleteExpression(t, src, context);
			case BRACKET:
				return this.parseBlock(t, src, context);
			case LITERAL:
				throw new UnsupportedOperationException();
			case FUTURE:
			case FUTURESTRICT:
				//TODO remove?
				break;
			case OPERATOR:
				throw new UnsupportedOperationException();
			case SPECIAL:
				switch (t.<JSSpecialGroup>getValue()) {
					case EOF:
						break;
					case EOL:
						break;
					case SEMICOLON:
						break;
					default:
						break;
				}
				throw new UnsupportedOperationException();
		}
		throw new JSUnexpectedTokenException(t);
	}
	
	protected StatementTree parseStatement(JSLexer src, Context context) {
		return this.parseStatement(src.nextToken(), src, context);
	}
	
	protected StatementTree parseStatement(Token token, JSLexer src, Context context) {
		// TODO finish
		switch (token.getKind()) {
			case SPECIAL:
				ensureToken(token, JSSpecialGroup.SEMICOLON);
				return new EmptyStatementTreeImpl(token);
			case BRACKET:
				ensureToken(token, '{');
				return this.parseBlock(token, src, context);
			case KEYWORD:
				switch (token.<JSKeyword>getValue()) {
					case BREAK:
					case CONTINUE:
						return this.parseGotoStatement(token, src, context);
					case DEBUGGER:
						return this.parseDebugger(token, src, context);
					case RETURN:
					case DELETE:
					case THROW:
					case NEW:
					case TYPEOF: {
						ExpressionTree expr = this.parsePrefixUnary(token, src, context);
						if (expr instanceof StatementTree)
							return (StatementTree) expr;
						return new ExpressionStatementTreeImpl(expr);
					}
					case AWAIT:
						// TODO impl
						break;
					case VOID:
						return this.parseVoid(token, src, context);
					case DO:
						return this.parseDoWhileLoop(token, src, context);
					case FOR:
						return this.parseUnknownForLoop(token, src, context);
					case WHILE:
						return this.parseWhileLoop(token, src, context);
					case IMPORT:
						return this.parseImportStatement(token, src, context);
					case EXPORT:
						return this.parseExportStatement(token, src, context);
					case IF:
						return this.parseIfStatement(token, src, context);
					case CLASS:
						return this.parseClassUnknown(token, src, context);
					case LET:
					case VAR:
					case CONST:
						return this.parseVariableDeclaration(token, src, context);
					case FUNCTION:
						return this.parseFunctionKeyword(token, src, context);
					case FUNCTION_GENERATOR:
						return this.parseGeneratorKeyword(token, src, context);
					case INTERFACE:
						return this.parseInterface(token, src, context);
					case SWITCH:
						return this.parseSwitchStatement(token, src, context);
					case TRY:
						return this.parseTryStatement(token, src, context);
					case WITH:
						return this.parseWithStatement(token, src, context);
					//These are all invalid
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
					default:
						throw new JSUnexpectedTokenException(token);
				}
			case IDENTIFIER:
				break;
			case LITERAL:
				break;
			case OPERATOR:
				throw new JSUnexpectedTokenException(token);
			case FUTURE:
			case FUTURESTRICT:
			default:
				throw new UnsupportedOperationException("Unknown kind: " + token.getKind());
		}
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 * @param commaToken
	 * @param src
	 * @param ctx
	 */
	protected void expectCommaSeparator(JSLexer src, Context ctx) {
		if (dialect.supports("extension.tolerance")) {
			Token commaToken = src.peekNextToken();
			if (commaToken.matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				src.skipToken(commaToken);	
			} else if (commaToken.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)) {
				src.skipToken(commaToken);
				//TODO tolerate
				throw new JSUnexpectedTokenException(commaToken);
			} else {
				throw new JSUnexpectedTokenException(commaToken);
			}
		} else {
			expect(null, TokenKind.OPERATOR, JSOperator.COMMA, src, ctx);
		}
	}
	
	protected ExpressionTree parsePrimaryExpression(Token t, JSLexer src, Context ctx) {
		if (t == null)
			t = src.nextToken();
		switch (t.getKind()) {
			case IDENTIFIER:
				return new IdentifierTreeImpl(t);
			case NUMERIC_LITERAL:
				if (ctx.isStrict()) {
					//TODO throw error on implicit octal
				}
				return new NumericLiteralTreeImpl(t);
			case STRING_LITERAL:
			case TEMPLATE_LITERAL:
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
				//TODO finish
				throw new UnsupportedOperationException();
			case OPERATOR:
				switch (t.<JSOperator>getValue()) {
					case ADDITION_ASSIGNMENT:
						break;
					case ASSIGNMENT:
						break;
					case BITWISE_AND:
						break;
					case BITWISE_AND_ASSIGNMENT:
						break;
					case BITWISE_NOT:
						break;
					case BITWISE_OR:
						break;
					case BITWISE_OR_ASSIGNMENT:
						break;
					case BITWISE_XOR:
						break;
					case BITWISE_XOR_ASSIGNMENT:
						break;
					case COLON:
						break;
					case COMMA:
						break;
					case DECREMENT:
						break;
					case DIVISION:
						break;
					case DIVISION_ASSIGNMENT:
						break;
					case EQUAL:
						break;
					case EXPONENTIATION:
						break;
					case EXPONENTIATION_ASSIGNMENT:
						break;
					case GREATER_THAN:
						break;
					case GREATER_THAN_EQUAL:
						break;
					case INCREMENT:
						break;
					case LAMBDA:
						break;
					case LEFT_PARENTHESIS:
						break;
					case LEFT_SHIFT:
						break;
					case LEFT_SHIFT_ASSIGNMENT:
						break;
					case LESS_THAN:
						break;
					case LESS_THAN_EQUAL:
						break;
					case LOGICAL_AND:
						break;
					case LOGICAL_NOT:
						break;
					case LOGICAL_OR:
						break;
					case MINUS:
						break;
					case MULTIPLICATION:
						break;
					case MULTIPLICATION_ASSIGNMENT:
						break;
					case NOT_EQUAL:
						break;
					case PERIOD:
						break;
					case PLUS:
						break;
					case QUESTION_MARK:
						break;
					case REMAINDER:
						break;
					case REMAINDER_ASSIGNMENT:
						break;
					case RIGHT_PARENTHESIS:
						break;
					case RIGHT_SHIFT:
						break;
					case RIGHT_SHIFT_ASSIGNMENT:
						break;
					case SPREAD:
						break;
					case STRICT_EQUAL:
						break;
					case STRICT_NOT_EQUAL:
						break;
					case SUBTRACTION_ASSIGNMENT:
						break;
					case UNSIGNED_RIGHT_SHIFT:
						break;
					case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
						break;
					default:
						break;
				}
			case BRACKET:
				break;
			case KEYWORD:
				break;
			case SPECIAL:
				break;
			default:
				break;
		}
		throw new UnsupportedOperationException();
	}
	
	protected Tree parseGroupExpression(Token leftParenToken, JSLexer lexer, Context context) {
		leftParenToken = expect(leftParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, lexer, context);
		Token next = lexer.nextToken();
		if (next.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			//Is lambda w/ no args ("()=>???")
			dialect.require("js.function.lambda", leftParenToken.getStart());
			lexer.expectToken(TokenKind.OPERATOR, JSOperator.LAMBDA);
			//TODO finish
			return null;
		} else if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			dialect.require("js.function.lambda", leftParenToken.getStart());
			ExpressionTree expr = parseSpread(next, lexer, context);
			lexer.expectToken(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS);
			lexer.expectToken(TokenKind.OPERATOR, JSOperator.LAMBDA);
			//TODO finish
			return null;
		} else {
			boolean arrow = false;
			//TODO finish
		}
		return null;
	}
	
	protected List<ParameterTree> parseArguments(Token t, JSLexer lexer, Context ctx) {
		 t = expect(t, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, lexer, ctx);
		 List<ParameterTree> result = new ArrayList<>();
		 while (!(t = lexer.nextToken()).matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			 
		 }
		 throw new UnsupportedOperationException();
	}
	
	protected ExpressionTree parseUnaryExpression(Token operator, JSLexer lexer, Context ctx) {
		if (operator == null)
			operator = lexer.nextToken();
		Tree.Kind kind = null;
		if (operator.getKind() == TokenKind.OPERATOR) {
			switch (operator.<JSOperator>getValue()) {
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
					kind = null;
			}
		} else if (operator.getKind() == TokenKind.KEYWORD) {
			switch(operator.<JSKeyword>getValue()) {
				case DELETE:
					kind = Tree.Kind.DELETE;
					break;
				case VOID:
					kind = Tree.Kind.VOID;
					break;
				case TYPEOF:
					kind = Tree.Kind.TYPEOF;
					break;
				default:
					kind = null;
			}
		} else {
			throw new JSUnexpectedTokenException(operator);
		}
		if (kind == null)
			throw new JSUnexpectedTokenException(operator);

		ExpressionTree expr = null;//TODO fin
		return new UnaryTreeImpl(operator.getStart(), expr.getEnd(), expr, kind);
	}
	
	protected ImportTree parseImportStatement(Token importKeywordToken, JSLexer lexer, Context context) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected ExportTree parseExportStatement(Token exportKeywordToken, JSLexer lexer, Context context) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	protected TypeTree parseTypeStatement(Token typeToken, JSLexer lexer, Context context) {
		if (typeToken == null)
			typeToken = lexer.nextToken();
		throw new UnsupportedOperationException();
	}
	
	@JSKeywordParser({ JSKeyword.CONST, JSKeyword.LET, JSKeyword.VAR })
	protected StatementTree parseVariableDeclaration(Token keywordToken, JSLexer lexer, Context context) {
		keywordToken = Token.expectKind(keywordToken, TokenKind.KEYWORD, lexer);
		boolean isConst = keywordToken.getValue() == JSKeyword.CONST;
		boolean isScoped = keywordToken.getValue() == JSKeyword.LET;
		//Check that the token is 'var', 'let', or 'const'.
		if (keywordToken.getValue() != JSKeyword.VAR && !(isConst || isScoped))
			throw new JSUnexpectedTokenException(keywordToken);
		//Check if allowed
		if (isScoped && !dialect.supports("js.variable.scoped"))
			throw new JSUnsupportedException("js.variable.scoped", keywordToken.getStart());
		if (isConst && !dialect.supports("js.variable.const"))
			throw new JSUnsupportedException("js.variable.const", keywordToken.getStart());
		
		//Build list of declarations
		List<VariableTree> declarations = new ArrayList<>();
		//Parse identifier(s)
		do {
			Token identifier = lexer.nextToken();
			TypeTree type = null;
			ExpressionTree initializer = null;
			if (!identifier.isIdentifier())
				throw new JSUnexpectedTokenException(identifier);
			Token peek = lexer.peekNextToken();
			//Check if a type is available
			if (peek.matches(TokenKind.OPERATOR, JSOperator.COLON)) {
				lexer.skipToken(peek);
				type = parseTypeStatement(lexer.nextToken(), lexer, context);
				
				peek = lexer.peekNextToken();
			}
			//Check if an initializer is available
			if (peek.matches(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) {
				lexer.skipToken(peek);
				initializer = parseExpression(null, lexer, context);
			}
			declarations.add(new VariableTreeImpl(declarations.isEmpty() ? keywordToken.getStart() : identifier.getStart(), lexer.getPosition(), isScoped, isConst, new IdentifierTreeImpl(identifier), type, initializer));
		} while (lexer.nextToken().matches(TokenKind.OPERATOR, JSOperator.COMMA));
		
		if (declarations.size() == 1)
			return declarations.get(0);
		//Return as an unscoped block
		return new BlockTreeImpl(keywordToken.getStart(), lexer.getPosition(), declarations, false);
	}
	
	@JSKeywordParser(JSKeyword.CLASS)
	protected StatementTree parseClassUnknown(Token classKeywordToken, JSLexer src, Context context) {
		classKeywordToken = expect(classKeywordToken, TokenKind.KEYWORD, JSKeyword.CLASS, src, context);
		IdentifierTree classIdentifier = null;
		
		Token next = src.nextToken();
		if (next.isIdentifier())
			classIdentifier = this.parseIdentifier(next, src, context);
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected InterfaceTree parseInterface(Token interfaceKeywordToken, JSLexer src, Context context) {
		interfaceKeywordToken = expect(interfaceKeywordToken, TokenKind.KEYWORD, JSKeyword.INTERFACE, src, context);
		
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected IdentifierTree parseIdentifier(Token identifierToken, JSLexer src, Context context) {
		identifierToken = Token.expectKind(identifierToken, TokenKind.IDENTIFIER, src);
		return new IdentifierTreeImpl(identifierToken.getStart(), identifierToken.getEnd(), identifierToken.getValue());
	}
	
	protected StatementTree parseFunctionKeyword(Token functionKeywordToken, JSLexer lexer, Context context) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected StatementTree parseGeneratorKeyword(Token generatorKeywordToken, JSLexer src, Context context) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected IfTree parseIfStatement(Token ifKeywordToken, JSLexer src, Context context) {
		ifKeywordToken = Token.expect(ifKeywordToken, TokenKind.KEYWORD, JSKeyword.IF, src);
		src.expectToken(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expectToken(JSOperator.RIGHT_PARENTHESIS);
		StatementTree thenStatement = this.parseStatement(null, src, context);
		StatementTree elseStatement = null;
		Token next = src.peekNextToken();
		if (next.matches(TokenKind.KEYWORD, JSKeyword.ELSE)) {
			src.skipToken(next);
			next = src.nextToken();
			// This if statement isn't really needed, but it speeds up 'else if'
			// statements
			// by a bit, and else if statements are actually more common than
			// else statements
			if (next.getKind() == TokenKind.KEYWORD && next.getValue() == JSKeyword.IF)
				elseStatement = parseIfStatement(next, src, context);
			else
				elseStatement = this.parseStatement(next, src, context);
		}
		if (elseStatement == null)
			elseStatement = new EmptyStatementTreeImpl(src.getPosition(), src.getPosition());
		return new IfTreeImpl(ifKeywordToken.getStart(), src.getPosition(), expression, thenStatement, elseStatement);
	}
	
	protected SwitchTree parseSwitchStatement(Token switchKeywordToken, JSLexer src, Context context) {
		switchKeywordToken = Token.expect(switchKeywordToken, TokenKind.KEYWORD, JSKeyword.SWITCH, src);
		src.expectToken(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expectToken(JSOperator.RIGHT_PARENTHESIS);
		src.expectToken(TokenKind.BRACKET, '{');
		List<CaseTree> cases = new LinkedList<>();
		Token next = src.nextToken();
		while (next.getKind() == TokenKind.KEYWORD) {
			ExpressionTree caseExpr;
			List<? extends StatementTree> statements = new LinkedList<>();
			if (next.getValue() == JSKeyword.CASE)
				caseExpr = this.parseNextExpression(src, context);
			else if (next.getValue() == JSKeyword.DEFAULT)
				caseExpr = null;
			else
				throw new JSUnexpectedTokenException(next);
			src.expectToken(JSOperator.COLON);
			// TODO parse statements
			cases.add(new CaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	protected TryTree parseTryStatement(Token tryKeywordToken, JSLexer src, Context context) {
		tryKeywordToken = Token.expect(tryKeywordToken, TokenKind.KEYWORD, JSKeyword.TRY, src);
		//TODO support try-with-resources?
		BlockTree tryBlock = this.parseBlock(null, src, context);
		ArrayList<CatchTree> catchBlocks = new ArrayList<>();
		BlockTree finallyBlock = null;
		
		Token next = src.peekNextToken();
		while (next.getKind() != TokenKind.SPECIAL && next.getValue() != JSSpecialGroup.EOF) {
			
		}
		if (finallyBlock == null && catchBlocks.isEmpty())
			throw new JSSyntaxException("Incomplete try statement", src.getPosition());
		// TODO finish
		return new TryTreeImpl(tryKeywordToken.getStart(), src.getPosition(), tryBlock, catchBlocks, finallyBlock);
	}
	
	protected IfTree parseFunctionStatement(Token functionKeywordToken, JSLexer src, Context context) {
		functionKeywordToken = Token.expect(functionKeywordToken, TokenKind.KEYWORD, JSKeyword.FUNCTION, src);
		throw new UnsupportedOperationException("Type support is (currently) not supported");
	}
	
	protected WithTree parseWithStatement(Token withKeywordToken, JSLexer src, Context context) {
		withKeywordToken = Token.expect(withKeywordToken, TokenKind.KEYWORD, JSKeyword.WITH, src);
		src.expectToken(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expectToken(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		return new WithTreeImpl(withKeywordToken.getStart(), src.getPosition(), expression, statement);
	}
	
	protected SpreadTree parseSpread(Token spreadToken, JSLexer src, Context context) {
		spreadToken = expect(spreadToken, TokenKind.OPERATOR, JSOperator.SPREAD, src, context);
		
		throw new UnsupportedOperationException();
	}
	
	protected VoidTree parseVoid(Token voidKeywordToken, JSLexer src, Context context) {
		voidKeywordToken = Token.expect(voidKeywordToken, TokenKind.KEYWORD, JSKeyword.VOID, src);
		ExpressionTree expr = this.parseNextExpression(src, context);
		return new UnaryTreeImpl.VoidTreeImpl(voidKeywordToken.getStart(), src.getPosition(), expr);
	}
	
	// Loops
	protected WhileLoopTree parseWhileLoop(Token whileKeywordToken, JSLexer src, Context context) {
		if (whileKeywordToken == null)
			whileKeywordToken = ensureToken(src, JSKeyword.WHILE);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree condition = parseNextExpression(src, context);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = parseStatement(src, context);
		return new WhileLoopTreeImpl(whileKeywordToken.getStart(), src.getPosition(), condition, statement);
	}
	
	protected DoWhileLoopTree parseDoWhileLoop(Token doKeywordToken, JSLexer src, Context context) {
		doKeywordToken = expect(doKeywordToken, TokenKind.KEYWORD, JSKeyword.DO, src, context);
		StatementTree statement = parseStatement(src, context);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree condition = parseNextExpression(src, context);
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
	protected LoopTree parseUnknownForLoop(Token forKeywordToken, JSLexer src, Context context) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, context);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		//TODO fix
		StatementTree statement0 = this.parseStatement(src, context);
		Token separator = src.nextToken();
		if (separator.isSpecial()) {
			ensureToken(separator, JSSpecialGroup.SEMICOLON);
			return parsePartialForLoopTree(forKeywordToken, statement0, src, context);
		} else if (separator.isKeyword()
				&& (separator.getValue() == JSKeyword.IN || separator.getValue() == JSKeyword.OF)) {
			return this.parsePartialForEachLoopTree(forKeywordToken, separator.getValue() == JSKeyword.OF, statement0,
					src, context);
		}
		throw new JSSyntaxException("Invalid 'for' loop", src.getPosition());
	}
	
	protected ForLoopTree parseForLoopTree(Token forKeywordToken, JSLexer src, Context context) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, context);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree initializer = parseNextExpression(src, context);
		
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		return parsePartialForLoopTree(forKeywordToken, initializer, src, context);
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
		ExpressionTree condition = this.parseNextExpression(src, context);
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		ExpressionTree update = this.parseNextExpression(src, context);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
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
			ExpressionTree variable, JSLexer src, Context context) {
		ExpressionTree expression = this.parseNextExpression(src, context);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		return new ForEachLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), isForEach, variable, expression,
				statement);
	}
	
	/**
	 * Parses an expression where it is known that a binary operator 
	 * @param t
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected ExpressionTree parseIncompleteExpression(Token t, JSLexer src, Context context) {
		if (t == null)
			t = src.nextToken();
		ExpressionTree leftExpr = this.parseNextExpression(t, src, context);
		Token next = src.nextToken();
		switch (next.getKind()) {
			case OPERATOR: {
				JSOperator operator = next.<JSOperator>getValue();
				if (operator.arity() == 2) {
					ExpressionTree rightExpr = this.parseNextExpression(src.nextToken(), src, context);
					Tree.Kind exprKind = null;
					switch (next.<JSOperator>getValue()) {
						case ASSIGNMENT:
							exprKind = Tree.Kind.ASSIGNMENT;
							break;
						case ADDITION_ASSIGNMENT:
							exprKind = Tree.Kind.ADDITION_ASSIGNMENT;
							break;
						case BITWISE_AND_ASSIGNMENT:
							exprKind = Tree.Kind.BITWISE_AND_ASSIGNMENT;
							break;
						case BITWISE_OR_ASSIGNMENT:
							exprKind = Tree.Kind.BITWISE_OR_ASSIGNMENT;
							break;
						case BITWISE_XOR_ASSIGNMENT:
							exprKind = Tree.Kind.BITWISE_XOR_ASSIGNMENT;
							break;
						case DIVISION_ASSIGNMENT:
							exprKind = Tree.Kind.DIVISION_ASSIGNMENT;
							break;
						case EXPONENTIATION_ASSIGNMENT:
							exprKind = Tree.Kind.EXPONENTIATION_ASSIGNMENT;
							break;
						case BITWISE_AND:
							exprKind = Tree.Kind.BITWISE_AND;
							break;
						case BITWISE_OR:
							exprKind = Tree.Kind.BITWISE_OR;
							break;
						case BITWISE_XOR:
							exprKind = Tree.Kind.BITWISE_XOR;
							break;
						case COMMA:
							exprKind = Tree.Kind.COMMA;
							break;
						case DIVISION:
							exprKind = Tree.Kind.DIVISION;
							break;
						case EQUAL:
							exprKind = Tree.Kind.EQUAL;
							break;
						case EXPONENTIATION:
							exprKind = Tree.Kind.EXPONENTIATION;
							break;
						case GREATER_THAN:
							exprKind = Tree.Kind.GREATER_THAN;
							break;
						case GREATER_THAN_EQUAL:
							exprKind = Tree.Kind.GREATER_THAN_EQUAL;
							break;
						case LAMBDA:
							//TODO special (transform comma in parens to arg list)
							break;
						case LEFT_SHIFT:
							exprKind = Tree.Kind.LEFT_SHIFT;
							break;
						case LEFT_SHIFT_ASSIGNMENT:
							exprKind = Tree.Kind.LEFT_SHIFT_ASSIGNMENT;
							break;
						case LESS_THAN:
							exprKind = Tree.Kind.LESS_THAN;
							break;
						case LESS_THAN_EQUAL:
							exprKind = Tree.Kind.LESS_THAN_EQUAL;
							break;
						case LOGICAL_AND:
							exprKind = Tree.Kind.LOGICAL_AND;
							break;
						case LOGICAL_OR:
							exprKind = Tree.Kind.LOGICAL_OR;
							break;
						case MINUS:
							exprKind = Tree.Kind.SUBTRACTION;
							break;
						case MULTIPLICATION:
							exprKind = Tree.Kind.MULTIPLICATION;
							break;
						case MULTIPLICATION_ASSIGNMENT:
							exprKind = Tree.Kind.MULTIPLICATION_ASSIGNMENT;
							break;
						case NOT_EQUAL:
							exprKind = Tree.Kind.NOT_EQUAL;
							break;
						case PLUS:
							exprKind = Tree.Kind.ADDITION;
							break;
						case REMAINDER:
							exprKind = Tree.Kind.REMAINDER;
							break;
						case REMAINDER_ASSIGNMENT:
							exprKind = Tree.Kind.REMAINDER_ASSIGNMENT;
							break;
						case RIGHT_SHIFT:
							exprKind = Tree.Kind.RIGHT_SHIFT;
							break;
						case RIGHT_SHIFT_ASSIGNMENT:
							exprKind = Tree.Kind.RIGHT_SHIFT_ASSIGNMENT;
							break;
						case STRICT_EQUAL:
							exprKind = Tree.Kind.STRICT_EQUAL;
							break;
						case STRICT_NOT_EQUAL:
							exprKind = Tree.Kind.STRICT_NOT_EQUAL;
							break;
						case SUBTRACTION_ASSIGNMENT:
							exprKind = Tree.Kind.SUBTRACTION_ASSIGNMENT;
							break;
						case UNSIGNED_RIGHT_SHIFT:
							exprKind = Tree.Kind.UNSIGNED_RIGHT_SHIFT;
							break;
						case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
							exprKind = Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
							break;
						default:
							throw new IllegalStateException("Unsupported operator: " + operator);
					}
					if (operator.isAssignment()) {
						return null;
					} else {
						return new BinaryTreeImpl(leftExpr.getStart(), rightExpr.getEnd(), exprKind, leftExpr, rightExpr);
					}
				}
				switch (next.<JSOperator>getValue()) {
					case ASSIGNMENT:
						break;
					case ADDITION_ASSIGNMENT:
						break;
					case BITWISE_AND_ASSIGNMENT:
						break;
					case BITWISE_OR_ASSIGNMENT:
						break;
					case BITWISE_XOR_ASSIGNMENT:
						break;
					case DIVISION_ASSIGNMENT:
						break;
					case EXPONENTIATION_ASSIGNMENT:
						break;
					case LEFT_SHIFT_ASSIGNMENT:
						break;
					case MULTIPLICATION_ASSIGNMENT:
						break;
					case REMAINDER_ASSIGNMENT:
						break;
					case RIGHT_SHIFT_ASSIGNMENT:
						break;
					case SUBTRACTION_ASSIGNMENT:
						break;
					case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
						break;
					case BITWISE_AND:
						break;
					case BITWISE_NOT:
						break;
					case BITWISE_OR:
						break;
					case BITWISE_XOR:
						break;
					case COLON:
						break;
					case COMMA:
						break;
					case DECREMENT:
						break;
					case DIVISION:
						break;
					case EQUAL:
						break;
					case EXPONENTIATION:
						break;
					case GREATER_THAN:
						break;
					case GREATER_THAN_EQUAL:
						break;
					case INCREMENT:
						break;
					case LAMBDA:
						break;
					case LEFT_PARENTHESIS:
						break;
					case LEFT_SHIFT:
						break;
					case LESS_THAN:
						break;
					case LESS_THAN_EQUAL:
						break;
					case LOGICAL_AND:
						break;
					case LOGICAL_NOT:
						break;
					case LOGICAL_OR:
						break;
					case MINUS:
						break;
					case MULTIPLICATION:
						break;
					case NOT_EQUAL:
						break;
					case PLUS:
						break;
					case QUESTION_MARK:
						break;
					case REMAINDER:
						break;
					case RIGHT_PARENTHESIS:
						break;
					case RIGHT_SHIFT:
						break;
					case STRICT_EQUAL:
						break;
					case STRICT_NOT_EQUAL:
						break;
					case UNSIGNED_RIGHT_SHIFT:
						break;
					default:
						break;
					
				}
			}
			case KEYWORD:
				switch ((JSKeyword) t.getValue()) {
					case VAR:
					
				}
		}
		// TODO finish
		throw new UnsupportedOperationException();
	}
	/**
	 * Recalculate the order of operations for an expression. Kinda recursive, should fix the
	 * trees to match
	 * developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence
	 * @param expression
	 * @return correct expression
	 */
	protected ExpressionTree recalculateO3(ExpressionTree expression) {
		Tree.Kind type = expression.getKind();
		switch (type) {
			case REGEXP_LITERAL:
			case STRING_INTERPOLATED_LITERAL:
			case STRING_LITERAL:
			case NULL_LITERAL:
			case NUMBER_LITERAL:
			case OBJECT_LITERAL:
			case ARRAY_LITERAL:
			case BOOLEAN_LITERAL:
			case IDENTIFIER:
				//These can't be broken down at all
				return expression;
			case PARENTHESIZED:
				//Precedence 19
				return new ParenthesizedTreeImpl(expression.getStart(), expression.getEnd(), recalculateO3(((ParenthesizedTree)expression).getExpression()));
			//TODO finish
			case ADDITION:
				break;
			case ADDITION_ASSIGNMENT:
				break;
			case ARRAY_ACCESS:
				break;
			case ASSIGNMENT:
				break;
			case BITWISE_AND:
				break;
			case BITWISE_AND_ASSIGNMENT:
				break;
			case BITWISE_NOT:
				break;
			case BITWISE_OR:
				break;
			case BITWISE_OR_ASSIGNMENT:
				break;
			case BITWISE_XOR:
				break;
			case BITWISE_XOR_ASSIGNMENT:
				break;
			case BLOCK:
				break;
			case CLASS_DECLARATION:
				break;
			case CLASS_EXPRESSION:
				break;
			case COMMA:
				break;
			case DELETE:
				break;
			case DIVISION:
				break;
			case DIVISION_ASSIGNMENT:
				break;
			case EQUAL:
				break;
			case EXPONENTIATION:
				break;
			case EXPONENTIATION_ASSIGNMENT:
				break;
			case FUNCTION:
				break;
			case FUNCTION_EXPRESSION:
				break;
			case FUNCTION_INVOCATION:
				break;
			case GREATER_THAN:
				break;
			case GREATER_THAN_EQUAL:
				break;
			case IN:
				break;
			case INSTANCE_OF:
				break;
			case INTERFACE_DECLARATION:
				break;
			case LABELED_STATEMENT:
				break;
			case LEFT_SHIFT:
				break;
			case LEFT_SHIFT_ASSIGNMENT:
				break;
			case LESS_THAN:
				break;
			case LESS_THAN_EQUAL:
				break;
			case LOGICAL_AND:
				break;
			case LOGICAL_NOT:
				break;
			case LOGICAL_OR:
				break;
			case MEMBER_SELECT:
				break;
			case MULTIPLICATION:
				break;
			case MULTIPLICATION_ASSIGNMENT:
				break;
			case NEW:
				break;
			case NOT_EQUAL:
				break;
			case POSTFIX_DECREMENT:
				break;
			case POSTFIX_INCREMENT:
				break;
			case PREFIX_DECREMENT:
				break;
			case PREFIX_INCREMENT:
				break;
			case PROPERTY:
				break;
			case REMAINDER:
				break;
			case REMAINDER_ASSIGNMENT:
				break;
			case RIGHT_SHIFT:
				break;
			case RIGHT_SHIFT_ASSIGNMENT:
				break;
			case SCOPED_FUNCTION:
				break;
			case SPREAD:
				break;
			case STRICT_EQUAL:
				break;
			case STRICT_NOT_EQUAL:
				break;
			case SUBTRACTION:
				break;
			case SUBTRACTION_ASSIGNMENT:
				break;
			case TYPEOF:
				break;
			case UNARY_MINUS:
				break;
			case UNARY_PLUS:
				break;
			case UNSIGNED_RIGHT_SHIFT:
				break;
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
				break;
			case VOID:
				break;
			default:
				break;
		}
		throw new JSSyntaxException("Unexpected branch: " + expression, expression.getStart());
	}
	protected ExpressionTree parseUnaryPostfix(ExpressionTree expr, Token t, JSLexer src, Context context) {
		t = Token.expectKind(t, TokenKind.OPERATOR, src);
		switch (t.<JSOperator>getValue()) {
			case INCREMENT:
				return new UnaryTreeImpl(expr.getStart(), t.getEnd(), expr, Tree.Kind.POSTFIX_INCREMENT);
			case DECREMENT:
				return new UnaryTreeImpl(expr.getStart(), t.getEnd(), expr, Tree.Kind.POSTFIX_INCREMENT);
			default:
				break;
		}
		throw new JSUnexpectedTokenException(t);
	}
	/**
	 * Parses the immediate next expression. For example, in 'a*b+c', it would parse 'a*b'
	 * @param t
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected ExpressionTree parseNextExpression(Token t, JSLexer src, Context context) {
		if (t == null)
			t = src.nextToken();
		
		// TODO finish
		return null;
	}
	
	protected FunctionCallTree parseFunctionCall(ExpressionTree functionSelectExpression, Token openParenToken,
			JSLexer src, Context context) {
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		List<? extends ExpressionTree> arguments = parseParentheticalSeries(openParenToken, src, context);
		return new FunctionCallTreeImpl(functionSelectExpression.getStart(), src.getPosition(), arguments, functionSelectExpression);
	}
	
	protected List<? extends ExpressionTree> parseParentheticalSeries(Token openParenToken, JSLexer src, Context context) {
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		ensureToken(openParenToken, JSOperator.LEFT_PARENTHESIS);
		List<ExpressionTree> result = new LinkedList<>();
		Token next;
		do {
			result.add(this.parseNextExpression(src, context));
		} while ((next = src.nextToken()).getValue() == JSOperator.COMMA);
		ensureToken(next, JSOperator.RIGHT_PARENTHESIS);
		return result;
	}
	
	protected GotoTree parseGotoStatement(Token keywordToken, JSLexer src, Context context) {
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
	
	protected UnaryTree parsePrefixUnary(Token keywordToken, JSLexer src, Context context) {
		if (keywordToken == null)
			keywordToken = src.nextToken();
		Tree.Kind kind = null;
		switch (keywordToken.getKind()) {
			case KEYWORD:
				switch ((JSKeyword) keywordToken.getValue()) {
					case VOID: {
						Token next = src.peekNextToken();
						if ((next = src.nextToken()).getKind() == TokenKind.SPECIAL
								&& next.getValue() == JSSpecialGroup.SEMICOLON) {
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
		ExpressionTree expression = this.parseNextExpression(src, context);
		if (kind == Tree.Kind.VOID)
			return new UnaryTreeImpl.VoidTreeImpl(expression);
		return new UnaryTreeImpl(keywordToken.getStart(), src.getPosition(), expression, kind);
	}
	
	protected DebuggerTree parseDebugger(Token debuggerKeywordToken, JSLexer src, Context context) {
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	protected BlockTree parseBlock(Token openBraceToken, JSLexer src, Context context) {
		openBraceToken = expect(openBraceToken, TokenKind.BRACKET, '{', src, context);
		List<StatementTree> statements = new LinkedList<>();
		Token t;
		while ((t = src.nextToken()).getKind() != TokenKind.BRACKET)
			statements.add(parseStatement(t, src, context));
		ensureToken(t, '}');
		return new BlockTreeImpl(openBraceToken.getStart(), src.getPosition(), statements);
	}
	protected static class Context {
		ContextData data = new ContextData();
		public void push() {
			data = new ContextData(data);
		}
		public void pop() {
			data = data.parent;
		}
		public boolean isStrict() {
			return data.isStrict;
		}
		public boolean allowReturn() {
			return data.inFunction || data.inGenerator;
		}
		public boolean allowBreak() {
			return data.inLoop || data.inSwitch || data.inNamedBlock;
		}
		public boolean allowContinue() {
			return data.inLoop;
		}
		public boolean allowYield() {
			return data.inGenerator;
		}
		static class ContextData {
			boolean isStrict = false;
			/**
			 * Whether the parser is currently inside a function statement.
			 * Allows the <code>return</code> keyword.
			 */
			boolean inFunction = false;
			/**
			 * Whether the parser is currently inside a switch statement.
			 * Allows the <code>break</code> keyword.
			 */
			boolean inSwitch = false;
			boolean inGenerator = false;
			/**
			 * Whether the parser is currently inside a loop statement.
			 * Allows the <code>break</code> and <code>continue</code> statements.
			 */
			boolean inLoop = false;
			boolean isAssignmentTarget = false;
			boolean inNamedBlock = false;
			final ContextData parent;
			public ContextData() {
				this.parent = null;
			}
			public ContextData(ContextData parent) {
				this.isStrict = parent.isStrict;
				this.inFunction = parent.inFunction;
				this.inSwitch = parent.inSwitch;
				this.inGenerator = parent.inGenerator;
				this.inLoop = parent.inLoop;
				this.isAssignmentTarget = parent.isAssignmentTarget;
				this.parent = parent;
			}
		}
	}
}