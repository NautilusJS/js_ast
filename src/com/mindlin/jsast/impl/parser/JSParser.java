package com.mindlin.jsast.impl.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.AnyTypeTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayTypeTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentPatternTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTypeTree;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.BooleanLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.CaseTreeImpl;
import com.mindlin.jsast.impl.tree.CastTreeImpl;
import com.mindlin.jsast.impl.tree.CatchTreeImpl;
import com.mindlin.jsast.impl.tree.ClassDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.ClassPropertyTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.ComputedPropertyKeyTreeImpl;
import com.mindlin.jsast.impl.tree.ConditionalExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExportTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionCallTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionTypeTreeImpl;
import com.mindlin.jsast.impl.tree.GenericTypeTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTypeTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.ImportSpecifierTreeImpl;
import com.mindlin.jsast.impl.tree.ImportTreeImpl;
import com.mindlin.jsast.impl.tree.IndexTypeTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.InterfacePropertyTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceTypeTreeImpl;
import com.mindlin.jsast.impl.tree.KeyofTypeTreeImpl;
import com.mindlin.jsast.impl.tree.LabeledStatementTreeImpl;
import com.mindlin.jsast.impl.tree.MethodDefinitionTreeImpl;
import com.mindlin.jsast.impl.tree.NewTreeImpl;
import com.mindlin.jsast.impl.tree.NullLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectLiteralPropertyTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectPatternPropertyTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ParameterTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.RegExpLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ReturnTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceTreeImpl;
import com.mindlin.jsast.impl.tree.StringLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.SuperExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.SwitchTreeImpl;
import com.mindlin.jsast.impl.tree.ThisExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ThrowTreeImpl;
import com.mindlin.jsast.impl.tree.TryTreeImpl;
import com.mindlin.jsast.impl.tree.TupleTypeTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclaratorTreeImpl;
import com.mindlin.jsast.impl.tree.VoidTypeTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WithTreeImpl;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.ClassPropertyTree.AccessModifier;
import com.mindlin.jsast.tree.ClassPropertyTree.PropertyDeclarationType;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.EnumDeclarationTree;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionStatementTree;
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
import com.mindlin.jsast.tree.ObjectPatternPropertyTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
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
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.GenericTypeTree;

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
	
	private static Token expectEOL(JSLexer src, Context context) {
		Token t = src.nextToken();
		if (t.isEOS() || t.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON) || t.matches(TokenKind.SPECIAL, JSSpecialGroup.EOL))
			return t;
		throw new JSSyntaxException("Illegal token " + t + "; expected EOL");
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
		List<StatementTree> elements = new ArrayList<>();
		StatementTree value;
		Context context = new Context();
		context.setScriptName(unitName);
		while ((value = parseStatement(src, context)) != null)
			elements.add(value);
		SourceFile source = null;
		return new CompilationUnitTreeImpl(0, src.getPosition(), source, null, elements, false);
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
						return this.parseVariableDeclaration(token, src, context, false);
					case DEBUGGER:
						return this.parseDebugger(token, src, context);
					case DO:
						return this.parseDoWhileLoop(token, src, context);
					case AWAIT:
					case ENUM:
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
					case THIS:
						return parseNextExpression(token, src, context);
					case CASE:
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
				switch (token.<String>getValue()) {
					case "type":
						if (src.peek().getKind() != TokenKind.IDENTIFIER)
							break;
						//type statements not yet supported
						throw new UnsupportedOperationException(""+token.getStart());
					case "async":
						if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
							return this.parseFunctionExpression(token, src, context);
						break;
					default:
						break;
				}
				Token lookahead = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON);
				if (lookahead != null)
					return this.parseLabeledStatement(this.parseIdentifier(token, src, context), lookahead, src, context);
			}
			//Fallthrough intentional
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
				else
					throw new JSUnexpectedTokenException(token);
			case OPERATOR:
				if (token.getValue() == JSOperator.LEFT_PARENTHESIS)
					return this.parseNextExpression(token, src, context);
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
		Token next = src.nextToken();
		switch (next.getKind()) {
			case KEYWORD: {
				switch (next.<JSKeyword>getValue()) {
					case BREAK:
					case CONTINUE:
						return this.parseGotoStatement(next, src, context);
					case DEBUGGER:
						return this.parseDebugger(next, src, context);
					case CLASS:
						return this.parseClass(next, src, context);
					case CONST:
					case LET:
					case VAR:
						return this.parseVariableDeclaration(next, src, context, false);
					case DO:
						return this.parseDoWhileLoop(next, src, context);
					case AWAIT:
					case ENUM:
						throw new UnsupportedOperationException();
					case EXPORT:
						return this.parseExportStatement(next, src, context);
					case FOR:
						return this.parseForStatement(next, src, context);
					case FUNCTION:
					case FUNCTION_GENERATOR:
						return this.finishExpressionStatement(this.parseFunctionExpression(next, src, context), src, context);
					case IF:
						return this.parseIfStatement(next, src, context);
					case IMPORT:
						return this.parseImportStatement(next, src, context);
					case INTERFACE:
						return this.parseInterface(next, src, context);
					case RETURN:
					case THROW:
						return this.parseUnaryStatement(next, src, context);
					case SWITCH:
						return this.parseSwitchStatement(next, src, context);
					case TRY:
						return this.parseTryStatement(next, src, context);
					case VOID:
						return this.parseVoid(next, src, context);
					case WHILE:
						return this.parseWhileLoop(next, src, context);
					case WITH:
						return this.parseWithStatement(next, src, context);
					case DELETE:
					case NEW:
					case TYPEOF:
					case YIELD:
					case YIELD_GENERATOR:
					case SUPER:
					case THIS:
						return this.parseExpressionStatement(next, src, context);
					case CASE:
					case FINALLY:
					case DEFAULT:
					case ELSE:
					case EXTENDS:
					case IN:
					case INSTANCEOF:
					default:
						throw new JSSyntaxException("Unexpected keyword " + next.getValue(), next.getStart());
				}
			}
			case BRACKET:
				if (next.<Character>getValue() == '{')
					return this.parseBlock(next, src, context);
				else
					return this.parseExpressionStatement(next, src, context);
			case OPERATOR:
				return this.parseExpressionStatement(next, src, context);
			case IDENTIFIER: {
				switch (next.<String>getValue()) {
					case "type":
						if (src.peek().getKind() != TokenKind.IDENTIFIER)
							break;
						//type statements not yet supported
						throw new UnsupportedOperationException("Type statements not yet supported: " + next.getStart());
					case "async":
						if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
							return this.finishExpressionStatement(this.parseFunctionExpression(next, src, context), src, context);
						break;
					default:
						break;
				}
				Token lookahead = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON);
				if (lookahead != null)
					return this.parseLabeledStatement(this.parseIdentifier(next, src, context), lookahead, src, context);
			}
			//Fallthrough intentional
			case BOOLEAN_LITERAL:
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case REGEX_LITERAL:
			case TEMPLATE_LITERAL:
			case NULL_LITERAL:
				return this.parseExpressionStatement(next, src, context);
			case SPECIAL:
				switch (next.<JSSpecialGroup>getValue()) {
					case EOF:
						return null;
					case EOL:
					case SEMICOLON:
						return new EmptyStatementTreeImpl(next);
					default:
						break;
				}
			case COMMENT:
				break;
			default:
				break;
			
		}
		return null;
	}
	
	protected ExpressionStatementTree parseExpressionStatement(Token token, JSLexer src, Context context) {
		return finishExpressionStatement(this.parseNextExpression(token, src, context), src, context);
	}
	
	protected ExpressionStatementTree finishExpressionStatement(ExpressionTree expression, JSLexer src, Context context) {
		long end = expression.getEnd();
		if (src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
			end = src.getPosition();
		return new ExpressionStatementTreeImpl(expression.getStart(), end, expression);
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
				case IN:
					return Tree.Kind.IN;
				case INSTANCEOF:
					return Tree.Kind.INSTANCEOF;
				default:
					break;
			}
		throw new JSSyntaxException(token + "is not a binary operator", token.getStart());
	}
	
	List<ParameterTree> reinterpretExpressionAsParameterList(ExpressionTree expr) {
		//Unwrap parentheses
		if (expr.getKind() == Kind.PARENTHESIZED)
			return reinterpretExpressionAsParameterList(((ParenthesizedTree)expr).getExpression());
		
		//Convert a sequence tree to a list of parameters
		if (expr.getKind() == Kind.SEQUENCE) {
			List<ExpressionTree> exprs= ((SequenceTree)expr).getExpressions();
			ArrayList<ParameterTree> params = new ArrayList<>(exprs.size());
			for (ExpressionTree child : exprs)
				params.add(reinterpretExpressionAsParameter(child));
			params.trimToSize();
			return params;
		}
		
		//Return parameter list of size one
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
				if (t.<String>getValue().equals("async") && src.peek().matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
					//Async function
					return this.parseFunctionExpression(t, src, context);
				else if (t.<String>getValue().equals("abstract") && src.peek().matches(TokenKind.KEYWORD, JSKeyword.CLASS))
					//Abstract class
					return this.parseClass(t, src, context);
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
					case LEFT_PARENTHESIS: {
						context.isBindingElement(false);
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseGroupExpression(t, src, context);
						context.inheritCoverGrammar();
						return result;
					}
					case DIVISION:
					case DIVISION_ASSIGNMENT: {
						context.isAssignmentTarget(false);
						context.isBindingElement(false);
						Token regex = src.finishRegExpLiteral(t);
						return this.parseLiteral(regex, src, context);
					}
					default:
						throw new JSUnexpectedTokenException(t);
				}
			case BRACKET:
				switch ((char)t.getValue()) {
					case '[': {
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseArrayInitializer(t, src, context);
						context.inheritCoverGrammar();
						return result;
					}
					case '{': {
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseObjectInitializer(t, src, context);
						context.inheritCoverGrammar();
						return result;
					}
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
					case INTERFACE:
						return this.parseInterface(t, src, context);
					default:
						throw new JSUnexpectedTokenException(t);
				}
			default:
				throw new JSUnexpectedTokenException(t);
		}
	}
	
	protected List<TypeTree> parseGenericArguments(Token openChevron, JSLexer src, Context context) {
		if (openChevron == null)
			if ((openChevron = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LESS_THAN)) == null)
				return Collections.emptyList();
		
		//No generic args (empty '<>')
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.GREATER_THAN))
			return Collections.emptyList();
		
		ArrayList<TypeTree> arguments = new ArrayList<>();
		
		//Read in arguments
		do {
			arguments.add(this.parseType(src, context));
		} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
		
		expect(TokenKind.OPERATOR, JSOperator.GREATER_THAN, src, context);
		
		arguments.trimToSize();
		return arguments;
	}
	
	/**
	 * Parse a list of generic type parameters
	 * @param openChevron
	 * @param src
	 * @param context
	 * @return
	 */
	protected List<GenericTypeTree> parseGenericParameters(Token openChevron, JSLexer src, Context context) {
		openChevron = expect(openChevron, TokenKind.OPERATOR, JSOperator.LESS_THAN, src, context);
		
		ArrayList<GenericTypeTree> generics = new ArrayList<>();
		
		//There are no generics (empty '<>')
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.GREATER_THAN))
			return generics;
		
		do {
			IdentifierTree identifier = this.parseIdentifier(null, src, context);
			
			context.registerGenericParam(identifier.getName(), identifier.getStart());
			
			TypeTree supertype = null;
			if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.EXTENDS))
				supertype = this.parseType(src, context);
			
			generics.add(new GenericTypeTreeImpl(identifier.getStart(), src.getPosition(), false, identifier, supertype));
		} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
		
		expect(TokenKind.OPERATOR, JSOperator.GREATER_THAN, src, context);
		
		generics.trimToSize();
		return generics;
	}
	
	protected List<ExpressionTree> parseArguments(Token t, JSLexer src, Context context) {
		 t = expect(t, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		 t = src.nextToken();
		 if (t.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			 return Collections.emptyList();
		 List<ExpressionTree> result = new ArrayList<>();
		 while (true) {
			 ExpressionTree expr;
			 if (t.matches(TokenKind.OPERATOR, JSOperator.SPREAD))
				 expr = parseSpread(t, src, context);
			 else
				 expr = parseAssignment(t, src, context.coverGrammarIsolated());
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
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) {
			expect(TokenKind.KEYWORD, JSKeyword.FROM, src, context);
		} else if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.DEFAULT)) {
			// TODO finish
			throw new UnsupportedOperationException();
		} else if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS)) {
			// TODO finish
			throw new UnsupportedOperationException();
		}
		ExpressionTree expr = parseNextExpression(src, context);
		
		//Optionally consume semicolon
		src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON);
		
		return new ExportTreeImpl(expr.getStart(), src.getPosition(), expr);
	}
	
	TypeTree reinterpretAsType(Token typeToken) {
		if (typeToken.matches(TokenKind.KEYWORD, JSKeyword.VOID))
			return new VoidTypeTreeImpl(typeToken);
		if (typeToken.getKind() == TokenKind.IDENTIFIER) {
			switch (typeToken.getValue().toString()) {
				case "any":
				case "never":
			}
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
	
	/**
	 * @param inFor If this variable declaration is in a for loop
	 */
	@JSKeywordParser({ JSKeyword.CONST, JSKeyword.LET, JSKeyword.VAR })
	protected VariableDeclarationTree parseVariableDeclaration(Token keywordToken, JSLexer src, Context context, boolean inFor) {
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
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT))
				initializer = parseAssignment(null, src, context.coverGrammarIsolated());
			else if (isConst)
				//No initializer
				throw new JSSyntaxException("Missing initializer in constant declaration", identifier.getStart());
			
			declarations.add(new VariableDeclaratorTreeImpl(identifier.getStart(), src.getPosition(), new IdentifierTreeImpl(identifier), type, initializer));
		} while (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		
		if (!inFor)
			expectEOL(src, context);
		
		return new VariableDeclarationTreeImpl(keywordToken.getStart(), src.getPosition(), isScoped, isConst, declarations);
	}
	
	protected ExpressiveStatementTree parseUnaryStatement(Token keywordToken, JSLexer src, Context context) {
		keywordToken = expect(keywordToken, TokenKind.KEYWORD, src, context);
		if (!(keywordToken.getValue() == JSKeyword.RETURN || keywordToken.getValue() == JSKeyword.THROW))
			throw new JSUnexpectedTokenException(keywordToken);
		ExpressionTree expr;
		if (keywordToken.getValue() == JSKeyword.RETURN && src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
			expr = new UnaryTreeImpl.VoidTreeImpl(src.getPosition(), src.getPosition(), null);
		else
			expr = parseNextExpression(src, context);
		
		expectEOL(src, context);
		
		if (keywordToken.getValue() == JSKeyword.RETURN)
			return new ReturnTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
		else
			return new ThrowTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
	}

	protected LabeledStatementTree parseLabeledStatement(IdentifierTree identifier, Token colon, JSLexer src, Context context) {
		StatementTree statement = this.parseStatement(src, context);
		context.registerStatementLabel(identifier.getName(), identifier.getStart());
		return new LabeledStatementTreeImpl(identifier.getStart(), src.getPosition(), identifier, statement);
	}
	
	//Type structures
	
	/**
	 * Parse a class declaration
	 */
	@JSKeywordParser(JSKeyword.CLASS)
	protected ClassDeclarationTree parseClass(Token classKeywordToken, JSLexer src, Context context) {
		if (classKeywordToken == null)
			classKeywordToken = src.nextToken();
		
		final long classStartPos = classKeywordToken.getStart();
		
		//Support abstract classes
		boolean isClassAbstract = false;
		if (classKeywordToken.matches(TokenKind.IDENTIFIER, "abstract")) {
			classKeywordToken = src.nextToken();
			isClassAbstract = true;
		}
		
		classKeywordToken = expect(classKeywordToken, TokenKind.KEYWORD, JSKeyword.CLASS, src, context);
		
		dialect.require("js.class", classKeywordToken.getStart());
		
		//Read optional class identifier
		IdentifierTree classIdentifier = null;
		Token next = src.nextToken();
		List<GenericTypeTree> generics = Collections.emptyList();
		if (next.isIdentifier()) {
			classIdentifier = this.parseIdentifier(next, src, context);
			next = src.nextToken();
			
			//Parse generics
			if (next.matches(TokenKind.OPERATOR, JSOperator.LESS_THAN)) {
				generics = parseGenericParameters(next, src, context);
				next = src.nextToken();
			}
		}
		
		//Read 'extends' and 'implements' clauses
		TypeTree superClass = null;
		List<TypeTree> interfaces = new ArrayList<>();
		//We don't care in which order the 'extends' and 'implements' clauses come in
		for (int i = 0; i < 2 && next.isKeyword(); i++) {
			if (next.matches(TokenKind.KEYWORD, JSKeyword.EXTENDS)) {
				if (superClass != null)
					throw new JSSyntaxException("Classes may not extend multiple classes", next.getStart(), next.getEnd());
				superClass = this.parseType(src, context);
				next = src.nextToken();
			}
			if (next.matches(TokenKind.KEYWORD, JSKeyword.IMPLEMENTS)) {
				if (!interfaces.isEmpty())
					throw new JSUnexpectedTokenException(next);
				//We can add multiple interfaces here
				do {
					interfaces.add(parseType(src, context));
				} while ((next = src.nextToken()).matches(TokenKind.OPERATOR, JSOperator.COMMA));
			}
		}
		if (superClass != null || !interfaces.isEmpty())
			dialect.require("js.class.inheritance", src.getPosition());//TODO fix position
		
		//Read class body
		expect(next, TokenKind.BRACKET, '{', src, context);
		ArrayList<ClassPropertyTree<?>> properties = new ArrayList<>();
		while (!(next = src.nextToken()).matches(TokenKind.BRACKET, '}')) {
			//Start position of our next index
			final long propertyStartPos = next.getStart();
			
			//Aspects of property
			PropertyDeclarationType methodType = null;
			AccessModifier accessModifier = null;//Defaults to PUBLIC
			boolean readonly = false;//Readonly modifier
			boolean generator = false;
			boolean isStatic = false;
			boolean isPropertyAbstract = false;
			Token modifierToken = null;//Store token of modifier for later error stuff
			
			//We can have up to 3 modifiers (ex. 'public static readonly')
			for (int i = 0; i < 3; i++) {
				//Check for 'readonly' modifier
				if (next.matches(TokenKind.IDENTIFIER, "readonly")) {
					if (readonly)//Readonly already set
						throw new JSUnexpectedTokenException(next);
					readonly = true;
				} else if (next.matches(TokenKind.IDENTIFIER, "abstract")) {
					//Check for 'abstract' keyword
					if (isPropertyAbstract)
						throw new JSSyntaxException("Can't have an abstract field in a non-abstract class", next.getStart(), next.getEnd());
					if (isPropertyAbstract)
						throw new JSUnexpectedTokenException(next);
					isPropertyAbstract = true;
				} else if (next.isKeyword()) {
					JSKeyword keyword = next.getValue();
					
					if (keyword == JSKeyword.STATIC) {
						if (isStatic)
							throw new JSUnexpectedTokenException(next);
						dialect.require("js.class.static", next.getStart());
						isStatic = true;
					} else if (keyword == JSKeyword.PUBLIC || next.getValue() == JSKeyword.PROTECTED || next.getValue() == JSKeyword.PRIVATE) {
						//Check that there weren't other access modifiers.
						if (accessModifier != null)
							throw new JSUnexpectedTokenException(next);
						
						if (keyword == JSKeyword.PUBLIC)
							accessModifier = AccessModifier.PUBLIC;
						if (keyword == JSKeyword.PROTECTED)
							accessModifier = AccessModifier.PROTECTED;
						else if (keyword == JSKeyword.PRIVATE)
							accessModifier = AccessModifier.PRIVATE;
					} else {
						//next isn't a modifier
						break;
					}
				} else {
					//next isn't a modifier
					break;
				}
				next = src.nextToken();
			}
			
			//Default accessModifier to PUBLIC if not set
			if (accessModifier == null)
				accessModifier = AccessModifier.PUBLIC;
			
			//Now check if it's a generator/getter/setter
			if (next.matches(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) {
				//Generator function
				dialect.require("js.method.generator", next.getStart());
				generator = true;
				methodType = PropertyDeclarationType.METHOD;
				
				modifierToken = next;
				next = src.nextToken();
			} else if ((next.matches(TokenKind.IDENTIFIER, "get") || next.matches(TokenKind.IDENTIFIER, "set")) && (src.peek().getKind() == TokenKind.IDENTIFIER || src.peek().matches(TokenKind.BRACKET, '['))) {
				//Getter/setter method
				dialect.require("js.accessor", next.getStart());
				methodType = next.getValue().equals("set") ? PropertyDeclarationType.SETTER : PropertyDeclarationType.GETTER;
				
				modifierToken = next;
				next = src.nextToken();
			}
			
			//Parse property key
			//I'm not sure if this is *correct* js/ts, but let's go with it.
			ObjectPropertyKeyTree key;
			boolean computed = false;
			if (next.isIdentifier()) {
				//The key is an identifier
				key = parseIdentifier(next, null, context);
			} else if (next.getKind() == TokenKind.STRING_LITERAL || next.getKind() == TokenKind.NUMERIC_LITERAL) {
				//We have a string or number as an identifier. Should this work? No. But does this work...
				key = (ObjectPropertyKeyTree) parseLiteral(next, null, context);
			} else if (next.matches(TokenKind.BRACKET, '[')) {
				//ES6 computed ID. Also should probably be illegal
				computed = true;
				ExpressionTree expr = parseNextExpression(src, context);
				Token closeBracket = expect(TokenKind.BRACKET, ']', src, context);
				key = new ComputedPropertyKeyTreeImpl(next.getStart(), closeBracket.getEnd(), expr);
			} else {
				throw new JSUnexpectedTokenException(next);
			}
			
			ClassPropertyTree<?> property;
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				//Some type of method
				
				if (!computed && key.getKind() == Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("constructor")) {
					//Promote to constructor
					if (methodType != null || generator) {
						String modifierName;
						if (generator)
							modifierName = "generator";
						else if (methodType == PropertyDeclarationType.GETTER)
							modifierName = "getter";
						else if (methodType == PropertyDeclarationType.SETTER)
							modifierName = "setter";
						else
							modifierName = "[unknown]";
						
						throw new JSSyntaxException("Modifier '" + modifierName + "' not allowed in constructor declaration", modifierToken.getStart(), modifierToken.getEnd());
					}
					dialect.require("js.class.constructor", key.getStart());
					methodType = PropertyDeclarationType.CONSTRUCTOR;
				} else if (methodType == null) {//It's a not a getter/setter
					methodType = PropertyDeclarationType.METHOD;
				}
				
				//Parse method
				List<ParameterTree> params = this.parseParameters(src, context);
				expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
				
				TypeTree returnType = this.parseTypeMaybe(src, context, false);
				
				FunctionExpressionTree fn = null;
				if (!isPropertyAbstract)
					fn = this.finishFunctionBody(propertyStartPos, false, key.getKind() == Kind.IDENTIFIER ? ((IdentifierTree)key) : null, params, returnType, false, generator, src, context);
				
				FunctionTypeTree functionType = new FunctionTypeTreeImpl(propertyStartPos, src.getPosition(), true, params, Collections.emptyList(), returnType);
				
				//Consume semicolon at end
				
				property = new MethodDefinitionTreeImpl(propertyStartPos, src.getPosition(), accessModifier, isPropertyAbstract, readonly, isStatic, methodType, key, functionType, fn);
			} else if (methodType != null || generator || isPropertyAbstract || (key.getKind() == Tree.Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("constructor"))) {
				//TODO also check for fields named 'new'?
				throw new JSSyntaxException("Key " + key + " must be a method.", key.getStart(), key.getEnd());
			} else {
				//Field with (optional) type
				TypeTree fieldType = this.parseTypeMaybe(src, context, false);
				//Optional value expression
				ExpressionTree value = null;
				if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT))
					value = this.parseAssignment(null, src, context);
				//Fields end with a semicolon
				src.expect(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON);
				property = new ClassPropertyTreeImpl<ExpressionTree>(propertyStartPos, src.getPosition(), accessModifier, readonly, isStatic, methodType, key, fieldType, value);
			}
			
			properties.add(property);
			
			src.nextTokenIf(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON);
		}
		expect(next, TokenKind.BRACKET, '}', src, context);
		return new ClassDeclarationTreeImpl(classStartPos, src.getPosition(), isClassAbstract, classIdentifier, generics, superClass, interfaces, properties);
	}
	
	/**
	 * Parse the body of an interface, whether it be inline or a declaration
	 * @param src
	 * @param context
	 * @return
	 */
	List<InterfacePropertyTree> parseInterfaceBody(JSLexer src, Context context) {
		ArrayList<InterfacePropertyTree> properties = new ArrayList<>();
		Token next;
		while (!(next = src.nextToken()).matches(TokenKind.BRACKET, '}')) {
			long start = next.getStart();
			
			boolean readonly = false;
			IdentifierTree propname;
			boolean optional;
			TypeTree type;
			
			if (next.matches(TokenKind.IDENTIFIER, "readonly")) {
				//Starts with 'readonly' keyword
				readonly = true;
				next = src.nextToken();
			}
			
			if (next.isIdentifier()) {
				//Is simple identifier => property/method
				propname = new IdentifierTreeImpl(next);
				optional = src.nextTokenIs(TokenKind.OPERATOR, JSOperator.QUESTION_MARK);
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
			} else if (next.matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				//Parse function type
				List<ParameterTree> params = this.parseParameters(src, context);
				expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
				TypeTree returnType = this.parseTypeMaybe(src, context, false);
				
				type = new FunctionTypeTreeImpl(next.getStart(), src.getPosition(), false, params, null, returnType);
				optional = false;
				propname = null;//Correct?
			} else {
				throw new JSUnexpectedTokenException(next);
			}
			
			//Optionally consume semicolon at end
			src.nextTokenIf(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON);
			
			properties.add(new InterfacePropertyTreeImpl(start, src.getPosition(), readonly, propname, optional, type));
		}
		
		expect(next, TokenKind.BRACKET, '}');
		
		if (properties.isEmpty())
			return Collections.emptyList();
		properties.trimToSize();
		return properties;
	}
	
	/**
	 * Parse an interface declaration
	 */
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
		List<InterfacePropertyTree> properties = parseInterfaceBody(src, context);
		
		return new InterfaceDeclarationTreeImpl(interfaceKeywordToken.getStart(), src.getPosition(), name, superClasses, properties);
	}
	
	/**
	 * Parse an enum declaration
	 */
	protected EnumDeclarationTree parseEnum(Token enumKeywordToken, JSLexer src, Context context) {
		//TODO finish
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Parse a type, if unknown whether a type declaration follows the current statement.
	 * Uses the colon (<kbd>:</kbd>) to detect if there should be a type.
	 * @param src The source
	 * @param context
	 * @param canBeFunction Whether the parsed type can be a function
	 * @return
	 */
	protected TypeTree parseTypeMaybe(JSLexer src, Context context, boolean canBeFunction) {
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COLON)) {
			if (canBeFunction && src.peek().matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS))
				return parseFunctionType(src, context);
			return null;
		}
		return parseType(src, context);
	}
	
	protected TypeTree parseFunctionType(JSLexer src, Context context) {
		//TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected TypeTree parseImmediateType(JSLexer src, Context context) {
		Token startToken = src.nextToken();
		if (startToken.matches(TokenKind.KEYWORD, JSKeyword.THIS)) {
			//'this' type
			IdentifierTree identifier = new IdentifierTreeImpl(startToken);
			//No generics on 'this'
			return new IdentifierTypeTreeImpl(identifier.getStart(), startToken.getEnd(), false, identifier, Collections.emptyList());
		} else if (startToken.isIdentifier()) {
			//Check for 'keyof X'
			if ("keyof".matches(startToken.getValue())) {
				TypeTree baseType = parseType(src, context);
				return new KeyofTypeTreeImpl(startToken.getStart(), baseType.getEnd(), false, baseType);
			}
			
			//Array<X> => X[]
			if ("Array".matches(startToken.getValue())) {
				List<TypeTree> arrayGenericArgs = this.parseGenericArguments(null, src, context);
				if (arrayGenericArgs.size() > 1)
					throw new JSSyntaxException("Cannot have more than one type for Array", arrayGenericArgs.get(2).getStart());
				
				TypeTree arrayBaseType = null;
				if (arrayGenericArgs.size() == 1)
					arrayBaseType = arrayGenericArgs.get(0);
				else
					//Fall back on 'any[]'
					arrayBaseType = new AnyTypeTreeImpl(-1, -1, true);
				
				return new ArrayTypeTreeImpl(startToken.getStart(), src.getPosition(), false, arrayBaseType);
			}
			
			IdentifierTree identifier = new IdentifierTreeImpl(startToken);
			
			List<TypeTree> generics = this.parseGenericArguments(null, src, context);
			
			return  new IdentifierTypeTreeImpl(identifier.getStart(), startToken.getEnd(), false, identifier, generics);
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.VOID)) {
			return new VoidTypeTreeImpl(startToken);
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION)) {
			//Function
			return parseFunctionType(src, context);
		} else if (startToken.matches(TokenKind.BRACKET, '{')) {
			//Inline interface (or object type '{}')
			List<InterfacePropertyTree> properties = this.parseInterfaceBody(src, context);
			return new InterfaceTypeTreeImpl(startToken.getStart(), src.getPosition(), false, properties);
		} else if (startToken.matches(TokenKind.BRACKET, '[')) {
			//Tuple (of array type '[]')
			//TODO fix for '[]'
			List<TypeTree> slots;
			if (!src.nextTokenIs(TokenKind.BRACKET, ']')) {
				slots = new ArrayList<>();
				do {
					slots.add(parseType(src, context));
				} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			} else {
				slots = Collections.emptyList();
			}
			
			Token endToken = expect(TokenKind.BRACKET, ']', src, context);
			return new TupleTypeTreeImpl(startToken.getStart(), endToken.getEnd(), false, slots);
		} else if (startToken.isLiteral()) {
			
			//String literal
		} else {
			throw new JSUnexpectedTokenException(startToken);
		}
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Parse a type declaration
	 */
	protected TypeTree parseType(JSLexer src, Context context) {
		//Support parentheses
		TypeTree type;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
			//Recurse
			type = parseType(src, context);
			expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
		} else {
			type = parseImmediateType(src, context);
		}
		
		//Support array types in form of 'X[]'
		while (src.nextTokenIs(TokenKind.BRACKET, '[')) {
			Token endToken = expect(TokenKind.BRACKET, ']', src, context);
			type = new ArrayTypeTreeImpl(type.getStart(), endToken.getEnd(), false, type);
		}
		
		//See if it's a union/intersection type
		Token next = src.nextTokenIf(TokenPredicate.TYPE_CONTINUATION);
		if (next == null)
			return type;
		boolean union = next.getValue() == JSOperator.BITWISE_OR;
		TypeTree left = type;
		TypeTree right = parseType(src, context);
		return new BinaryTypeTree(left.getStart(), right.getEnd(), false, left, union ? Kind.TYPE_UNION : Kind.TYPE_INTERSECTION, right);
	}
	
	
	//Control flows
	
	protected DebuggerTree parseDebugger(Token debuggerKeywordToken, JSLexer src, Context context) {
		debuggerKeywordToken = expect(debuggerKeywordToken, TokenKind.KEYWORD, JSKeyword.DEBUGGER, src, context);
		expectEOL(src, context);
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	protected BlockTree parseBlock(Token openBraceToken, JSLexer src, Context context) {
		openBraceToken = expect(openBraceToken, TokenKind.BRACKET, '{', src, context);
		List<StatementTree> statements = new LinkedList<>();
		Token t;
		while ((t = src.nextTokenIf(TokenKind.BRACKET, '}')) == null)
			statements.add(parseStatement(src, context));
		expect(t, '}');
		return new BlockTreeImpl(openBraceToken.getStart(), src.getPosition(), statements);
	}
	
	/**
	 * Parse a {@code break} or {@code continue} statement, with optional label.
	 */
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
		StatementTree thenStatement = parseStatement(src, context);
		StatementTree elseStatement = null;
		Token next = src.nextTokenIf(TokenKind.KEYWORD, JSKeyword.ELSE);
		if (next != null) {
			// This if statement isn't really needed, but it speeds up 'else if'
			// statements by a bit, and else if statements are more common than
			// else statements (IMHO)
			if ((next = src.nextTokenIf(TokenKind.KEYWORD, JSKeyword.IF)) != null)
				elseStatement = parseIfStatement(next, src, context);
			else
				elseStatement = parseStatement(src, context);
		} else {
			elseStatement = new EmptyStatementTreeImpl(src.getPosition(), src.getPosition());
		}
		return new IfTreeImpl(ifKeywordToken.getStart(), src.getPosition(), expression, thenStatement, elseStatement);
	}
	
	protected SwitchTree parseSwitchStatement(Token switchKeywordToken, JSLexer src, Context context) {
		switchKeywordToken = expect(switchKeywordToken, TokenKind.KEYWORD, JSKeyword.SWITCH, src, context);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		src.expect(TokenKind.BRACKET, '{');
		
		ArrayList<CaseTree> cases = new ArrayList<>();
		Token next;
		while ((next = src.nextToken()).isKeyword()) {
			ExpressionTree caseExpr;
			if (next.getValue() == JSKeyword.CASE)
				caseExpr = this.parseNextExpression(src, context);
			else if (next.getValue() == JSKeyword.DEFAULT)
				caseExpr = null;
			else
				throw new JSUnexpectedTokenException(next);
			src.expect(JSOperator.COLON);
			
			//Parse statements in case body
			ArrayList<StatementTree> statements = new ArrayList<>();
			while (true) {
				Token lookahead = src.peek();
				if (lookahead.matches(TokenKind.KEYWORD, JSKeyword.CASE) || lookahead.matches(TokenKind.KEYWORD, JSKeyword.DEFAULT) || lookahead.matches(TokenKind.BRACKET, '}'))
						break;
				statements.add(parseStatement(src, context));
			}
			statements.trimToSize();
			
			cases.add(new CaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		expect(next, TokenKind.BRACKET, '}');
		cases.trimToSize();
		
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	/**
	 * Parse a try/catch, try/finally, or try/catch/finally statement
	 */
	protected TryTree parseTryStatement(Token tryKeywordToken, JSLexer src, Context context) {
		tryKeywordToken = expect(tryKeywordToken, TokenKind.KEYWORD, JSKeyword.TRY, src, context);
		
		//Read the block that is in the try part
		BlockTree tryBlock = parseBlock(null, src, context);
		
		//Read all catch blocks
		ArrayList<CatchTree> catchBlocks = new ArrayList<>();
		
		Token next;
		while ((next = src.nextTokenIf(TokenKind.IDENTIFIER, "catch")) != null) {
			expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
			IdentifierTree param = parseIdentifier(src.nextToken(), src, context);
			
			//Optional param type
			TypeTree type = this.parseTypeMaybe(src, context, false);
			
			expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
			BlockTree block = parseBlock(null, src, context);
			catchBlocks.add(new CatchTreeImpl(next.getStart(), block.getEnd(), block, param, type));
		}

		//Optional finally block (must come after any & all catch blocks)
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
		
		//Parse loop statement
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
		
		expect(TokenKind.KEYWORD, JSKeyword.WHILE, src, context);
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		ExpressionTree condition = parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		expectEOL(src, context);
		
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
			VariableDeclarationTree declarations = parseVariableDeclaration(next, src, context, true);
			context.pop();
			
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				
				if (declarations.getDeclarations().size() != 1)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Must have 1 binding", next.getStart());
				if (declarations.getDeclarations().get(0).getIntitializer() != null)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Variable may not have an initializer", declarations.getDeclarations().get(0).getIntitializer().getStart());
				
				return parsePartialForEachLoopTree(forKeywordToken, declarations, isOf, src, context);
			}
			initializer = declarations;
		} else {
			context.push();
			context.allowIn(false);
			ExpressionTree expr = this.parseNextExpression(next, src, context);
			context.pop();
			
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				PatternTree left = this.reinterpretExpressionAsPattern(expr);
				return this.parsePartialForEachLoopTree(forKeywordToken, left, isOf, src, context);
			}
			
			initializer = new ExpressionStatementTreeImpl(expr);
		}
		
		expectEOL(src, context);
		
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
		ExpressionTree condition = null;
		if (!src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)) {
			condition = parseNextExpression(src, context);
			expectSemicolon(src, context);
		}
		ExpressionTree update = src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS) ? null : parseNextExpression(src, context);
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
	protected ForEachLoopTree parsePartialForEachLoopTree(Token forKeywordToken, PatternTree pattern, boolean isForEach, JSLexer src, Context context) {
		final ExpressionTree right = isForEach ? this.parseAssignment(null, src, context) : this.parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		StatementTree statement = this.parseStatement(src, context);
		return new ForEachLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), pattern, isForEach, right, statement);
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
		ExpressionTree result = this.parseAssignment(t, src, context.coverGrammarIsolated());
		
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
			return result;
		
		ArrayList<ExpressionTree> expressions = new ArrayList<>();
		expressions.add(result);
		
		do {
			expressions.add(parseAssignment(null, src, context.coverGrammarIsolated()));
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
		
		context.isolateCoverGrammar();
		final ExpressionTree expr = this.parseUnaryExpression(t, src, context);
		context.inheritCoverGrammar();
		
		Token operatorToken = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.EXPONENTIATION);
		if (operatorToken == null)
			return expr;
		context.isAssignmentTarget(false).isBindingElement(false).push();
		final ExpressionTree right = parseExponentiation(null, src, context.coverGrammarIsolated());
		context.pop();
		return new BinaryTreeImpl(expr.getStart(), right.getEnd(), Tree.Kind.EXPONENTIATION, expr, right);
	}
	
	/**
	 * Look up precedence for various binary operators
	 */
	int binaryPrecedence(Token t, Context context) {
		if (t.isOperator()) {
			if (t.<JSOperator>getValue().isAssignment())
				return -1;
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
//					return 3;
					return -1;
				case QUESTION_MARK:
//					return 4;
					return -1;
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
		} else if (t.isKeyword()) {
			switch (t.<JSKeyword>getValue()) {
				case IN:
					if (!context.allowIn())
						return -1;
					//Fallthrough intentional
				case INSTANCEOF:
				case AS:
					return 11;
				default:
					return -1;
			}
		}
		return -1;
	}
	
	protected ExpressionTree parseBinaryExpression(Token startToken, JSLexer src, Context context) {
		if (startToken == null)
			startToken = src.nextToken();
		
		context.isolateCoverGrammar();
		ExpressionTree expr = parseExponentiation(startToken, src, context);
		context.inheritCoverGrammar();
		
		/*
		 * Consume all 'as' expressions at the start.
		 * In practice, there is no good reason for there to be more than one,
		 * but there *could" be.
		 */
		while (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS))
			expr = new CastTreeImpl(expr, parseType(src, context));
		
		Token token = src.peek();
		int precedence = binaryPrecedence(token, context);
		if (precedence < 0)
			return expr;
		
		src.skip(token);
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		//Accumulate-reduce binary trees on stack
		final Stack<Token> operators = new Stack<>();
		final Stack<ExpressionTree> stack = new Stack<>();
		int lastPrecedence = precedence;
		
		
		stack.add(expr);
		operators.add(token);
		
		stack.add(parseExponentiation(src.nextToken(), src, context.coverGrammarIsolated()));
		
		
		while ((precedence = binaryPrecedence(src.peek(), context)) >= 0) {
			/*
			 * Reduce expressions with precedences less than the latest operator.
			 * For example, when the expression 'a * b / c + d' is where:
			 *     stack = [(a * b), (c)]
			 *     operators = [/]
			 *     lastPrecedence = 14
			 *     
			 *     src.peek() == '+'
			 *     precedence = 13
			 * 
			 * This loop will reduce it such that:
			 *     stack = [((a * b) / c)]
			 *     operators = []
			 */
			while ((!operators.isEmpty()) && precedence <= lastPrecedence) {
				final ExpressionTree right = stack.pop();
				final Token operator = operators.pop();
				final Kind kind = this.mapTokenToBinaryTree(operator);
				final ExpressionTree left = stack.pop();
				
				BinaryTree binary;
				if (operator.isOperator() && operator.<JSOperator>getValue().isAssignment()) {
					binary = new AssignmentTreeImpl(kind, left, right);
					System.out.println(expr + ";" + stack);
				} else
					binary = new BinaryTreeImpl(kind, left, right);
				stack.add(binary);
				
				lastPrecedence = operators.isEmpty() ? Integer.MAX_VALUE : binaryPrecedence(operators.peek(), context);
			}
			
			
			//Shift top onto stack
			token = src.nextToken();
			
			if (token.matches(TokenKind.KEYWORD, JSKeyword.AS)) {
				//Handle 'as' specially, because it's RHS argument is a type
				ExpressionTree left = stack.pop();
				TypeTree right = this.parseType(src, context);
				stack.push(new CastTreeImpl(left, right));
			} else {
				//Push the newest operator/RHS argument onto their respective stacks
				operators.add(token);
				stack.add(this.parseExponentiation(src.nextToken(), src, context.coverGrammarIsolated()));
			}
			
			lastPrecedence = binaryPrecedence(token, context);
		}
		
		
		/* 
		 * Apply a final reduction, knowing that the expression tree is complete.
		 * For example, when the expression 'a + b * c' is where:
		 *     stack = [(a), (b), (c)]
		 *     operators = [+, *]
		 * 
		 * This loop will reduce it such that:
		 *     stack = [(a), (b), (c)] => [(a), (b * c)] => [(a + (b * c))]
		 *     operators = [+,   *   ] => [    +       ] => [             ]
		 */
		expr = stack.pop();
		

		while (!stack.isEmpty()) {
			final ExpressionTree left = stack.pop();
			final Token operator = operators.pop();
			final Kind kind = this.mapTokenToBinaryTree(operator);
			if (operator.isOperator() && operator.<JSOperator>getValue().isAssignment())
				expr = new AssignmentTreeImpl(kind, left, expr);
			else
				expr = new BinaryTreeImpl(kind, left, expr);
		}
		
		//Reduction failed (this should never happen)
		if (!stack.isEmpty()) {
			System.err.println("Stack: " + stack);
			System.err.println("Ops: " + operators);
			throw new IllegalStateException("Stack not empty");
		}
		return expr;
	}
	
	protected ExpressionTree parseConditional(Token startToken, JSLexer src, Context context) {
		context.isolateCoverGrammar();
		ExpressionTree expr = this.parseBinaryExpression(startToken, src, context);
		context.inheritCoverGrammar();
		
		src.mark();
		Token next = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK);
		//Shortcut to optional property w/type
		//The only time when the sequences '?:', '?,', or '?)' will occur are in a function definition
		if (next != null && context.isMaybeParam()) {
			Token lookahead = src.peek();
			if (lookahead.matches(TokenKind.OPERATOR, JSOperator.COLON) || lookahead.matches(TokenKind.OPERATOR, JSOperator.COMMA) || lookahead.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
				src.reset();
				return expr;
			}
		}
		src.unmark();
		
		if (next == null)
			return expr;
		
		
		context.push();
		context.allowIn(true);
		ExpressionTree concequent = parseAssignment(null, src, context.coverGrammarIsolated());
		context.pop();
		
		expectOperator(JSOperator.COLON, src, context);
		ExpressionTree alternate = parseAssignment(null, src, context.coverGrammarIsolated());
		
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		return new ConditionalExpressionTreeImpl(expr, concequent, alternate);
	}
	
	PatternTree reinterpretExpressionAsPattern(ExpressionTree expr) {
		switch (expr.getKind()) {
			case OBJECT_LITERAL: {
				ObjectLiteralTree obj = (ObjectLiteralTree) expr;
				ArrayList<ObjectPatternPropertyTree> properties = new ArrayList<>();
				for (ObjectLiteralPropertyTree property : obj.getProperties()) {
					ObjectPropertyKeyTree key = property.getKey();
					if (key.isComputed())
						throw new JSSyntaxException("Cannot reinterpret computed property named " + key + " as a pattern.", property.getStart(), property.getEnd());
					if (property.getKind() == Kind.METHOD_DEFINITION)
						throw new JSSyntaxException("Cannot reinterpret method definition " + key + " as a pattern.", property.getStart(), property.getEnd());
					properties.add(new ObjectPatternPropertyTreeImpl(property.getStart(), property.getEnd(), key, reinterpretExpressionAsPattern(property.getValue())));
				}
				properties.trimToSize();
				return new ObjectPatternTreeImpl(obj.getStart(), obj.getEnd(), properties);
			}
			case IDENTIFIER:
				return (IdentifierTree) expr;
			case ARRAY_LITERAL: {
				ArrayList<PatternTree> elements = new ArrayList<>();
				for (ExpressionTree elem : ((ArrayLiteralTree)expr).getElements())
					elements.add(elem == null ? null : reinterpretExpressionAsPattern(elem));
				elements.trimToSize();
				return new ArrayPatternTreeImpl(expr.getStart(), expr.getEnd(), elements);
			}
			case ASSIGNMENT:
				return new AssignmentPatternTreeImpl(expr.getStart(), expr.getEnd(), reinterpretExpressionAsPattern(((AssignmentTree)expr).getLeftOperand()), reinterpretExpressionAsPattern(((AssignmentTree)expr).getRightOperand()));
			default:
				break;
		}
		throw new JSSyntaxException("Cannot reinterpret " + expr + " as a pattern.", expr.getStart());
	}
	
	protected ExpressionTree parseAssignment(Token startToken, JSLexer src, Context context) {
		if (startToken == null)
			startToken = src.nextToken();
		
		//Check if this could possibly start a parameter
		if (context.isMaybeParam() && !TokenPredicate.START_OF_PARAMETER.test(startToken))
			context.isMaybeParam(false);
		
		if (!context.allowYield() && startToken.matches(TokenKind.KEYWORD, JSKeyword.YIELD))
			return this.parseYield(startToken, src, context);
		
		ExpressionTree expr = this.parseConditional(startToken, src, context);
		
		//Upgrade to lambda
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LAMBDA))
			return this.finishFunctionBody(expr.getStart(), false, null, this.reinterpretExpressionAsParameterList(expr), null, true, false, src, context);
		
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
		
		
		final ExpressionTree right = this.parseAssignment(null, src, context.coverGrammarIsolated());
		return new AssignmentTreeImpl(startToken.getStart(), right.getEnd(), this.mapTokenToBinaryTree(assignmentOperator), expr, right);
	}
	
	/**
	 * Parse function parameters. This method will consume all parameters up to a closing (right)
	 * parenthesis (which will also be consumed).
	 * 
	 * Note: parameters are in the function <i>declaration</i>, while arguments are used when
	 * invoking a function.
	 */
	protected List<ParameterTree> parseParameters(JSLexer src, Context context) {
		if (src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			return Collections.emptyList();
		
		ArrayList<ParameterTree> result = new ArrayList<>();
		
		//Flag to remember that all parameters after the first optional parameter must also be
		// optional
		boolean prevOptional = false;
		do {
			Token identifier = src.nextToken();
			if (!identifier.isIdentifier()) {
				if (identifier.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
					//We have ourselves a rest parameter.
					Token spread = identifier;
					UnaryTree expr = this.parseSpread(spread, src, context);
					
					if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) != null)
						throw new JSSyntaxException("Rest parameters cannot be optional", src.getPosition());
					
					TypeTree type = this.parseTypeMaybe(src, context, false);
					
					if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASSIGNMENT) != null)
						throw new JSSyntaxException("Rest parameters cannot have a default value", src.getPosition());
					
					//Rest parameters must be at the end
					if (!src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
						throw new JSSyntaxException("Rest parameter must be the last", src.getPosition());
					
					result.add(new ParameterTreeImpl(identifier.getStart(), src.getPosition(), (IdentifierTree)expr.getExpression(), true, false, type, null));
					break;
				}
				throw new JSUnexpectedTokenException(identifier);
			}
			
			//Check if it's an optional parameter
			boolean optional = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) != null;
			if (prevOptional && !optional)
				throw new JSSyntaxException("A required parameter cannot follow an optional parameter", src.getPosition());
			prevOptional |= optional;
			
			//Parse optional parameter type
			TypeTree type = this.parseTypeMaybe(src, context, false);
			
			//Parse optional default value
			Token assignment = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASSIGNMENT);
			ExpressionTree defaultValue = null;
			if (assignment != null)
				defaultValue = parseAssignment(null, src, context);
			
			result.add(new ParameterTreeImpl(identifier.getStart(), src.getPosition(), new IdentifierTreeImpl(identifier), false, optional, type, defaultValue));
		} while (!src.isEOF() && src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		
		//Expect to end with a right paren
		expect(src.peek(), TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS);
		//Compress ArrayList (not strictly needed, but why not?)
		result.trimToSize();
		return result;
	}
	
	/**
	 * Upgrade a group expression to a lambda function
	 */
	protected FunctionExpressionTree upgradeGroupToLambdaFunction(long startPos, List<ExpressionTree> expressions, ExpressionTree lastParam, JSLexer src, Context context) {
		dialect.require("js.function.lambda", startPos);
		
		ArrayList<ParameterTree> parameters = new ArrayList<>();
		if (expressions != null)
			for (ExpressionTree expression : expressions)
				parameters.add(reinterpretExpressionAsParameter(expression));
		
		
		//Finish last parameter
		if (lastParam.getKind() != Kind.IDENTIFIER)
			//TODO support destructured parameters
			throw new JSUnexpectedTokenException(src.peek());
							
		Token optionalToken = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.QUESTION_MARK);
		if (optionalToken != null)
			dialect.require("ts.parameter.optional", optionalToken.getStart());
		
		//Parse type declaration, if exists
		TypeTree type = parseTypeMaybe(src, context, false);
		if (type != null && !type.isImplicit())
			dialect.require("ts.types", type.getStart());
		
		//Parse default value, if exists
		ExpressionTree defaultValue = ((optionalToken == null && type == null) || src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) ? this.parseAssignment(null, src, context) : null;
		
		parameters.add(new ParameterTreeImpl(lastParam.getStart(), src.getPosition(), (IdentifierTree)lastParam, false, optionalToken != null, type, defaultValue));
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
			parameters.addAll(this.parseParameters(src, context));
		
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		expectOperator(JSOperator.LAMBDA, src, context);
		
		parameters.trimToSize();
		
		return finishFunctionBody(startPos, false, null, parameters, null, true, false, src, context);
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
		
		//Check for easy upgrades to lambda expression
		if (next.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			//Is lambda w/ no args ("()=>???")
			dialect.require("js.function.lambda", leftParenToken.getStart());
			expectOperator(JSOperator.LAMBDA, src, context);
			return finishFunctionBody(leftParenToken.getStart(), false, null, Collections.emptyList(), null, true, false, src, context);
		} else if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			//Lambda w/ 1 rest operator
			dialect.require("js.function.lambda", leftParenToken.getStart());
			dialect.require("js.parameter.rest", next.getStart());
			List<ParameterTree> param = reinterpretExpressionAsParameterList(parseSpread(next, src, context));
			expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
			expectOperator(JSOperator.LAMBDA, src, context);
			return finishFunctionBody(leftParenToken.getStart(), false, null, param, null, true, false, src, context);
		}
		
		context.isBindingElement(true);
		context.isolateCoverGrammar();
		context.isMaybeParam(true);
		ExpressionTree expr = parseAssignment(next, src, context);
		context.inheritCoverGrammar();
		
		if (TokenPredicate.PARAMETER_TYPE_START.test(src.peek())) {
			//Lambda expression where the first parameter has an explicit type/is optional/has default value
			
			return upgradeGroupToLambdaFunction(leftParenToken.getStart(), null, expr, src, context);
		}
		
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
					return this.finishFunctionBody(leftParenToken.getStart(), false, null, params, null, true, false, src, context);
				} else {
					context.isolateCoverGrammar();
					context.isMaybeParam(true);
					final ExpressionTree expression = this.parseAssignment(next, src, context);
					context.inheritCoverGrammar();
					
					
					// Check for declared types (means its a lambda param)
					Token lookahead = src.peek();
					if (lookahead.matches(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) || lookahead.matches(TokenKind.OPERATOR, JSOperator.COLON))
						return upgradeGroupToLambdaFunction(leftParenToken.getStart(), (List<ExpressionTree>)(List<?>)expressions, expression, src, context);
					
					
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
		expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LAMBDA)) {
			//Upgrade to lambda
			dialect.require("js.function.lambda", leftParenToken.getStart());
			List<ParameterTree> params = this.reinterpretExpressionAsParameterList(expr);
			return finishFunctionBody(leftParenToken.getStart(), false, null, params, null, true, false, src, context);
		}
		//Not a lambda, just some parentheses around some expression.
		//TODO probably needs to be wrapped with ParenthesizedTree
		return new ParenthesizedTreeImpl(leftParenToken.getStart(), src.getPosition(), expr);
	}
	
	protected UnaryTree parseSpread(Token spreadToken, JSLexer src, Context context) {
		spreadToken = expect(spreadToken, TokenKind.OPERATOR, JSOperator.SPREAD, src, context);
		dialect.require("js.operator.spread", spreadToken.getStart());
		
		context.isolateCoverGrammar();
		final ExpressionTree expr = parseAssignment(null, src, context);
		context.inheritCoverGrammar();
		
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
		final ExpressionTree callee = parseLeftSideExpression(null, src, context.coverGrammarIsolated(), false);
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
		boolean prevAllowIn = context.allowIn();
		context.allowIn(true);
		
		ExpressionTree expr;
		if (context.inFunction() && t.matches(TokenKind.KEYWORD, JSKeyword.SUPER))
			expr = parseSuper(t, src, context);
		else {
			context.isolateCoverGrammar();
			if (t.matches(TokenKind.KEYWORD, JSKeyword.NEW))
				expr = parseNew(t, src, context);
			else
				expr = parsePrimaryExpression(t, src, context);
			context.inheritCoverGrammar();
		}
		
		while (true) {
			if (src.nextTokenIs(TokenKind.BRACKET, '[')) {
				//Computed member access expressions
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = parseNextExpression(src, context.coverGrammarIsolated());
				expect(TokenKind.BRACKET, ']', src, context);
				expr = new BinaryTreeImpl(expr.getStart(), src.getPosition(), Kind.ARRAY_ACCESS, expr, property);
			} else if (allowCall && (t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null) {
				//Function call
				context.isBindingElement(false);
				context.isAssignmentTarget(false);
				List<ExpressionTree> arguments = parseArguments(t, src, context);
				expr = new FunctionCallTreeImpl(expr.getStart(), src.getPosition(), expr, arguments);
			} else if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.PERIOD)) {
				//Static member access
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = parseIdentifier(null, src, context);
				expr = new BinaryTreeImpl(expr.getStart(), src.getPosition(), Kind.MEMBER_SELECT, expr, property);
			} else if ((t = src.nextTokenIf(TokenKind.TEMPLATE_LITERAL)) != null) {
				//TODO Tagged template literal
				throw new UnsupportedOperationException();
			} else {
				break;
			}
		}
		
		context.allowIn(prevAllowIn);
		return expr;
	}
	
	/**
	 * Parse a function call expression, in the form of {@code [expr]([expr],...,[expr])}.
	 * @param functionSelectExpression
	 *     Expression that contains the expression of the function that is being called
	 * @param openParenToken
	 *     Token for the open parenthesis of the call expression (should be immediately after functionSelectExpression).
	 *     May be null (in this case, it will be read)
	 * @param src
	 * @param context
	 * @returns AST for function call expression
	 */
	protected FunctionCallTree parseFunctionCall(ExpressionTree functionSelectExpression, Token openParenToken,
			JSLexer src, Context context) {
		//Make sure that we have the token for the open paren of the function call
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		
		//Read function arguments (also consumes closing token)
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

	FunctionExpressionTree finishFunctionBody(long startPos, boolean async, IdentifierTree identifier, List<ParameterTree> parameters, TypeTree returnType, boolean arrow, boolean generator, JSLexer src, Context ctx) {
		Token startBodyToken = src.peek();
		ctx.push().enterFunction()
			.allowAwait(async);
		//Read function body
		StatementTree body;
		if (startBodyToken.matches(TokenKind.BRACKET, '{')) {
			src.skip(startBodyToken);
			body = parseBlock(startBodyToken, src, ctx);
		} else
			body = new ReturnTreeImpl(parseNextExpression(src, ctx.coverGrammarIsolated()));
		//TODO infer name from syntax
		FunctionExpressionTree result = new FunctionExpressionTreeImpl(startPos, body.getEnd(), async, identifier, parameters, returnType, arrow, body, ctx.isStrict(), generator);
		ctx.pop();
		//You can't assign to a function
		ctx.isAssignmentTarget(false);
		ctx.isBindingElement(false);
		return result;
	}
	
	protected FunctionExpressionTree parseFunctionExpression(Token functionKeywordToken, JSLexer src, Context context) {
		if (functionKeywordToken == null)
			functionKeywordToken = src.nextToken();
		
		long startPos = functionKeywordToken.getStart();
		
		//The first token could be an async modifier
		boolean async = false;
		if (functionKeywordToken.matches(TokenKind.IDENTIFIER, "async")) {
			dialect.require("js.function.async", functionKeywordToken.getStart());
			async = true;
			functionKeywordToken = src.nextToken();
		}
		
		if (!functionKeywordToken.isKeyword())
			throw new JSUnexpectedTokenException(functionKeywordToken);
		
		boolean generator = functionKeywordToken.getValue() == JSKeyword.FUNCTION_GENERATOR;
		if (!generator && functionKeywordToken.getValue() != JSKeyword.FUNCTION)
			throw new JSUnexpectedTokenException(functionKeywordToken);
		
		IdentifierTree identifier = null;
		Token next = src.nextTokenIf(TokenKind.IDENTIFIER);
		if (next != null)
			identifier = new IdentifierTreeImpl(next);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = parseParameters(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		//Get return type, if provided
		TypeTree returnType = this.parseTypeMaybe(src, context, true);
		
		return finishFunctionBody(startPos, async, identifier, params, returnType, false, generator, src, context);
	}
	
	//Literals
	
	protected LiteralTree<?> parseLiteral(Token literalToken, JSLexer src, Context context) {
		if (literalToken == null)
			literalToken = src.nextToken();
		switch (literalToken.getKind()) {
			case STRING_LITERAL:
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				return new StringLiteralTreeImpl(literalToken);
			case NUMERIC_LITERAL:
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				return new NumericLiteralTreeImpl(literalToken);
			case BOOLEAN_LITERAL:
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				return new BooleanLiteralTreeImpl(literalToken);
			case NULL_LITERAL:
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				return new NullLiteralTreeImpl(literalToken);
			case REGEX_LITERAL:
				context.isAssignmentTarget(false);
				context.isBindingElement(false);
				return new RegExpLiteralTreeImpl(literalToken);
			case TEMPLATE_LITERAL:
				//TODO finish parsing
				throw new UnsupportedOperationException();
			default:
				throw new JSUnexpectedTokenException(literalToken);
		}
	}
	
	/**
	 * Parse array literal.
	 */
	protected ArrayLiteralTree parseArrayInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '[', src, context);
		
		ArrayList<ExpressionTree> values = new ArrayList<>();
		
		Token next;
		while (!((next = src.nextToken()).matches(TokenKind.BRACKET, ']') || src.isEOF())) {
			if (next.matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				values.add(null);
				continue;
			} else if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
				values.add(parseSpread(next, src, context));
				if (!src.peek().matches(TokenKind.BRACKET, ']')) {
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				}
			} else {
				context.isolateCoverGrammar();
				values.add(parseAssignment(next, src, context));
				context.inheritCoverGrammar();
			}
			
			if (!src.peek().matches(TokenKind.BRACKET, ']'))
				expect(TokenKind.OPERATOR, JSOperator.COMMA, src, context);
		}
		
		expect(next, TokenKind.BRACKET, ']');
		
		values.trimToSize();
		
		return new ArrayLiteralTreeImpl(startToken.getStart(), next.getEnd(), values);
	}
	
	/**
	 * Parse object literal.
	 */
	protected ObjectLiteralTree parseObjectInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '{', src, context);
		
		ArrayList<ObjectLiteralPropertyTree> properties = new ArrayList<>();
		
		Token next;
		if ((next = src.nextTokenIf(TokenKind.BRACKET, '}')) == null) {
			while (!src.isEOF()) {
				next = src.nextToken();
				final long startPos = next.getStart();
				
				PropertyDeclarationType methodType = null;
				boolean generator = false;
				Token modifierToken = null;
				if (next.matches(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) {
					generator = true;
					dialect.require("js.method.generator", next.getStart());
					methodType = PropertyDeclarationType.METHOD;
					modifierToken = next;
					next = src.nextToken();
				} else if ((next.matches(TokenKind.IDENTIFIER, "get") || next.matches(TokenKind.IDENTIFIER, "set")) && (src.peek().getKind() == TokenKind.IDENTIFIER || src.peek().matches(TokenKind.BRACKET, '['))) {
					dialect.require("js.accessor", next.getStart());
					methodType = next.getValue().equals("set") ? PropertyDeclarationType.SETTER : PropertyDeclarationType.GETTER;
					modifierToken = next;
					next = src.nextToken();
				}

				ObjectPropertyKeyTree key;
				boolean computed = false;
				if (next.isIdentifier()) {
					key = parseIdentifier(next, null, context);
				} else if (next.getKind() == TokenKind.STRING_LITERAL || next.getKind() == TokenKind.NUMERIC_LITERAL) {
					key = (ObjectPropertyKeyTree) parseLiteral(next, null, context);
				} else if (next.matches(TokenKind.BRACKET, '[')) {
					//Computed ID
					computed = true;
					ExpressionTree expr = parseNextExpression(src, context);
					Token closeBracket = expect(TokenKind.BRACKET, ']', src, context);
					key = new ComputedPropertyKeyTreeImpl(next.getStart(), closeBracket.getEnd(), expr);
				} else {
					throw new JSUnexpectedTokenException(next);
				}
				
				ObjectLiteralPropertyTree prop;
				if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS) != null) {
					if (!computed && key.getKind() == Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("contructor")) {
						if (methodType != null || generator) {
							String modifierName;
							if (generator)
								modifierName = "generator";
							else if (methodType == PropertyDeclarationType.GETTER)
								modifierName = "getter";
							else if (methodType == PropertyDeclarationType.SETTER)
								modifierName = "setter";
							else
								modifierName = "unknown";
							
							throw new JSSyntaxException("Modifier '" + modifierName + "' not allowed in constructor declaration", modifierToken.getStart(), modifierToken.getEnd());
						}
						methodType = PropertyDeclarationType.CONSTRUCTOR;
					} else if (methodType == null) {
						methodType = PropertyDeclarationType.METHOD;
					}
					
					List<ParameterTree> params = this.parseParameters(src, context);
					expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
					
					TypeTree returnType = this.parseTypeMaybe(src, context, true);
					
					FunctionExpressionTree fn = this.finishFunctionBody(startPos, false, key.getKind() == Kind.IDENTIFIER ? ((IdentifierTree)key) : null, params, returnType, false, generator, src, context);
					
					//XXX finish
					throw new UnsupportedOperationException();
					//prop = new MethodDefinitionTreeImpl(startPos, src.getPosition(), key, fn, methodType);
				} else if (methodType != null || generator) {
					throw new JSSyntaxException("Key " + key + " must be a method.", key.getStart(), key.getEnd());
				} else if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON) != null) {
					ExpressionTree value = this.parseAssignment(null, src, context);
					prop = new ObjectLiteralPropertyTreeImpl(startPos, value.getEnd(), key, value);
				} else if ((next = src.nextTokenIf(t->t.matches(TokenKind.OPERATOR, JSOperator.COMMA)||t.matches(TokenKind.BRACKET, '}'))) != null){
					//ES6 shorthand 
					dialect.require("js.property.shorthand", key.getStart());
					properties.add(new ObjectLiteralPropertyTreeImpl(startPos, key.getEnd(), key, key));
					
					if (next.getValue() != TokenKind.OPERATOR)
						break;
					continue;
				} else {
					throw new JSUnexpectedTokenException(src.peek());
				}
				
				properties.add(prop);
				
				if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) == null) {
					next = expect(TokenKind.BRACKET, '}', src, context);
					break;
				}
			}
		}
		expect(next, TokenKind.BRACKET, '}');
		
		properties.trimToSize();
		return new ObjectLiteralTreeImpl(startToken.getStart(), next.getEnd(), properties);
	}
	
	//Unary ops
	
	/**
	 * Parse an unary expression (in form of {@code {OP} {EXPR}} or {@code {EXPR} {OP}}).
	 * 
	 */
	protected ExpressionTree parseUnaryExpression(Token operatorToken, JSLexer src, Context context) {
		if (operatorToken == null)
			operatorToken = src.nextToken();
		Tree.Kind kind = null;
		boolean updates = false;
		switch (operatorToken.getKind()) {
			case KEYWORD:
				switch (operatorToken.<JSKeyword>getValue()) {
					case VOID:
						if (src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
							return new UnaryTreeImpl.VoidTreeImpl(operatorToken.getStart(), src.getPosition(), null);
						kind = Tree.Kind.VOID;
						break;
					case TYPEOF:
						kind = Tree.Kind.TYPEOF;
						break;
					case DELETE:
						kind = Tree.Kind.DELETE;
						break;
					case YIELD:
					case YIELD_GENERATOR:
						return this.parseYield(operatorToken, src, context);
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
						//Special handling for spread operator
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
			context.isolateCoverGrammar();
			ExpressionTree expression = parseUnaryExpression(null, src, context);
			context.inheritCoverGrammar();
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
	
	/**
	 * Handles {@code yield} and {@code yield*} expressions.
	 *
	 */
	protected UnaryTree parseYield(Token yieldKeywordToken, JSLexer src, Context context) {
		yieldKeywordToken = expect(yieldKeywordToken, TokenKind.KEYWORD, src, context);
		
		dialect.require("js.yield", src.getPosition());
		
		//Check if it's a 'yield*'
		boolean delegates = yieldKeywordToken.getValue() == JSKeyword.YIELD_GENERATOR;
		
		//Make sure that our keywork is, in fact, either 'yield' or 'yield*'
		if (!delegates && yieldKeywordToken.getValue() != JSKeyword.YIELD)
			throw new JSUnexpectedTokenException(yieldKeywordToken);
		
		//Parse RHS of expression
		ExpressionTree argument = delegates
				|| !(src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)
				|| src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)
				|| src.peek().matches(TokenKind.BRACKET, '}')) ? this.parseAssignment(null, src, context) : null;
		
		return new UnaryTreeImpl(yieldKeywordToken.getStart(), src.getPosition(), argument, delegates ? Kind.YIELD_GENERATOR : Kind.YIELD);
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
		
		public Context coverGrammarIsolated() {
			Context result = pushed();
			result.isAssignmentTarget(true);
			result.isBindingElement(true);
			return result;
		}
		
		public void isolateCoverGrammar() {
			this.push();
			this.isAssignmentTarget(true);
			this.isBindingElement(true);
		}
		
		/**
		 * Pop the context, inheriting 
		 * @return
		 */
		public Context inheritCoverGrammar() {
			ContextData toInherit = this.data;
			this.pop();
			this.data.isBindingElement &= toInherit.isBindingElement;
			this.data.isAssignmentTarget &= toInherit.isAssignmentTarget;
			return this;
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
		
		public Context allowAwait(boolean value) {
			data.allowAwait = value;
			return this;
		}
		
		public boolean allowAwait() {
			return data.allowAwait;
		}

		public boolean isAssignmentTarget() {
			return data.isAssignmentTarget;
		}
		
		public boolean isMaybeParam() {
			return data.isMaybeParam;
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
		
		public Context isMaybeParam(boolean value) {
			data.isMaybeParam = value;
			return this;
		}
		
		public Context registerGenericParam(String name, long position) throws JSSyntaxException {
			for (ContextData data = this.data; data != null; data = data.parent)
				if (data.genericNames != null && data.genericNames.contains(name))
					throw new JSSyntaxException("Duplicate generic parameter '" + name + "'", position);
			if (this.data.genericNames == null)
				//TODO maybe carry some names from parent to decrease average traversal distance
				this.data.genericNames = new HashSet<>();
			this.data.genericNames.add(name);
			return this;
		}
		
		public Context registerStatementLabel(String name, long position) {
			for (ContextData data = this.data; data != null; data = data.parent)
				if (data.labels != null && data.labels.contains(name))
					throw new JSSyntaxException("Duplicate statement label '" + name + "'", position);
			if (this.data.labels == null)
				//TODO maybe carry some names from parent to decrease average traversal distance
				this.data.labels = new HashSet<>();
			this.data.labels.add(name);
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
			/**
			 * If we're in an async function
			 */
			boolean allowAwait = false;
			boolean isMaybeParam = false;
			final ContextData parent;
			/**
			 * Set of generic names
			 */
			Set<String> genericNames;
			/**
			 * Set of statement labels
			 */
			Set<String> labels;

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
				this.allowAwait = parent.allowAwait;
				this.isMaybeParam = parent.isMaybeParam;
				this.parent = parent;
			}
		}
	}
}
