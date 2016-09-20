package com.mindlin.jsast.impl.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.ArrayLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.BooleanLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.CaseTreeImpl;
import com.mindlin.jsast.impl.tree.CatchTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.ConditionalExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionCallTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTypeTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.ImportSpecifierTreeImpl;
import com.mindlin.jsast.impl.tree.ImportTreeImpl;
import com.mindlin.jsast.impl.tree.IndexTypeTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.InterfacePropertyTreeImpl;
import com.mindlin.jsast.impl.tree.NewTreeImpl;
import com.mindlin.jsast.impl.tree.NullLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ParameterTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.ReturnTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceTreeImpl;
import com.mindlin.jsast.impl.tree.StringLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.SuperExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.SwitchTreeImpl;
import com.mindlin.jsast.impl.tree.ThisExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ThrowTreeImpl;
import com.mindlin.jsast.impl.tree.TryTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclaratorTreeImpl;
import com.mindlin.jsast.impl.tree.VoidTypeTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WithTreeImpl;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.EnumDeclarationTree;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressiveStatementTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.ObjectLiteralPropertyTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.SequenceTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.VoidTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;

public class JSParser {
	/**
	 * A convenience method, to assert a token's kind
	 * @param token
	 * @param value
	 */
	private static void expect(Token token, TokenKind kind) {
		if (token.getKind() != kind)
			throw new JSSyntaxException("Illegal token " + token + "; expected kind " + kind, token.getStart());
	}
	
	/**
	 * A convenience method, to assert a token's value
	 * @param token
	 * @param value
	 */
	private static void expect(Token token, Object value) {
		if (!Objects.equals(token.getValue(), value))
			throw new JSSyntaxException("Illegal token " + token + "; expected value " + value, token.getStart());
	}
	
	private static Token expect(Token token, TokenKind kind, Object value, JSLexer src, Context context) {
		if (token == null)
			token = src.nextToken();
		if (token.getKind() != kind)
			throw new JSSyntaxException("Illegal token " + token + "; expected kind " + kind, token.getStart());
		if (!Objects.equals(token.getValue(), value))
			throw new JSSyntaxException("Illegal token " + token + "; expected value " + value, token.getStart());
		return token;
	}
	
	private static Token expect(TokenKind kind, Object value, JSLexer src, Context context) {
		Token t = src.nextToken();
		expect(t, kind);
		expect(t, value);
		return t;
	}
	
	private static void expect(Token token, TokenKind kind, Object value) {
		expect(token, kind);
		expect(token, value);
	}
	
	private static Token expect(Token token, TokenKind kind, JSLexer src, Context context) {
		if (token == null)
			token = src.nextToken();
		expect(token, kind);
		return token;
	}
	
	private static Token expectOperator(JSOperator operator, JSLexer src, Context context) {
		return expect(TokenKind.OPERATOR, operator, src, context);
	}
	
	private static Token expectSemicolon(JSLexer src, Context context) {
		return expect(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON, src, context);
	}
	
	//Parser properties
	protected JSDialect dialect;
	
	public JSParser() {
		this(JSDialect.JSStandardDialect.EVERYTHING);
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
		context.setScriptName(unitName);
		while ((value = parseStatement(src, context)) != null)
			elements.add(value);
		return new CompilationUnitTreeImpl(0, src.getPosition(), unitName, null, elements, false);
	}
	
	public Tree parseNext(JSLexer src, Context context) {
		return parseNext(src.nextToken(), src, context);
	}
	
	protected Tree parseNext(Token token, JSLexer src, Context context) {
		switch (token.getKind()) {
			case KEYWORD:
				switch (token.<JSKeyword>getValue()) {
					case BREAK:
					case CONTINUE:
						return this.parseGotoStatement(token, src, context);
					case CLASS:
						return this.parseClass(token, src, context);
					case CONST:
					case LET:
					case VAR:
						return this.parseVariableDeclaration(token, src, context);
					case DEBUGGER:
						return this.parseDebugger(token, src, context);
					case DO:
						return this.parseDoWhileLoop(token, src, context);
					case AWAIT:
					case ENUM:
					case TYPE:
						throw new UnsupportedOperationException();
					case EXPORT:
						return this.parseExportStatement(token, src, context);
					case FOR:
						return this.parseForStatement(token, src, context);
					case FUNCTION:
					case FUNCTION_GENERATOR:
						return this.parseFunctionExpression(token, src, context);
					case IF:
						return this.parseIfStatement(token, src, context);
					case IMPORT:
						return this.parseImportStatement(token, src, context);
					case INTERFACE:
						return this.parseInterface(token, src, context);
					case RETURN:
					case THROW:
						return this.parseUnaryStatement(token, src, context);
					case SWITCH:
						return this.parseSwitchStatement(token, src, context);
					case TRY:
						return this.parseTryStatement(token, src, context);
					case VOID:
						return this.parseVoid(token, src, context);
					case WHILE:
						return this.parseWhileLoop(token, src, context);
					case WITH:
						return this.parseWithStatement(token, src, context);
					case DELETE:
					case NEW:
					case TYPEOF:
					case YIELD:
					case YIELD_GENERATOR:
					case SUPER:
					case THIS: {
						ExpressionTree expr = parseUnaryExpression(token, src, context);
						if (expr.getKind().isStatement())
							return (StatementTree) expr;
						return new ExpressionStatementTreeImpl(expr);
					}
					case CASE:
					case CATCH:
					case FINALLY:
					case DEFAULT:
					case ELSE:
					case EXTENDS:
					case IN:
					case INSTANCEOF:
					default:
						throw new JSSyntaxException("Unexpected keyword " + token.getValue(), token.getStart());
				}
			case IDENTIFIER: {
				Token lookahead = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON);
				if (lookahead != null)
					return this.parseLabeledStatement(this.parseIdentifier(token, src, context), lookahead, src, context);
			}
			case BOOLEAN_LITERAL:
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case REGEX_LITERAL:
			case TEMPLATE_LITERAL:
			case NULL_LITERAL:
				return this.parseNextExpression(token, src, context);
			case BRACKET:
				if (token.<Character>getValue() == '{')
					return this.parseBlock(token, src, context);
				else if (token.<Character>getValue() == '[')
					return this.parseNextExpression(token, src, context);
			case OPERATOR:
				return this.parseUnaryExpression(token, src, context);
			case SPECIAL:
				switch (token.<JSSpecialGroup>getValue()) {
					case EOF:
						return null;
					case EOL:
					case SEMICOLON:
						return new EmptyStatementTreeImpl(token);
					default:
						break;
				}
			case COMMENT:
				break;
		}
		throw new JSUnexpectedTokenException(token);
	}
	
	protected StatementTree parseStatement(JSLexer src, Context context) {
		Tree next = parseNext(src, context);
		if (next == null)
			return null;
		expectSemicolon(src, context);
		if (next.getKind().isStatement())
			return (StatementTree)next;
		else if (next.getKind().isExpression())
			return new ExpressionStatementTreeImpl((ExpressionTree)next);
		else if (next.getKind().isType())
			throw new JSSyntaxException("Unexpected TypeTree when parsing statement: " + next);
		throw new JSSyntaxException("Unexpected Tree when parsing statement: " + next);
	}
	
	//Helper methods
	
	/**
	 * 
	 * @param commaToken
	 * @param src
	 * @param ctx
	 */
	protected void expectCommaSeparator(JSLexer src, Context ctx) {
		if (dialect.supports("extension.tolerance")) {
			Token commaToken = src.peek();
			if (commaToken.matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				src.skip(commaToken);	
			} else if (commaToken.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)) {
				src.skip(commaToken);
				//TODO tolerate
				throw new JSUnexpectedTokenException(commaToken);
			} else {
				throw new JSUnexpectedTokenException(commaToken);
			}
		} else {
			expectOperator(JSOperator.COMMA, src, ctx);
		}
	}

	/**
	 * Map a JSOperator type to a Tree.Kind type. Does not support operators not binary.
	 * 
	 * @param operator
	 * @return
	 */
	protected Tree.Kind mapTokenToBinaryTree(Token token) {
		if (token.isOperator())
			switch (token.<JSOperator>getValue()) {
				case ADDITION_ASSIGNMENT:
					return Tree.Kind.ADDITION_ASSIGNMENT;
				case ASSIGNMENT:
					return Tree.Kind.ASSIGNMENT;
				case BITWISE_AND:
					return Tree.Kind.BITWISE_AND;
				case BITWISE_AND_ASSIGNMENT:
					return Tree.Kind.BITWISE_AND_ASSIGNMENT;
				case BITWISE_NOT:
					return Tree.Kind.BITWISE_NOT;
				case BITWISE_OR:
					return Tree.Kind.BITWISE_OR;
				case BITWISE_OR_ASSIGNMENT:
					return Tree.Kind.BITWISE_OR_ASSIGNMENT;
				case BITWISE_XOR:
					return Tree.Kind.BITWISE_XOR;
				case BITWISE_XOR_ASSIGNMENT:
					return Tree.Kind.BITWISE_XOR_ASSIGNMENT;
				case DIVISION:
					return Tree.Kind.DIVISION;
				case DIVISION_ASSIGNMENT:
					return Tree.Kind.DIVISION_ASSIGNMENT;
				case EQUAL:
					return Tree.Kind.EQUAL;
				case EXPONENTIATION:
					return Tree.Kind.EXPONENTIATION;
				case EXPONENTIATION_ASSIGNMENT:
					return Tree.Kind.EXPONENTIATION_ASSIGNMENT;
				case GREATER_THAN:
					return Tree.Kind.GREATER_THAN;
				case GREATER_THAN_EQUAL:
					return Tree.Kind.GREATER_THAN_EQUAL;
				case LEFT_SHIFT:
					return Tree.Kind.LEFT_SHIFT;
				case LEFT_SHIFT_ASSIGNMENT:
					return Tree.Kind.LEFT_SHIFT_ASSIGNMENT;
				case LESS_THAN:
					return Tree.Kind.LESS_THAN;
				case LESS_THAN_EQUAL:
					return Tree.Kind.LESS_THAN_EQUAL;
				case LOGICAL_AND:
					return Tree.Kind.LOGICAL_AND;
				case LOGICAL_NOT:
					return Tree.Kind.LOGICAL_NOT;
				case LOGICAL_OR:
					return Tree.Kind.LOGICAL_OR;
				case MINUS:
					return Tree.Kind.SUBTRACTION;
				case MULTIPLICATION:
					return Tree.Kind.MULTIPLICATION;
				case MULTIPLICATION_ASSIGNMENT:
					return Tree.Kind.MULTIPLICATION_ASSIGNMENT;
				case NOT_EQUAL:
					return Tree.Kind.NOT_EQUAL;
				case PERIOD:
					return Tree.Kind.MEMBER_SELECT;
				case PLUS:
					return Tree.Kind.ADDITION;
				case QUESTION_MARK:
					return Tree.Kind.CONDITIONAL;
				case REMAINDER:
					return Tree.Kind.REMAINDER;
				case REMAINDER_ASSIGNMENT:
					return Tree.Kind.REMAINDER_ASSIGNMENT;
				case RIGHT_SHIFT:
					return Tree.Kind.RIGHT_SHIFT;
				case RIGHT_SHIFT_ASSIGNMENT:
					return Tree.Kind.RIGHT_SHIFT_ASSIGNMENT;
				case STRICT_EQUAL:
					return Tree.Kind.STRICT_EQUAL;
				case STRICT_NOT_EQUAL:
					return Tree.Kind.STRICT_NOT_EQUAL;
				case SUBTRACTION_ASSIGNMENT:
					return Tree.Kind.SUBTRACTION_ASSIGNMENT;
				case UNSIGNED_RIGHT_SHIFT:
					return Tree.Kind.UNSIGNED_RIGHT_SHIFT;
				case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
					return Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
				case INCREMENT:
				case DECREMENT:
				case COLON:
				case COMMA:
				case LAMBDA:
				case LEFT_PARENTHESIS:
				case RIGHT_PARENTHESIS:
				default:
					break;
			}
		if (token.isKeyword())
			switch (token.<JSKeyword>getValue()) {
				case YIELD:
					return Tree.Kind.YIELD;
				case YIELD_GENERATOR:
					return Tree.Kind.YIELD_GENERATOR;
				default:
					break;
			}
		throw new JSSyntaxException(token + "is not a binary operator", token.getStart());
	}
	
	List<ParameterTree> reinterpretExpressionAsParameterList(ExpressionTree expr) {
		if (expr.getKind() == Kind.PARENTHESIZED)
			return reinterpretExpressionAsParameterList(((ParenthesizedTree)expr).getExpression());
		if (expr.getKind() == Kind.SEQUENCE) {
			List<ExpressionTree> exprs= ((SequenceTree)expr).getExpressions();
			ArrayList<ParameterTree> params = new ArrayList<>(exprs.size());
			for (ExpressionTree child : exprs)
				params.add(reinterpretExpressionAsParameter(child));
			params.trimToSize();
			return params;
		}
		return Arrays.asList(reinterpretExpressionAsParameter(expr));
	}
	
	ParameterTree reinterpretExpressionAsParameter(ExpressionTree expr) {
		switch (expr.getKind()) {
			case IDENTIFIER:
				return new ParameterTreeImpl((IdentifierTree)expr);
			case ASSIGNMENT: {
				dialect.require("js.parameter.default", expr.getStart());
				//Turn into default parameter
				AssignmentTree assignment = (AssignmentTree) expr;
				if (assignment.getLeftOperand().getKind() != Tree.Kind.IDENTIFIER)
					throw new JSSyntaxException("Cannot reinterpret " + expr + "as parameter (left-hand side of assignment expression is not an identifier)", expr.getStart());
				// I can't find any example of an expression that can't be used
				// as a default value (except ones that won't run at all)
				
				IdentifierTree identifier = (IdentifierTree) assignment.getLeftOperand();
				return new ParameterTreeImpl(identifier.getStart(), assignment.getRightOperand().getEnd(), identifier, false, false, null, assignment.getRightOperand());
			}
			case SPREAD:
				dialect.require("js.parameter.rest", expr.getStart());
				//Turn into rest parameter
				return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), (IdentifierTree)((UnaryTree)expr).getExpression(), true, false, null, null);
			case ARRAY_LITERAL:
			case OBJECT_LITERAL:
				dialect.require("js.parameter.destructured", expr.getStart());
				// TODO support for destructuring
				throw new UnsupportedOperationException("Array/Object literal conversion to destructuring parameters not yet supported");
			default:
				break;
		}
		throw new JSSyntaxException("Cannot reinterpret " + expr + "as parameter", expr.getStart());
	}
	
	protected ExpressionTree parsePrimaryExpression(Token t, JSLexer src, Context context) {
		switch (t.getKind()) {
			case IDENTIFIER:
				return parseIdentifier(t, src, context);
			case NUMERIC_LITERAL:
				if (context.isStrict()) {
					//TODO throw error on implicit octal
				}
			case STRING_LITERAL:
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
			case TEMPLATE_LITERAL:
				return parseLiteral(t, src, context);
			case OPERATOR:
				switch (t.<JSOperator>getValue()) {
					case LEFT_PARENTHESIS:
						context.isBindingElement(false);
						return this.parseGroupExpression(t, src, context.pushed());
					case DIVISION:
					case DIVISION_ASSIGNMENT:
						//TODO finish (parse regex literal)
						throw new UnsupportedOperationException();
					default:
						throw new JSUnexpectedTokenException(t);
				}
			case BRACKET:
				switch ((char)t.getValue()) {
					case '[':
						return this.parseArrayInitializer(t, src, context);
					case '{':
						return this.parseObjectInitializer(t, src, context);
					default:
						throw new JSUnexpectedTokenException(t);
				}
			case KEYWORD:
				context.isAssignmentTarget(false).isBindingElement(false);
				switch (t.<JSKeyword>getValue()) {
					case FUNCTION:
						return this.parseFunctionExpression(t, src, context);
					case THIS:
						return parseThis(t, src, context);
					case CLASS:
						return this.parseClass(t, src, context);
					default:
						throw new JSUnexpectedTokenException(t);
				}
			default:
				throw new JSUnexpectedTokenException(t);
		}
	}
	
	protected List<ExpressionTree> parseArguments(Token t, JSLexer src, Context context) {
		 t = expect(t, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		 List<ExpressionTree> result = new ArrayList<>();
		 t = src.nextToken();
		 if (t.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			 return result;
		 while (true) {
			 ExpressionTree expr;
			 if (t.matches(TokenKind.OPERATOR, JSOperator.SPREAD))
				 expr = parseSpread(t, src, context);
			 else
				 expr = parseAssignment(t, src, context.pushed());
			 result.add(expr);
			 t = src.nextToken();
			 if (t.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
				 break;
			 if (!t.matches(TokenKind.OPERATOR, JSOperator.COMMA))
				 throw new JSUnexpectedTokenException(t);
			 t = src.nextToken();
		 }
		 if (!t.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			 throw new JSUnexpectedTokenException(t);
		 return result;
	}
	
	//Various statements
	
	protected ImportTree parseImportStatement(Token importKeywordToken, JSLexer src, Context context) {
		importKeywordToken = expect(importKeywordToken, TokenKind.KEYWORD, JSKeyword.IMPORT, src, context);
		List<ImportSpecifierTree> importSpecifiers = new ArrayList<>();
		Token t = src.nextToken();
		if (t.isIdentifier()) {
			importSpecifiers.add(new ImportSpecifierTreeImpl(parseIdentifier(t, src, context)));
			if (!(t = src.nextToken()).matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				expect(t, TokenKind.KEYWORD, JSKeyword.FROM, src, context);
				StringLiteralTree source = (StringLiteralTree)parseLiteral(null, src, context);
				return new ImportTreeImpl(importKeywordToken.getStart(), source.getEnd(), importSpecifiers, source);
			}
			t = src.nextToken();
		}
		if (t.matches(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) {
			IdentifierTree identifier = new IdentifierTreeImpl(t.getStart(), t.getEnd(), "*");
			t = expect(TokenKind.KEYWORD, JSKeyword.AS, src, context);
			IdentifierTree alias = parseIdentifier(null, src, context);
			importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
			t = src.nextToken();
		} else if (t.matches(TokenKind.BRACKET, '{')) {
			do {
				t = src.nextToken();
				IdentifierTree identifier = parseIdentifier(t, src, context);
				IdentifierTree alias = identifier;
				if ((t = src.nextToken()).matches(TokenKind.KEYWORD, JSKeyword.AS)) {
					alias = parseIdentifier(null, src, context);
					t = src.nextToken();
				}
				importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
			} while (t.matches(TokenKind.OPERATOR, JSOperator.COMMA));
			expect(t, TokenKind.BRACKET, '}', src, context);
			t = src.nextToken();
		}
		if (!importSpecifiers.isEmpty()) {
			expect(t, TokenKind.KEYWORD, JSKeyword.FROM, src, context);
			t = null;
		}
		StringLiteralTree source = (StringLiteralTree)parseLiteral(t, src, context);
		return new ImportTreeImpl(importKeywordToken.getStart(), source.getEnd(), importSpecifiers, source);
	}
	
	protected ExportTree parseExportStatement(Token exportKeywordToken, JSLexer src, Context context) {
		exportKeywordToken = expect(exportKeywordToken, TokenKind.KEYWORD, JSKeyword.EXPORT, src, context);
		ExpressionTree expr = parseNextExpression(src, context);
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	TypeTree reinterpretAsType(Token typeToken) {
		if (typeToken.matches(TokenKind.KEYWORD, JSKeyword.VOID)) {
			return new VoidTypeTreeImpl(typeToken);
		}
		//TODO fix identifiers
		throw new JSUnexpectedTokenException(typeToken);
	}
	
	protected TypeTree parseTypeStatement(Token typeToken, JSLexer src, Context context) {
		if (typeToken == null)
			typeToken = src.nextToken();
		if (typeToken.matches(TokenKind.KEYWORD, JSKeyword.VOID))
				return new VoidTypeTreeImpl(typeToken);
		if (typeToken.isIdentifier()) {
			switch (typeToken.<String>getValue()) {
				case "any":
				case "string":
				case "number":
				case "boolean":
					//TODO finish
			}
		}
		throw new UnsupportedOperationException();
	}
	
	@JSKeywordParser({ JSKeyword.CONST, JSKeyword.LET, JSKeyword.VAR })
	protected VariableDeclarationTree parseVariableDeclaration(Token keywordToken, JSLexer src, Context context) {
		keywordToken = expect(keywordToken, TokenKind.KEYWORD, src, context);
		boolean isConst = keywordToken.getValue() == JSKeyword.CONST;
		boolean isScoped = isConst || keywordToken.getValue() == JSKeyword.LET;//Const's are also scoped
		//Check that the token is 'var', 'let', or 'const'.
		if (keywordToken.getValue() != JSKeyword.VAR && !isScoped)
			throw new JSUnexpectedTokenException(keywordToken);
		//Check if allowed
		if (isScoped) {
			dialect.require("js.variable.scoped", keywordToken.getStart());
			if (isConst)
				dialect.require("js.variable.const", keywordToken.getStart());
		}
		
		//Build list of declarations
		List<VariableDeclaratorTree> declarations = new ArrayList<>();
		//Parse identifier(s)
		do {
			Token identifier = src.nextToken();
			if (!identifier.isIdentifier())
				throw new JSUnexpectedTokenException(identifier);
			
			//Check if a type is available
			TypeTree type = this.parseTypeMaybe(src, context, false);
			
			//Check if an initializer is available
			ExpressionTree initializer = null;
			if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASSIGNMENT) != null)
				initializer = parseAssignment(null, src, context);
			else if (isConst)
				//No initializer
				throw new JSSyntaxException("Missing initializer in constant declaration", identifier.getStart());
			
			declarations.add(new VariableDeclaratorTreeImpl(identifier.getStart(), src.getPosition(), new IdentifierTreeImpl(identifier), type, initializer));
		} while (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		
		return new VariableDeclarationTreeImpl(keywordToken.getStart(), src.getPosition(), isScoped, isConst, declarations);
	}
	
	protected ExpressiveStatementTree parseUnaryStatement(Token keywordToken, JSLexer src, Context context) {
		keywordToken = expect(keywordToken, TokenKind.KEYWORD, src, context);
		if (!(keywordToken.getValue() == JSKeyword.RETURN || keywordToken.getValue() == JSKeyword.THROW))
			throw new JSUnexpectedTokenException(keywordToken);
		ExpressionTree expr = parseNextExpression(null, src, context);
		if (keywordToken.getValue() == JSKeyword.RETURN)
			return new ReturnTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
		else
			return new ThrowTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
	}

	protected LabeledStatementTree parseLabeledStatement(IdentifierTree identifier, Token colon, JSLexer src, Context ctx) {
		throw new UnsupportedOperationException();
	}
	
	//Type structures
	
	@JSKeywordParser(JSKeyword.CLASS)
	protected ClassDeclarationTree parseClass(Token classKeywordToken, JSLexer src, Context context) {
		if (classKeywordToken == null)
			classKeywordToken = src.nextToken();
		//TODO fix for abstract classes
		classKeywordToken = expect(classKeywordToken, TokenKind.KEYWORD, JSKeyword.CLASS, src, context);
		IdentifierTree classIdentifier = null;
		TypeTree superClass = null;
		List<TypeTree> interfaces = new ArrayList<>();
		
		Token next = src.nextToken();
		if (next.isIdentifier()) {
			classIdentifier = this.parseIdentifier(next, src, context);
			next = src.nextToken();
		}
		for (int i = 0; i < 1; i++) {
			if (next.matches(TokenKind.KEYWORD, JSKeyword.EXTENDS) && superClass == null) {
				superClass = this.parseType(src, context);
				next = src.nextToken();
			}
			if (next.matches(TokenKind.KEYWORD, JSKeyword.IMPLEMENTS) && interfaces.isEmpty()) {
				do {
					interfaces.add(parseType(src, context));
				} while ((next = src.nextToken()).matches(TokenKind.OPERATOR, JSOperator.COMMA));
			}
		}
		expect(next, TokenKind.BRACKET, '{', src, context);
		
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected InterfaceDeclarationTree parseInterface(Token interfaceKeywordToken, JSLexer src, Context context) {
		interfaceKeywordToken = expect(interfaceKeywordToken, TokenKind.KEYWORD, JSKeyword.INTERFACE, src, context);
		dialect.require("ts.types.interface", interfaceKeywordToken.getStart());
		Token next = src.nextToken();
		expect(next, TokenKind.IDENTIFIER);
		IdentifierTree name = parseIdentifier(next, src, context);
		List<TypeTree> superClasses;
		if ((next = src.nextTokenIf(TokenKind.KEYWORD, JSKeyword.EXTENDS)) != null) {
			superClasses = new ArrayList<>();
			do {
				superClasses.add(parseType(src, context));
			} while (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		} else {
			superClasses = Collections.emptyList();
		}
		expect(src.nextToken(), TokenKind.BRACKET, '{');
		List<InterfacePropertyTree> properties = new ArrayList<>();
		while (!(next = src.nextToken()).matches(TokenKind.BRACKET, '}')) {
			IdentifierTree propname;
			boolean optional;
			TypeTree type;
			if (next.isIdentifier()) {
				propname = new IdentifierTreeImpl(next);
				optional = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) != null;
				type = this.parseTypeMaybe(src, context, true);
			} else if (next.matches(TokenKind.BRACKET, '[')) {
				//Parse index type
				Token id = src.nextToken();
				expect(id, TokenKind.IDENTIFIER);
				propname = new IdentifierTreeImpl(id);
				optional = false;
				expectOperator(JSOperator.COLON, src, context);
				TypeTree idxType = this.parseType(src, context);
				expect(TokenKind.BRACKET, ']', src, context);
				expectOperator(JSOperator.COLON, src, context);
				TypeTree returnType = this.parseType(src, context);
				type = new IndexTypeTreeImpl(next.getStart(), src.getPosition(), false, idxType, returnType);
			} else {
				throw new JSUnexpectedTokenException(next);
			}
			properties.add(new InterfacePropertyTreeImpl(next.getStart(), src.getPosition(), propname, optional, type));
		}
		return new InterfaceDeclarationTreeImpl(interfaceKeywordToken.getStart(), next.getEnd(), name, superClasses, properties);
	}
	
	protected EnumDeclarationTree parseEnum(Token enumKeywordToken, JSLexer src, Context context) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Parse a type, if unknown whether a type declaration follows the current statement.
	 * Uses the colon (<kbd>:</kbd>) to detect if there should be a type.
	 * @param src The source
	 * @param context
	 * @return
	 */
	protected TypeTree parseTypeMaybe(JSLexer src, Context context, boolean canBeFunction) {
		if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON) == null) {
			if (canBeFunction && src.peek().matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS))
				return parseFunctionType(src, context);
			return null;
		}
		return parseType(src, context);
	}
	
	protected TypeTree parseFunctionType(JSLexer src, Context context) {
		throw new UnsupportedOperationException();
	}
	
	protected TypeTree parseImmediateType(Token startToken, JSLexer src, Context context) {
		if (startToken.isIdentifier() || startToken.matches(TokenKind.KEYWORD, JSKeyword.VOID) || startToken.matches(TokenKind.KEYWORD, JSKeyword.THIS)) {
			IdentifierTree identifier = new IdentifierTreeImpl(startToken);
			List<TypeTree> generics;
			if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LESS_THAN) != null) {
				generics = new ArrayList<>();
				do {
					generics.add(parseType(src, context));
				} while ((startToken = src.nextToken()).matches(TokenKind.OPERATOR, JSOperator.COMMA));
			} else {
				generics = Collections.emptyList();
			}
			return  new IdentifierTypeTreeImpl(identifier.getStart(), startToken.getEnd(), false, identifier, generics);
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION)) {
			//Function
			return parseFunctionType(src, context);
		} else if (startToken.matches(TokenKind.BRACKET, '{')) {
			//Inline interface
		} else if (startToken.matches(TokenKind.BRACKET, '[')) {
			//Tuple
		} else if (startToken.isLiteral()) {
			//String literal
		} else {
			throw new JSUnexpectedTokenException(startToken);
		}
		throw new UnsupportedOperationException();
	}
	
	protected TypeTree parseType(JSLexer src, Context context) {
		TypeTree type = parseImmediateType(src.nextToken(), src, context);
		
		return type;
	}
	
	
	//Control flows
	
	protected DebuggerTree parseDebugger(Token debuggerKeywordToken, JSLexer src, Context context) {
		debuggerKeywordToken = expect(debuggerKeywordToken, TokenKind.KEYWORD, JSKeyword.DEBUGGER, src, context);
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	protected BlockTree parseBlock(Token openBraceToken, JSLexer src, Context context) {
		openBraceToken = expect(openBraceToken, TokenKind.BRACKET, '{', src, context);
		List<StatementTree> statements = new LinkedList<>();
		Token t;
		while (!(t = src.nextToken()).matches(TokenKind.BRACKET, '}'))
			statements.add(parseStatement(t, src, context));
		expect(t, '}');
		return new BlockTreeImpl(openBraceToken.getStart(), src.getPosition(), statements);
	}
	
	protected GotoTree parseGotoStatement(Token keywordToken, JSLexer src, Context context) {
		keywordToken = expect(keywordToken, TokenKind.KEYWORD, src, context);
		if (keywordToken.getValue() != JSKeyword.BREAK && keywordToken.getValue() != JSKeyword.CONTINUE)
			throw new JSUnexpectedTokenException(keywordToken);
		//TODO check if keyword allowed in context
		String label = null;
		Token next = src.nextTokenIf(TokenKind.IDENTIFIER);
		if (next != null)
			label = next.getValue();
		final long start = keywordToken.getStart();
		final long end = src.getPosition();
		if (keywordToken.getValue() == JSKeyword.BREAK)
			return new AbstractGotoTree.BreakTreeImpl(start, end, label);
		else if (keywordToken.getValue() == JSKeyword.CONTINUE)
			return new AbstractGotoTree.ContinueTreeImpl(start, end, label);
		throw new JSUnexpectedTokenException(keywordToken);
	}
	
	protected IfTree parseIfStatement(Token ifKeywordToken, JSLexer src, Context context) {
		ifKeywordToken = expect(ifKeywordToken, TokenKind.KEYWORD, JSKeyword.IF, src, context);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree thenStatement = parseStatement(null, src, context);
		StatementTree elseStatement = null;
		Token next = src.nextTokenIf(TokenKind.KEYWORD, JSKeyword.ELSE);
		if (next != null) {
			next = src.nextToken();
			// This if statement isn't really needed, but it speeds up 'else if'
			// statements by a bit, and else if statements are more common than
			// else statements (IMHO)
			if (next.matches(TokenKind.KEYWORD, JSKeyword.IF))
				elseStatement = parseIfStatement(next, src, context);
			else
				elseStatement = parseStatement(next, src, context);
		} else {
			next = expect(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON, src, context);
			elseStatement = new EmptyStatementTreeImpl(next);
		}
		return new IfTreeImpl(ifKeywordToken.getStart(), src.getPosition(), expression, thenStatement, elseStatement);
	}
	
	protected SwitchTree parseSwitchStatement(Token switchKeywordToken, JSLexer src, Context context) {
		switchKeywordToken = expect(switchKeywordToken, TokenKind.KEYWORD, JSKeyword.SWITCH, src, context);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		src.expect(TokenKind.BRACKET, '{');
		List<CaseTree> cases = new LinkedList<>();
		Token next = src.nextToken();
		while (next.isKeyword()) {
			ExpressionTree caseExpr;
			List<? extends StatementTree> statements = new LinkedList<>();
			if (next.getValue() == JSKeyword.CASE)
				caseExpr = this.parseNextExpression(src, context);
			else if (next.getValue() == JSKeyword.DEFAULT)
				caseExpr = null;
			else
				throw new JSUnexpectedTokenException(next);
			src.expect(JSOperator.COLON);
			// TODO parse statements
			cases.add(new CaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	protected TryTree parseTryStatement(Token tryKeywordToken, JSLexer src, Context context) {
		tryKeywordToken = expect(tryKeywordToken, TokenKind.KEYWORD, JSKeyword.TRY, src, context);
		BlockTree tryBlock = parseBlock(null, src, context);
		ArrayList<CatchTree> catchBlocks = new ArrayList<>();
		
		Token next;
		while ((next = src.nextTokenIf(TokenKind.KEYWORD, JSKeyword.CATCH)) != null) {
			expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
			IdentifierTree param = parseIdentifier(src.nextToken(), src, context);
			
			Token t = src.nextToken();
			expect(t, TokenKind.OPERATOR);
			//Optional param type
			TypeTree type = this.parseTypeMaybe(src, context, false);
			
			expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
			BlockTree block = parseBlock(null, src, context);
			catchBlocks.add(new CatchTreeImpl(next.getStart(), block.getEnd(), block, param, type));
		}

		//Optional finally block
		BlockTree finallyBlock = null;
		if (src.nextTokenIf(TokenKind.KEYWORD, JSKeyword.FINALLY) != null)
			finallyBlock = parseBlock(null, src, context);
		else if (catchBlocks.isEmpty())
			//No catch or finally blocks
			throw new JSSyntaxException("Incomplete try statement", src.getPosition());
		catchBlocks.trimToSize();
		return new TryTreeImpl(tryKeywordToken.getStart(), src.getPosition(), tryBlock, catchBlocks, finallyBlock);
	}
	
	protected IfTree parseFunctionStatement(Token functionKeywordToken, JSLexer src, Context context) {
		functionKeywordToken = expect(functionKeywordToken, TokenKind.KEYWORD, JSKeyword.FUNCTION, src, context);
		//TODO finish
		throw new UnsupportedOperationException("Type support is (currently) not supported");
	}
	
	protected WithTree parseWithStatement(Token withKeywordToken, JSLexer src, Context context) {
		withKeywordToken = expect(withKeywordToken, TokenKind.KEYWORD, JSKeyword.WITH, src, context);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		return new WithTreeImpl(withKeywordToken.getStart(), src.getPosition(), expression, statement);
	}
	
	// Loops
	
	@JSKeywordParser({JSKeyword.WHILE})
	protected WhileLoopTree parseWhileLoop(Token whileKeywordToken, JSLexer src, Context context) {
		if (whileKeywordToken == null)
			whileKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.WHILE, src, context);
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		ExpressionTree condition = parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		context.push().enterLoop();
		StatementTree statement = parseStatement(src, context);
		context.pop();
		return new WhileLoopTreeImpl(whileKeywordToken.getStart(), src.getPosition(), condition, statement);
	}
	
	@JSKeywordParser({JSKeyword.DO})
	protected DoWhileLoopTree parseDoWhileLoop(Token doKeywordToken, JSLexer src, Context context) {
		doKeywordToken = expect(doKeywordToken, TokenKind.KEYWORD, JSKeyword.DO, src, context);
		
		context.push().enterLoop();
		StatementTree statement = parseStatement(src, context);
		context.pop();
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		ExpressionTree condition = parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		expectSemicolon(src, context);
		
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
	protected LoopTree parseForStatement(Token forKeywordToken, JSLexer src, Context context) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, context);
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		
		Token next = src.nextToken();
		if (next.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
			//Empty initializer statement
			return parsePartialForLoopTree(forKeywordToken, new EmptyStatementTreeImpl(next), src, context);
		
		StatementTree initializer = null;
		if (TokenPredicate.VARIABLE_START.test(next)) {
			context.push().allowIn(false);
			VariableDeclarationTree declarations = parseVariableDeclaration(next, src, context);
			context.pop();
			
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				
				if (declarations.getDeclarations().size() != 1)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Must have 1 binding", next.getStart());
				if (declarations.getDeclarations().get(0).getIntitializer() != null)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Variable may not have an initializer", declarations.getDeclarations().get(0).getIntitializer().getStart());
				
				return parsePartialForEachLoopTree(forKeywordToken, isOf, declarations, src, context);
			}
			initializer = declarations;
		} else {
			ExpressionTree expr = this.parseLeftSideExpression(next, src, context, true);
			
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				throw new UnsupportedOperationException();
			}
			
			initializer = new ExpressionStatementTreeImpl(expr);
		}
		
		expect(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON, src, context);
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
	protected ForLoopTree parsePartialForLoopTree(Token forKeywordToken, StatementTree initializer, JSLexer src, Context context) {
		ExpressionTree condition = parseNextExpression(src, context);
		expectSemicolon(src, context);
		ExpressionTree update = parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		StatementTree statement = parseStatement(src, context);
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
			VariableDeclarationTree variable, JSLexer src, Context context) {
		ExpressionTree expression = this.parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		StatementTree statement = this.parseStatement(src, context);
		return new ForEachLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), isForEach, variable, expression,
				statement);
	}
	
	
	// Expressions

	public ExpressionTree parseNextExpression(JSLexer src, Context context) {
		return parseNextExpression(src.nextToken(), src, context);
	}
	
	/**
	 * Parses the immediate next expression. For example, in 'a*b;c', it would parse 'a*b'
	 * Will stop at:
	 * <ul>
	 * <li><kbd>;</kbd> (end of statement)</li>
	 * <li><kbd>,</kbd> (separate)</li>
	 * <li><kbd>)</kbd> (end of parenthetical/function call)</li>
	 * <li><kbd>]</kbd> (end of array access/initializer)</li>
	 * <li><kbd>}</kbd> (end of block)</li>
	 * <li><kbd>:</kbd> (end of conditional)</li>
	 * </ul>
	 * @param t
	 * @param src
	 * @param isStrict
	 * @return
	 */
	protected ExpressionTree parseNextExpression(Token t, JSLexer src, Context context) {
		if (t == null)
			t = src.nextToken();
		
		ExpressionTree result = this.parseAssignment(t, src, context.pushed());
		
		if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) == null)
			return result;
		
		ArrayList<ExpressionTree> expressions = new ArrayList<>();
		expressions.add(result);
		
		do {
			expressions.add(parseAssignment(t, src, context));
		} while (!src.isEOF() && src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
			
		expressions.trimToSize();
		return new SequenceTreeImpl(expressions);
	}

	/**
	 * Unlike most binary operators, <code>**</code> has right-associativity, which means:
	 * <ol>
	 * <li><code>a**b**c</code> is interperted as <code>a**(b**c)</code></li>
	 * <li>A lot of extra code has to be written to handle it</li>
	 * </ol>
	 * @param t
	 * @param src
	 * @param context
	 * @return
	 */
	protected ExpressionTree parseExponentiation(Token t, JSLexer src, Context context) {
		final ExpressionTree expr = this.parseUnaryExpression(t, src, context.pushed());
		Token operatorToken = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.EXPONENTIATION);
		if (operatorToken == null)
			return expr;
		context.isAssignmentTarget(false).isBindingElement(false).push();
		final ExpressionTree right = parseExponentiation(null, src, context);
		context.pop();
		return new BinaryTreeImpl(expr.getStart(), right.getEnd(), Tree.Kind.EXPONENTIATION, expr, right);
	}
	
	int binaryPrecedence(Token t) {
		if (t.isOperator())
			switch (t.<JSOperator>getValue()) {
				case COMMA:
					//return 0;
					return -1;
				case SPREAD:
					break;
				case ASSIGNMENT:
				case ADDITION_ASSIGNMENT:
				case SUBTRACTION_ASSIGNMENT:
				case MULTIPLICATION_ASSIGNMENT:
				case DIVISION_ASSIGNMENT:
				case EXPONENTIATION_ASSIGNMENT:
				case REMAINDER_ASSIGNMENT:
				case BITWISE_OR_ASSIGNMENT:
				case BITWISE_XOR_ASSIGNMENT:
				case BITWISE_AND_ASSIGNMENT:
				case LEFT_SHIFT_ASSIGNMENT:
				case RIGHT_SHIFT_ASSIGNMENT:
				case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
					return 3;
				case QUESTION_MARK:
					return 4;
				case LOGICAL_OR:
					return 5;
				case LOGICAL_AND:
					return 6;
				case BITWISE_OR:
					return 7;
				case BITWISE_XOR:
					return 8;
				case BITWISE_AND:
					return 9;
				case STRICT_EQUAL:
				case STRICT_NOT_EQUAL:
				case EQUAL:
				case NOT_EQUAL:
					return 10;
				case LESS_THAN:
				case GREATER_THAN:
				case LESS_THAN_EQUAL:
				case GREATER_THAN_EQUAL:
					return 11;
				case LEFT_SHIFT:
				case RIGHT_SHIFT:
				case UNSIGNED_RIGHT_SHIFT:
					return 12;
				case PLUS:
				case MINUS:
					return 13;
				case MULTIPLICATION:
				case DIVISION:
				case REMAINDER:
				case EXPONENTIATION:
					return 14;
				case PERIOD:
					return 18;
				default:
					return -1;
			}
		if (t.isKeyword())
			switch (t.<JSKeyword>getValue()) {
				case YIELD:
				case YIELD_GENERATOR:
					return 2;
				case IN:
				case INSTANCEOF:
					return 11;
				default:
					return -1;
			}
		return -1;
	}
	
	protected ExpressionTree parseBinaryExpression(Token startToken, JSLexer src, Context context) {
		if (startToken == null)
			startToken = src.nextToken();
		
		context.push();
		ExpressionTree expr = parseExponentiation(startToken, src, context);
		context.pop();
		
		Token token = src.peek();
		int precedence = binaryPrecedence(token);
		if (precedence < 0)
			return expr;
		
		src.skip(token);
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		final Stack<Token> operators = new Stack<>();
		final Stack<ExpressionTree> stack = new Stack<>();
		int lastPrecedence = precedence;
		
		stack.add(expr);
		operators.add(token);
		stack.add(parseExponentiation(src.nextToken(), src, context));
		
		while ((precedence = binaryPrecedence(src.peek())) >= 0) {
			while ((!operators.isEmpty()) && precedence <= lastPrecedence) {
				final ExpressionTree right = stack.pop();
				final Kind kind = this.mapTokenToBinaryTree(operators.pop());
				final ExpressionTree left = stack.pop();
				stack.add(new BinaryTreeImpl(kind, left, right));
				lastPrecedence = operators.isEmpty() ? Integer.MAX_VALUE : binaryPrecedence(operators.peek());
			}
			//Shift top onto stack
			token = src.nextToken();
			operators.add(token);
			lastPrecedence = binaryPrecedence(token);
			stack.add(parseExponentiation(src.nextToken(), src, context));
		}
		
		expr = stack.pop();
		
		//Final reduce

//		System.out.println("Stack: " + stack);
//		System.out.println("Ops: " + operators);
		while (!stack.isEmpty()) {
			final ExpressionTree left = stack.pop();
			final Kind kind = this.mapTokenToBinaryTree(operators.pop());
			expr = new BinaryTreeImpl(kind, left, expr);
		}
		if (!stack.isEmpty()) {
			System.err.println("Stack: " + stack);
			System.err.println("Ops: " + operators);
			throw new IllegalStateException("Stack not empty");
		}
		return expr;
	}
	
	protected ExpressionTree parseConditional(Token startToken, JSLexer src, Context context) {
		ExpressionTree expr = this.parseBinaryExpression(startToken, src, context.pushed());
		Token next = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK);
		if (next == null)
			return expr;
		
		context.push();
		context.allowIn(true);
		ExpressionTree concequent = parseAssignment(null, src, context);
		context.pop();
		
		expectOperator(JSOperator.COLON, src, context);
		ExpressionTree alternate = parseAssignment(null, src, context);
		
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		return new ConditionalExpressionTreeImpl(expr, concequent, alternate);
	}
	
	PatternTree reinterpretExpressionAsPattern(ExpressionTree expr) {
		throw new UnsupportedOperationException();
	}
	
	protected ExpressionTree parseAssignment(Token startToken, JSLexer src, Context context) {
		if (startToken == null)
			startToken = src.nextToken();
		if (context.allowYield() && startToken.matches(TokenKind.KEYWORD, JSKeyword.YIELD)) {
			//TODO finish
			throw new UnsupportedOperationException();
		}
		ExpressionTree expr = this.parseConditional(startToken, src, context);
		//Upgrade to lambda
		if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LAMBDA) != null)
			return this.finishFunctionBody(expr.getStart(), null, this.reinterpretExpressionAsParameterList(expr), true, false, src, context);
		
		if (!(src.peek().isOperator() && src.peek().<JSOperator>getValue().isAssignment()))
			return expr;
		if (!context.isAssignmentTarget())
			throw new JSSyntaxException("Not assignment target", src.getPosition());
		
		Token assignmentOperator = src.nextToken();
		if (assignmentOperator.matches(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) {
			expr = reinterpretExpressionAsPattern(expr);
		} else {
			context.isAssignmentTarget(false);
			context.isBindingElement(false);
		}
		
		
		final ExpressionTree right = this.parseAssignment(null, src, context.pushed());
		return new AssignmentTreeImpl(startToken.getStart(), right.getEnd(), this.mapTokenToBinaryTree(assignmentOperator), expr, right);
	}
	
	protected List<ParameterTree> parseParameters(JSLexer src, Context context) {
		ArrayList<ParameterTree> result = new ArrayList<>();
		
		do {
			Token identifier = src.nextToken();
			if (!identifier.isIdentifier()) {
				if (identifier.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
					Token spread = identifier;
					UnaryTree expr = this.parseSpread(spread, src, context);
					
					if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) != null)
						throw new JSSyntaxException("Rest parameters cannot be optional", src.getPosition());
					
					TypeTree type = this.parseTypeMaybe(src, context, false);
					
					if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASSIGNMENT) != null)
						throw new JSSyntaxException("Rest parameters cannot have a default value", src.getPosition());
					
					if (!src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
						throw new JSSyntaxException("Rest parameter must be the last", src.getPosition());
					
					result.add(new ParameterTreeImpl(identifier.getStart(), src.getPosition(), (IdentifierTree)expr.getExpression(), true, false, type, null));
					break;
				}
				throw new JSUnexpectedTokenException(identifier);
			}
			
			boolean optional = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) != null;
			
			Token colon = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON);
			TypeTree type = null;
			if (colon != null)
				type = this.parseTypeStatement(null, src, context);
			
			Token assignment = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASSIGNMENT);
			ExpressionTree defaultValue = null;
			if (assignment != null)
				defaultValue = parseAssignment(null, src, context);
			
			result.add(new ParameterTreeImpl(identifier.getStart(), src.getPosition(), new IdentifierTreeImpl(identifier), false, optional, type, defaultValue));
		} while (!src.isEOF() && src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		expect(src.peek(), TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS);
		result.trimToSize();
		return result;
	}
	/**
	 * Parse an expression starting with <kbd>(</kbd>, generating either a
	 * ParenthesizedTree or a FunctionExpressionTree (if a lambda expression)
	 * 
	 * @param leftParenToken
	 *            Token for the opening parenthesis of this expression (if null,
	 *            takes the next token from <var>src</var>)
	 * @param src
	 *            JS source code lexer
	 * @param context
	 *            Context that this is parsing in
	 * @return Either a ParenthesizedTree or a FunctionExpressionTree
	 */
	@SuppressWarnings("unchecked")
	protected ExpressionTree parseGroupExpression(Token leftParenToken, JSLexer src, Context context) {
		leftParenToken = expect(leftParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		Token next = src.nextToken();
		if (next.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			//Is lambda w/ no args ("()=>???")
			dialect.require("js.function.lambda", leftParenToken.getStart());
			expectOperator(JSOperator.LAMBDA, src, context);
			return finishFunctionBody(leftParenToken.getStart(), null, Collections.emptyList(), true, false, src, context);
		} else if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			//Lambda w/ 1 rest operator
			dialect.require("js.function.lambda", leftParenToken.getStart());
			dialect.require("js.parameter.rest", next.getStart());
			List<ParameterTree> param = reinterpretExpressionAsParameterList(parseSpread(next, src, context));
			expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
			expectOperator(JSOperator.LAMBDA, src, context);
			return finishFunctionBody(leftParenToken.getStart(), null, param, true, false, src, context);
		} else {
			ExpressionTree expr = parseAssignment(next, src, context);
			
			//There are multiple expressions here
			if ((next = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA)) != null) {
				List<Tree> expressions = new ArrayList<>();
				expressions.add(expr);
				
				next = src.nextToken();
				do {
					if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
						dialect.require("js.parameter.rest", leftParenToken.getStart());
						//Rest parameter. Must be lambda expression
						expressions.add(parseSpread(next, src, context));
						
						//Upgrade to lambda
						dialect.require("js.function.lambda", leftParenToken.getStart());
						List<ParameterTree> params = new ArrayList<>(expressions.size());
						for (ExpressionTree expression : (List<ExpressionTree>)(List<?>)expressions)
							params.add(reinterpretExpressionAsParameter(expression));
						//The rest parameter must be the last one
						expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
						expectOperator(JSOperator.LAMBDA, src, context);
						return this.finishFunctionBody(leftParenToken.getStart(), null, params, true, false, src, context);
					} else {
						final ExpressionTree expression = parseNextExpression(next, src, context);
						// Check for declared types (means its a lambda param)
						Token colonToken = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON);
						if (colonToken != null) {
							//Upgrade to lambda
							dialect.require("js.function.lambda", leftParenToken.getStart());
							List<ParameterTree> params = new ArrayList<>(expressions.size());
							for (ExpressionTree x : (List<ExpressionTree>)(List<?>)expressions)
								params.add(reinterpretExpressionAsParameter(x));
							
							if (expression.getKind() != Kind.IDENTIFIER)
								//TODO support destructured parameters
								throw new JSUnexpectedTokenException(colonToken);
							
							//Parse type declaration
							TypeTree type = parseType(src, context);
							
							params.add(new ParameterTreeImpl(expression.getStart(), expression.getEnd(), (IdentifierTree)expression, false, false, type, null));
							
							if ((next = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA)) != null)
								params.addAll(this.parseParameters(src, context));
							
							expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
							expectOperator(JSOperator.LAMBDA, src, context);
							
							return this.finishFunctionBody(leftParenToken.getStart(), null, params, true, false, src, context);
						}
						expressions.add(expression);
					}
					next = src.nextToken();
					if (!next.matches(TokenKind.OPERATOR, JSOperator.COMMA))
						break;
					next = src.nextToken();
				} while (!src.isEOF());
				
				//Ensure that it exited the loop with a closing paren
				context.pop();
				if (!next.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
					throw new JSUnexpectedTokenException(next);
				
				//Sequence, but not lambda
				return new ParenthesizedTreeImpl(leftParenToken.getStart(), next.getEnd(), new SequenceTreeImpl((List<ExpressionTree>)(List<?>)expressions));
			}
			context.pop();
			//Only one expression
			if (!(next = src.nextToken()).matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
				throw new JSUnexpectedTokenException(next);
			
			if ((next = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LAMBDA)) != null) {
				//Upgrade to lambda
				dialect.require("js.function.lambda", leftParenToken.getStart());
				List<ParameterTree> params = this.reinterpretExpressionAsParameterList(expr);
				return finishFunctionBody(leftParenToken.getStart(), null, params, true, false, src, context);
			}
			//Not a lambda, just some parentheses around some expression.
			return expr;
		}
	}
	
	protected UnaryTree parseSpread(Token spreadToken, JSLexer src, Context context) {
		spreadToken = expect(spreadToken, TokenKind.OPERATOR, JSOperator.SPREAD, src, context);
		dialect.require("js.operator.spread", spreadToken.getStart());
		final ExpressionTree expr = parseAssignment(null, src, context);
		return new UnaryTreeImpl(spreadToken.getStart(), expr.getEnd(), expr, Kind.SPREAD);
	}

	protected ExpressionTree parseNew(Token newKeywordToken, JSLexer src, Context context) {
		newKeywordToken = expect(newKeywordToken, TokenKind.KEYWORD, JSKeyword.NEW, src, context);
		Token t;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.PERIOD)) != null) {
			Token r = src.nextToken();
			if (context.inFunction() && r.matches(TokenKind.IDENTIFIER, "target"))
				//See developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/new.target
				return new BinaryTreeImpl(Tree.Kind.MEMBER_SELECT, new IdentifierTreeImpl(newKeywordToken.getStart(), newKeywordToken.getEnd(), "new"), new IdentifierTreeImpl(r));
			throw new JSUnexpectedTokenException(t);
		}
		final ExpressionTree callee = parseLeftSideExpression(null, src, context, false);
		final List<ExpressionTree> args;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null) {
			args = parseArguments(t, src, context);
		} else {
			args = Collections.emptyList();
		}
		return new NewTreeImpl(newKeywordToken.getStart(), src.getPosition(), callee, args);
	}
	
	protected VoidTree parseVoid(Token voidKeywordToken, JSLexer src, Context context) {
		voidKeywordToken = expect(voidKeywordToken, TokenKind.KEYWORD, JSKeyword.VOID, src, context);
		ExpressionTree expr = parseNextExpression(src, context);
		return new UnaryTreeImpl.VoidTreeImpl(voidKeywordToken.getStart(), src.getPosition(), expr);
	}
	
	protected ExpressionTree parseLeftSideExpression(Token t, JSLexer src, Context context, boolean allowCall) {
		if (t == null)
			t = src.nextToken();
		context.push().allowIn(true);
		if (!context.allowIn())
			throw new IllegalStateException();
		
		ExpressionTree expr;
		if (context.inFunction() && t.matches(TokenKind.KEYWORD, JSKeyword.SUPER))
			expr = parseSuper(t, src, context);
		else if (t.matches(TokenKind.KEYWORD, JSKeyword.NEW))
			expr = parseNew(t, src, context);
		else
			expr = parsePrimaryExpression(t, src, context);
		
		while (true) {
			if ((t = src.nextTokenIf(TokenKind.BRACKET, '[')) != null) {
				//Computed member access expressions
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = parseNextExpression(null, src, context.pushed());
				expect(TokenKind.BRACKET, ']', src, context);
				expr = new BinaryTreeImpl(t.getStart(), src.getPosition(), Kind.ARRAY_ACCESS, expr, property);
			} else if (allowCall && (t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null) {
				//Function call
				context.isBindingElement(false);
				context.isAssignmentTarget(false);
				List<ExpressionTree> arguments = parseArguments(t, src, context.pushed());
				expr = new FunctionCallTreeImpl(t.getStart(), src.getPosition(), expr, arguments);
			} else if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.PERIOD)) != null) {
				//Static member access
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = parseIdentifier(null, src, context.pushed());
				expr = new BinaryTreeImpl(t.getStart(), src.getPosition(), Kind.MEMBER_SELECT, expr, property);
			} else if ((t = src.nextTokenIf(TokenKind.TEMPLATE_LITERAL)) != null) {
				//TODO Tagged template literal
				throw new UnsupportedOperationException();
			} else {
				break;
			}
		}
		
		context.pop();
		return expr;
	}
	
	protected FunctionCallTree parseFunctionCall(ExpressionTree functionSelectExpression, Token openParenToken,
			JSLexer src, Context context) {
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		List<? extends ExpressionTree> arguments = parseArguments(openParenToken, src, context);
		return new FunctionCallTreeImpl(functionSelectExpression.getStart(), src.getPosition(), functionSelectExpression, arguments);
	}
	
	protected IdentifierTree parseIdentifier(Token identifierToken, JSLexer src, Context context) {
		identifierToken = expect(identifierToken, TokenKind.IDENTIFIER, src, context);
		return new IdentifierTreeImpl(identifierToken.getStart(), identifierToken.getEnd(), identifierToken.getValue());
	}
	
	protected ThisExpressionTree parseThis(Token thisKeywordToken, JSLexer src, Context ctx) {
		return new ThisExpressionTreeImpl(expect(thisKeywordToken, TokenKind.KEYWORD, JSKeyword.THIS, src, ctx));
	}
	
	protected SuperExpressionTree parseSuper(Token superKeywordToken, JSLexer src, Context context) {
		SuperExpressionTree result = new SuperExpressionTreeImpl(expect(superKeywordToken, TokenKind.KEYWORD, JSKeyword.SUPER, src, context));
		Token tmp = src.peek();
		if (!(tmp.matches(TokenKind.BRACKET, '[') || tmp.matches(TokenKind.OPERATOR, JSOperator.PERIOD)))
			throw new JSUnexpectedTokenException(tmp);
		return result;
	}
	
	//Function stuff

	FunctionExpressionTree finishFunctionBody(long startPos, IdentifierTree identifier, List<ParameterTree> parameters, boolean isLambda, boolean isGenerator, JSLexer src, Context ctx) {
		Token startBodyToken = src.nextToken();
		ctx.push().enterFunction();
		StatementTree body;
		if (startBodyToken.matches(TokenKind.BRACKET, '{')) {
			body = parseBlock(startBodyToken, src, ctx);
		} else {
			ExpressionTree expr = parseNextExpression(startBodyToken, src, ctx);
			//Unwrap parentheses
			while (expr.getKind() == Kind.PARENTHESIZED && ((ParenthesizedTree)expr).getExpression().getKind() != Kind.SEQUENCE)
				expr = parseNextExpression(startBodyToken, src, ctx);
			body = new ReturnTreeImpl(expr);
		}
		//TODO infer name from syntax
		FunctionExpressionTree result = new FunctionExpressionTreeImpl(startPos, body.getEnd(), parameters, null, true, body, ctx.isStrict(), isGenerator);
		ctx.pop();
		return result;
	}
	
	protected FunctionExpressionTree parseFunctionExpression(Token functionKeywordToken, JSLexer src, Context context) {
		functionKeywordToken = expect(functionKeywordToken, TokenKind.KEYWORD, src, context);
		boolean generator = functionKeywordToken.getValue() == JSKeyword.FUNCTION_GENERATOR;
		if (!generator && functionKeywordToken.getValue() != JSKeyword.FUNCTION)
			throw new JSUnexpectedTokenException(functionKeywordToken);
		
		IdentifierTree identifier = null;
		Token next = src.nextTokenIf(TokenKind.IDENTIFIER);
		if (next != null)
			identifier = new IdentifierTreeImpl(next);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = parseParameters(src, context);
		
		return finishFunctionBody(functionKeywordToken.getStart(), identifier, params, false, generator, src, context);
	}
	
	//Literals
	
	protected LiteralTree<?> parseLiteral(Token literalToken, JSLexer src, Context context) {
		if (literalToken == null)
			literalToken = src.nextToken();
		switch (literalToken.getKind()) {
			case STRING_LITERAL:
				return new StringLiteralTreeImpl(literalToken);
			case NUMERIC_LITERAL:
				return new NumericLiteralTreeImpl(literalToken);
			case BOOLEAN_LITERAL:
				return new BooleanLiteralTreeImpl(literalToken);
			case NULL_LITERAL:
				return new NullLiteralTreeImpl(literalToken);
			case TEMPLATE_LITERAL:
			case REGEX_LITERAL:
				//TODO finish parsing
				throw new UnsupportedOperationException();
			default:
				throw new JSUnexpectedTokenException(literalToken);
		}
	}
	
	protected ArrayLiteralTree parseArrayInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '[', src, context);
		
		ArrayList<ExpressionTree> values = new ArrayList<>();
		
		Token next;
		while (!((next = src.nextToken()).matches(TokenKind.BRACKET, ']') || src.isEOF())) {
			if (next.matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				values.add(null);
				continue;
			}

			if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD))
				values.add(parseSpread(next, src, context));
			else
				values.add(parseAssignment(next, src, context));
			
			if (!src.peek().matches(TokenKind.BRACKET, ']'))
				expect(TokenKind.OPERATOR, JSOperator.COMMA, src, context);
		}
		
		expect(next, TokenKind.BRACKET, ']');
		
		values.trimToSize();
		
		return new ArrayLiteralTreeImpl(startToken.getStart(), next.getEnd(), values);
	}
	
	protected ObjectLiteralTree parseObjectInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '{', src, context);
		ArrayList<? extends ObjectLiteralPropertyTree> properties = new ArrayList<>();
		Token next;
		while (!(next = src.nextToken()).matches(TokenKind.BRACKET, '}')) {
			
		}
		properties.trimToSize();
		return new ObjectLiteralTreeImpl(startToken.getStart(), next.getEnd(), properties);
	}
	
	//Unary ops
	
	protected ExpressionTree parseUnaryExpression(Token operatorToken, JSLexer src, Context context) {
		if (operatorToken == null)
			operatorToken = src.nextToken();
		Tree.Kind kind = null;
		boolean updates = false;
		switch (operatorToken.getKind()) {
			case KEYWORD:
				switch (operatorToken.<JSKeyword>getValue()) {
					case VOID:
						if (src.nextTokenIf(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON) != null)
							return new UnaryTreeImpl(operatorToken.getStart(), src.getPosition(), null, Tree.Kind.VOID);
						kind = Tree.Kind.VOID;
						break;
					case TYPEOF:
						kind = Tree.Kind.TYPEOF;
						break;
					case DELETE:
						kind = Tree.Kind.DELETE;
						break;
					case YIELD:
						kind = Tree.Kind.YIELD;
						break;
					case YIELD_GENERATOR:
						kind = Tree.Kind.YIELD_GENERATOR;
						break;
					default:
						break;
				}
				break;
			case OPERATOR:
				switch (operatorToken.<JSOperator>getValue()) {
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
					case SPREAD:
						return parseSpread(operatorToken, src, context);
					case INCREMENT:
						kind = Tree.Kind.PREFIX_INCREMENT;
						updates = true;
						break;
					case DECREMENT:
						kind = Tree.Kind.PREFIX_DECREMENT;
						updates = false;
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
		if (kind != null) {
			context.push();
			if (updates)
				context.isBindingElement(true);
			ExpressionTree expression = parseUnaryExpression(null, src, context);
			context.pop();
			if (updates) {
				//Check if the target can be modified
				if (!Validator.canBeAssigned(expression, dialect))
					throw new JSSyntaxException("Invalid right-hand side expression in " + kind + " expression", operatorToken.getStart());
			} else if (context.isStrict() && kind == Tree.Kind.DELETE && expression.getKind() == Tree.Kind.IDENTIFIER)
				throw new JSSyntaxException("Cannot delete unqualified identifier " + ((IdentifierTree)expression).getName() + " in strict mode", operatorToken.getStart());
			else if (kind == Tree.Kind.VOID)
				//Because it's a special little snowflake
				return new UnaryTreeImpl.VoidTreeImpl(operatorToken, expression);
			return new UnaryTreeImpl(operatorToken.getStart(), expression.getEnd(), expression, kind);
		} else {
			context.push();
			ExpressionTree result = parseLeftSideExpression(operatorToken, src, context, true);
			context.pop();
			operatorToken = src.nextTokenIf(t->(t.isOperator() && (t.getValue() == JSOperator.INCREMENT || t.getValue() == JSOperator.DECREMENT)));
			if (operatorToken != null)
				return parseUnaryPostfix(result, operatorToken, src, context);
			return result;
		}
	}
	
	protected UnaryTree parseUnaryPostfix(ExpressionTree expr, Token operatorToken, JSLexer src, Context context) {
		if (expr == null)
			expr = parseLeftSideExpression(null, src, context.pushed(), true);
		if (!Validator.canBeAssigned(expr, dialect))
			throw new JSSyntaxException("Invalid left-hand side expression in " + operatorToken.getKind() + " expression", expr.getStart());
		Kind kind;
		if (operatorToken.getValue() == JSOperator.INCREMENT)
			kind = Kind.POSTFIX_INCREMENT;
		else if (operatorToken.getValue() == JSOperator.DECREMENT)
			kind = Kind.POSTFIX_DECREMENT;
		else
			throw new JSUnexpectedTokenException(operatorToken, " is not a unary postfix operator.");
		return new UnaryTreeImpl(expr.getStart(), operatorToken.getEnd(), expr, kind);
	}
	
	public static class Context {
		ContextData data;
		// Fields that are global
		String scriptName;
		
		public Context() {
			data = new ContextData();
		}
		
		public Context(ContextData data) {
			this.data = data;
		}
		
		public Context setScriptName(String name) {
			this.scriptName = name;
			return this;
		}
		
		public String getScriptName() {
			return scriptName;
		}
		
		public Context push() {
			data = new ContextData(data);
			return this;
		}
		
		public Context pushed() {
			return new Context(new ContextData(data));
		}
		
		public Context pop() {
			data = data.parent;
			return this;
		}
		
		public boolean isStrict() {
			return data.isStrict;
		}
		
		public boolean allowReturn() {
			return data.inFunction || data.inGenerator;
		}
		
		public boolean allowIn() {
			return data.allowIn;
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
		
		public boolean inBinding() {
			return data.isBindingElement;
		}

		public boolean inFunction() {
			return data.inFunction;
		}

		public Context allowIn(boolean value) {
			data.allowIn = value;
			return this;
		}

		public boolean isAssignmentTarget() {
			return data.isAssignmentTarget;
		}

		/**
		 * Marks this level of the context as being in a function body. Allows
		 * the use of <code>return</code> statements.
		 * 
		 * @return self
		 */
		public Context enterFunction() {
			data.inFunction = true;
			return this;
		}

		/**
		 * Marks this level of the context as being in a switch statement.
		 * Allows the use of <code>break</code>
		 * 
		 * @return self
		 */
		public Context enterSwitch() {
			data.inSwitch = true;
			return this;
		}

		/**
		 * Marks this level of the context as being in a generator function.
		 * Allows the same expressions as being in a normal function, in
		 * addition to <code>yield</code> and <code>yield*</code>.
		 * 
		 * @return self
		 */
		public Context enterGenerator() {
			data.inGenerator = true;
			return this;
		}

		/**
		 * Marks this level of the context as being in a loop. Allows
		 * <code>break</code> and <break>continue</break> statements
		 * 
		 * @return self
		 */
		public Context enterLoop() {
			data.inLoop = true;
			return this;
		}

		public Context enterStrict() {
			data.isStrict = true;
			return this;
		}

		public Context isBindingElement(boolean value) {
			data.isBindingElement = value;
			return this;
		}

		public Context isAssignmentTarget(boolean value) {
			data.isAssignmentTarget = value;
			return this;
		}
		
		static class ContextData {
			boolean isStrict = false;
			/**
			 * Whether the parser is currently inside a function statement.
			 * Allows the <code>return</code> keyword.
			 */
			boolean inFunction = false;
			/**
			 * Whether the parser is currently inside a switch statement. Allows
			 * the <code>break</code> keyword.
			 */
			boolean inSwitch = false;
			boolean inGenerator = false;
			/**
			 * Whether the parser is currently inside a loop statement. Allows
			 * the <code>break</code> and <code>continue</code> statements.
			 */
			boolean inLoop = false;
			boolean isAssignmentTarget = false;
			boolean inNamedBlock = false;
			boolean isBindingElement = false;
			boolean allowIn = true;
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
				this.isBindingElement = parent.isBindingElement;
				this.allowIn = parent.allowIn;
				this.parent = parent;
			}
		}
	}
}
