package com.mindlin.jsast.impl.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.ArrayLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.BooleanLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.CaseTreeImpl;
import com.mindlin.jsast.impl.tree.CatchTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionCallTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.ImportSpecifierTreeImpl;
import com.mindlin.jsast.impl.tree.ImportTreeImpl;
import com.mindlin.jsast.impl.tree.NewTreeImpl;
import com.mindlin.jsast.impl.tree.NullLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ParameterTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.ReturnTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceTreeImpl;
import com.mindlin.jsast.impl.tree.SpreadTreeImpl;
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
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
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
import com.mindlin.jsast.tree.InterfaceTree;
import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.SequenceTree;
import com.mindlin.jsast.tree.SpreadTree;
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
	private static void ensureTokenKind(Token token, TokenKind kind) {
		if (token.getKind() != kind)
			throw new JSSyntaxException("Illegal token " + token + "; expected kind " + kind, token.getStart());
	}
	
	/**
	 * A convenience method, to assert a token's value
	 * @param token
	 * @param value
	 */
	private static void ensureToken(Token token, Object value) {
		if (!Objects.equals(token.getValue(), value))
			throw new JSSyntaxException("Illegal token " + token + "; expected value " + value, token.getStart());
	}
	
	private static Token expect(Token t, TokenKind kind, Object value, JSLexer src, Context context) {
		if (t == null)
			t = src.nextToken();
		ensureTokenKind(t, kind);
		ensureToken(t, value);
		return t;
	}
	
	private static Token expect(TokenKind kind, Object value, JSLexer src, Context context) {
		Token t = src.nextToken();
		ensureTokenKind(t, kind);
		ensureToken(t, value);
		return t;
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
		while ((value = parseNext(src, context)) != null)
			elements.add(value);
		return new CompilationUnitTreeImpl(0, src.getPosition(), unitName, null, elements, false);
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
						return this.parseForStatement(t, src, context);
					case IF:
						return this.parseIfStatement(t, src, context);
					case SWITCH:
						return this.parseSwitchStatement(t, src, context);
					case TRY:
						return this.parseTryStatement(t, src, context);
					case FUNCTION:
						throw new UnsupportedOperationException();
//						return this.parseFunctionKeyword(t, src, context);
					case WITH:
						return this.parseWithStatement(t, src, context);
					case CONST:
					case VAR:
					case LET:
						return this.parseVariableDeclaration(t, src, context);
					case CLASS:
						return this.parseClassUnknown(t, src, context);
					case TYPEOF:
						return this.parseUnaryPrefix(t, src, context);
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
		switch (token.getKind()) {
			case SPECIAL:
				ensureToken(token, JSSpecialGroup.SEMICOLON);
				return new EmptyStatementTreeImpl(token);
			case BRACKET:
				ensureToken(token, '{');
				return parseBlock(token, src, context);
			case KEYWORD:
				switch (token.<JSKeyword>getValue()) {
					case BREAK:
					case CONTINUE:
						return parseGotoStatement(token, src, context);
					case DEBUGGER:
						return parseDebugger(token, src, context);
					case RETURN:
					case THROW:
						return parseUnaryStatement(token, src, context);
					case DELETE:
					case NEW:
					case TYPEOF: {
						ExpressionTree expr = parseUnaryPrefix(token, src, context);
						if (expr.getKind().isStatement())
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
						return this.parseForStatement(token, src, context);
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
						break;
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
				break;
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
			case TEMPLATE_LITERAL:
			case REGEX_LITERAL:
			case IDENTIFIER: {
				ExpressionTree expr = parseNextExpression(token, src, context);
				if (expr.getKind().isStatement())
					return (StatementTree) expr;
				return new ExpressionStatementTreeImpl(expr);
			}
			case OPERATOR:
				if (token.<JSOperator>getValue().arity() == 1) {
					ExpressionTree expr = parseNextExpression(token, src, context);
					if (expr.getKind().isStatement())
						return (StatementTree) expr;
					return new ExpressionStatementTreeImpl(expr);
				}
				break;
			default:
				break;
		}
		throw new JSUnexpectedTokenException(token);
	}
	
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
			expect(null, TokenKind.OPERATOR, JSOperator.COMMA, src, ctx);
		}
	}

	/**
	 * Map a JSOperator type to a Tree.Kind type. Does not support
	 * {@link JSOperator#PLUS}, {@link JSOperator#MINUS},
	 * {@link JSOperator#INCREMENT}, or {@link JSOperator#DECREMENT}.
	 * 
	 * @param operator
	 * @return
	 */
	protected Tree.Kind mapOperatorToTree(JSOperator operator) {
		switch (operator) {
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
			case LAMBDA:
				return Tree.Kind.FUNCTION;
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
			case MULTIPLICATION:
				return Tree.Kind.MULTIPLICATION;
			case MULTIPLICATION_ASSIGNMENT:
				return Tree.Kind.MULTIPLICATION_ASSIGNMENT;
			case NOT_EQUAL:
				return Tree.Kind.NOT_EQUAL;
			case PERIOD:
				return Tree.Kind.MEMBER_SELECT;
			case QUESTION_MARK:
				return Tree.Kind.CONDITIONAL;
			case REMAINDER:
				return Tree.Kind.REMAINDER;
			case REMAINDER_ASSIGNMENT:
				return Tree.Kind.REMAINDER_ASSIGNMENT;
			case LEFT_PARENTHESIS:
			case RIGHT_PARENTHESIS:
				return Tree.Kind.PARENTHESIZED;
			case RIGHT_SHIFT:
				return Tree.Kind.RIGHT_SHIFT;
			case RIGHT_SHIFT_ASSIGNMENT:
				return Tree.Kind.RIGHT_SHIFT_ASSIGNMENT;
			case SPREAD:
				return Tree.Kind.SPREAD;
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
			case MINUS:
			case PLUS:
			case COLON:
			default:
				throw new UnsupportedOperationException();
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
					case LEFT_PARENTHESIS:
					case DIVISION:
					case DIVISION_ASSIGNMENT:
						//TODO finish
				}
			case BRACKET:
				switch ((char)t.getValue()) {
					case '[':
						//Parse array initializer
						break;
					case '{':
						//Parse object initializer
						break;
				}
				break;
			case KEYWORD:
				ExpressionTree expr;
				switch (t.<JSKeyword>getValue()) {
					case YIELD:
						if (!ctx.allowYield())
							throw new JSUnexpectedTokenException(t);
						//TODO finish
						break;
					case FUNCTION:
						return this.parseFunctionExpression(t, src, ctx);
					case THIS:
						return parseThis(t, src, ctx);
					case CLASS:
						
				}
				break;
			case SPECIAL:
			default:
				break;
		}
		throw new UnsupportedOperationException();
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
				return new ParameterTreeImpl(identifier.getStart(), assignment.getRightOperand().getEnd(), identifier.getName(), false, false, null, assignment.getRightOperand());
			}
			case SPREAD:
				dialect.require("js.parameter.rest", expr.getStart());
				//Turn into rest parameter
				return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), ((IdentifierTree)((UnaryTree)expr).getExpression()).getName(), true, false, null, null);
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
	
	FunctionExpressionTree finishLambda(long startPos, List<ParameterTree> parameters, Token startBodyToken, JSLexer src, Context ctx) {
		if (startBodyToken == null)
			startBodyToken = src.nextToken();
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
		FunctionExpressionTree result = new FunctionExpressionTreeImpl(startPos, body.getEnd(), parameters, "", true, body, ctx.isStrict(), false);
		ctx.pop();
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
			src.expect(TokenKind.OPERATOR, JSOperator.LAMBDA);
			return finishLambda(leftParenToken.getStart(), Collections.emptyList(), null, src, context);
		} else if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			//Lambda w/ 1 rest operator
			dialect.require("js.function.lambda", leftParenToken.getStart());
			dialect.require("js.parameter.rest", next.getStart());
			ParameterTree expr = reinterpretExpressionAsParameter(parseSpread(next, src, context));
			src.expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS);
			src.expect(TokenKind.OPERATOR, JSOperator.LAMBDA);
			return finishLambda(leftParenToken.getStart(), Arrays.asList(expr), null, src, context);
		} else {
			boolean arrow = false;
			
			ExpressionTree expr = parseNextExpression(next, src, context);
			
			//There are multiple expressions here
			if ((next = src.peek()).matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				src.skip(next);
				
				List<Tree> expressions = new ArrayList<>();
				expressions.add(expr);
				
				next = src.nextToken();
				do {
					// Lookahead for symbols. This variable is so, in case there
					// is no declared type, the token can be reused later
					// (less ovarall computation) at the cost of practically 0
					// (or maybe <0, b/c GC) memory.
					Token lookahead = null;
					
					if (next.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
						dialect.require("js.parameter.rest", leftParenToken.getStart());
						//Rest parameter. Must be lambda expression
						expressions.add(parseSpread(next, src, context));
						
						if (!arrow) {
							//Upgrade to lambda
							dialect.require("js.function.lambda", leftParenToken.getStart());
							List<ParameterTree> params = new ArrayList<>(expressions.size());
							for (ExpressionTree expression : (List<ExpressionTree>)(List<?>)expressions)
								params.add(reinterpretExpressionAsParameter(expression));
							expressions = (List<Tree>)(List<?>)params;
						}
						arrow = true;
						//The rest parameter must be the last one
						expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
						expect(TokenKind.OPERATOR, JSOperator.LAMBDA, src, context);
						break;
					} else {
						final ExpressionTree expression = parseNextExpression(next, src, context);
						// Check for declared types (means its a lambda param)
						if ((lookahead = src.peek()).matches(TokenKind.OPERATOR, JSOperator.COLON)) {
							src.skip(lookahead);
							
							if (!arrow) {
								//Upgrade to lambda
								dialect.require("js.function.lambda", leftParenToken.getStart());
								List<ParameterTree> params = new ArrayList<>(expressions.size());
								for (ExpressionTree x : (List<ExpressionTree>)(List<?>)expressions)
									params.add(reinterpretExpressionAsParameter(x));
								expressions = (List<Tree>)(List<?>)params;
							}
							arrow = true;
							if (expression.getKind() != Kind.IDENTIFIER)
								throw new JSUnexpectedTokenException(lookahead);
							//TODO support destructured parameters
							
							//Parse type declaration
							TypeTree type = parseTypeStatement(null, src, context);
							
							expressions.add(new ParameterTreeImpl(expression.getStart(), expression.getEnd(), ((IdentifierTree)expression).getName(), false, false, type, null));
							
							//Invalidate lookahead (can't be used for the next iteration anymore)
							lookahead = null;
						} else if (arrow) {
							expressions.add(reinterpretExpressionAsParameter(expression));
						} else {
							expressions.add(expression);
						}
					}

					if (!(next = lookahead == null ? src.peek() : lookahead).matches(TokenKind.OPERATOR, JSOperator.COMMA))
						break;
					src.skip(next);
					next = src.nextToken();
				} while (!src.isEOF());
				
				//Ensure that it exited the loop with a closing paren
				context.pop();
				if (!next.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
					throw new JSUnexpectedTokenException(next);
				
				if (!arrow)
					//Sequence, but not lambda
					return new ParenthesizedTreeImpl(leftParenToken.getStart(), next.getEnd(), new SequenceTreeImpl((List<ExpressionTree>)(List<?>)expressions));
				
				// lambda w/ multiple params, but no rest parameter
				return finishLambda(leftParenToken.getStart(), (List<ParameterTree>)(List<?>)expressions, null, src, context);
			}
			context.pop();
			//Only one expression
			if (!next.matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
				throw new JSUnexpectedTokenException(next);
			
			if ((next = src.peek()).matches(TokenKind.OPERATOR, JSOperator.LAMBDA)) {
				//Upgrade to lambda
				dialect.require("js.function.lambda", leftParenToken.getStart());
				if (expr.getKind() == Kind.SEQUENCE) {
					List<ExpressionTree> expressions = ((SequenceTree)expr).getExpressions();
					List<ParameterTree> params = new ArrayList<>(expressions.size());
					for (ExpressionTree x : expressions)
						params.add(reinterpretExpressionAsParameter(x));
					return finishLambda(leftParenToken.getStart(), params, null, src, context);
				}
				return finishLambda(leftParenToken.getStart(), Arrays.asList(reinterpretExpressionAsParameter(expr)), null, src, context);
			}
			//Not a lambda, just some parentheses around some expression.
			return expr;
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
				 expr = parseNextExpression(src, context);
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
	
	TypeTree reinterpretType(Token typeToken) {
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
		keywordToken = Token.expectKind(keywordToken, TokenKind.KEYWORD, src);
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
			TypeTree type = null;
			ExpressionTree initializer = null;
			if (!identifier.isIdentifier())
				throw new JSUnexpectedTokenException(identifier);
			Token peek = src.peek();
			//Check if a type is available
			if (peek.matches(TokenKind.OPERATOR, JSOperator.COLON)) {
				src.skip(peek);
				type = parseTypeStatement(src.nextToken(), src, context);
				
				peek = src.peek();
			}
			//Check if an initializer is available
			if (peek.matches(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) {
				src.skip(peek);
				initializer = parseNextExpression(null, src, context);
			}
			if (isConst && initializer == null)
				throw new JSSyntaxException("Missing initializer in constant declaration", identifier.getStart());
			declarations.add(new VariableDeclaratorTreeImpl(identifier.getStart(), src.getPosition(), new IdentifierTreeImpl(identifier), type, initializer));
		} while (src.nextToken().matches(TokenKind.OPERATOR, JSOperator.COMMA));
		
		return new VariableDeclarationTreeImpl(keywordToken.getStart(), src.getPosition(), isScoped, isConst, declarations);
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
	
	protected ExpressionTree parseFunctionExpression(Token functionKeywordToken, JSLexer src, Context context) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	protected StatementTree parseGeneratorKeyword(Token generatorKeywordToken, JSLexer src, Context context) {
		// TODO finish
		throw new UnsupportedOperationException();
	}
	
	//Control flows
	protected IfTree parseIfStatement(Token ifKeywordToken, JSLexer src, Context context) {
		ifKeywordToken = Token.expect(ifKeywordToken, TokenKind.KEYWORD, JSKeyword.IF, src);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree thenStatement = parseStatement(null, src, context);
		StatementTree elseStatement = null;
		Token next = src.peek();
		if (next.matches(TokenKind.KEYWORD, JSKeyword.ELSE)) {
			src.skip(next);
			next = src.nextToken();
			// This if statement isn't really needed, but it speeds up 'else if'
			// statements by a bit, and else if statements are more common than
			// else statements (IMHO)
			if (next.matches(TokenKind.KEYWORD, JSKeyword.IF))
				elseStatement = parseIfStatement(next, src, context);
			else
				elseStatement = parseStatement(next, src, context);
		}
		if (elseStatement == null)
			elseStatement = new EmptyStatementTreeImpl(src.getPosition(), src.getPosition());
		return new IfTreeImpl(ifKeywordToken.getStart(), src.getPosition(), expression, thenStatement, elseStatement);
	}
	
	protected SwitchTree parseSwitchStatement(Token switchKeywordToken, JSLexer src, Context context) {
		switchKeywordToken = Token.expect(switchKeywordToken, TokenKind.KEYWORD, JSKeyword.SWITCH, src);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		src.expect(TokenKind.BRACKET, '{');
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
			src.expect(JSOperator.COLON);
			// TODO parse statements
			cases.add(new CaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	protected TryTree parseTryStatement(Token tryKeywordToken, JSLexer src, Context context) {
		tryKeywordToken = expect(tryKeywordToken, TokenKind.KEYWORD, JSKeyword.TRY, src, context);
		//TODO support try-with-resources?
		BlockTree tryBlock = parseBlock(null, src, context);
		ArrayList<CatchTree> catchBlocks = new ArrayList<>();
		
		Token next;
		while ((next = src.peek()).matches(TokenKind.KEYWORD, JSKeyword.CATCH)) {
			src.skip(next);
			expect(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
			IdentifierTree param = parseIdentifier(src.nextToken(), src, context);
			
			Token t = src.nextToken();
			//Optional param type
			TypeTree type = null;
			if (t.matches(TokenKind.OPERATOR, JSOperator.COLON)) {
				type = parseTypeStatement(null, src, context);
				t = src.nextToken();
			}
			expect(t, TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, null, context);
			BlockTree block = parseBlock(src.nextToken(), src, context);
			catchBlocks.add(new CatchTreeImpl(next.getStart(), block.getEnd(), block, param, type));
		}

		//Optional finally block
		BlockTree finallyBlock = null;
		if (next.matches(TokenKind.KEYWORD, JSKeyword.FINALLY)) {
			src.skip(next);
			finallyBlock = parseBlock(null, src, context);
		}
		if (finallyBlock == null && catchBlocks.isEmpty())
			throw new JSSyntaxException("Incomplete try statement", src.getPosition());
		return new TryTreeImpl(tryKeywordToken.getStart(), src.getPosition(), tryBlock, catchBlocks, finallyBlock);
	}
	
	protected IfTree parseFunctionStatement(Token functionKeywordToken, JSLexer src, Context context) {
		functionKeywordToken = Token.expect(functionKeywordToken, TokenKind.KEYWORD, JSKeyword.FUNCTION, src);
		//TODO finish
		throw new UnsupportedOperationException("Type support is (currently) not supported");
	}
	
	protected WithTree parseWithStatement(Token withKeywordToken, JSLexer src, Context context) {
		withKeywordToken = Token.expect(withKeywordToken, TokenKind.KEYWORD, JSKeyword.WITH, src);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		return new WithTreeImpl(withKeywordToken.getStart(), src.getPosition(), expression, statement);
	}
	
	protected SpreadTree parseSpread(Token spreadToken, JSLexer src, Context context) {
		spreadToken = expect(spreadToken, TokenKind.OPERATOR, JSOperator.SPREAD, src, context);
		ExpressionTree expr = parseNextExpression(src, context);
		return new SpreadTreeImpl(spreadToken, expr);
	}
	
	protected ArrayLiteralTree parseArrayInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '[', src, context);
		List<ExpressionTree> values = new ArrayList<>();
		Token t;
		while (!(t = src.nextToken()).matches(TokenKind.BRACKET, ']')) {
			if (t.matches(TokenKind.OPERATOR, JSOperator.COMMA)) {
				values.add(null);
				continue;
			}
			if (t.matches(TokenKind.OPERATOR, JSOperator.SPREAD))
				values.add(parseSpread(t, src, context));
			else
				values.add(parseNextExpression(t, src, context));
			
			t = src.peek();
			if (t.matches(TokenKind.BRACKET, ']'))
				break;
			if (!t.matches(TokenKind.OPERATOR, JSOperator.COMMA))
				throw new JSUnexpectedTokenException(t);
			src.skip(t);
		}
		return new ArrayLiteralTreeImpl(startToken.getStart(), t.getEnd(), values);
	}
	
	protected VoidTree parseVoid(Token voidKeywordToken, JSLexer src, Context context) {
		voidKeywordToken = Token.expect(voidKeywordToken, TokenKind.KEYWORD, JSKeyword.VOID, src);
		ExpressionTree expr = parseNextExpression(src, context);
		return new UnaryTreeImpl.VoidTreeImpl(voidKeywordToken.getStart(), src.getPosition(), expr);
	}
	
	// Loops
	@JSKeywordParser({JSKeyword.WHILE})
	protected WhileLoopTree parseWhileLoop(Token whileKeywordToken, JSLexer src, Context context) {
		if (whileKeywordToken == null)
			whileKeywordToken = ensureToken(src, JSKeyword.WHILE);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		ExpressionTree condition = parseNextExpression(src, context);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
		
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
	protected LoopTree parseForStatement(Token forKeywordToken, JSLexer src, Context context) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, context);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		
		Token t = src.nextToken();
		if (t.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
			//Empty initializer statement
			return parsePartialForLoopTree(forKeywordToken, new EmptyStatementTreeImpl(t), src, context);
		
		StatementTree initializer;
		if (t.getKind() == TokenKind.KEYWORD) {
			if (t.getValue() == JSKeyword.VAR) {
				context.push().allowIn(false);
				VariableDeclarationTree declarations = parseVariableDeclaration(t, src, context);
				context.pop();
				t = src.nextToken();
				if (declarations.getDeclarations().size() == 1 && declarations.getDeclarations().get(0).getIntitializer() == null && (t.matches(TokenKind.KEYWORD, JSKeyword.IN) || t.matches(TokenKind.KEYWORD, JSKeyword.OF)))
					return parsePartialForEachLoopTree(forKeywordToken, t.getValue() == JSKeyword.OF, declarations, src, context);
				initializer = declarations;
			} else if (t.getValue() == JSKeyword.LET || t.getValue() == JSKeyword.CONST) {
				//For??? with let/const variable initializer
				Token identifier = src.nextToken();
				if (!identifier.isIdentifier())
					throw new JSUnexpectedTokenException(identifier);
				Token lookahead = src.peek();
				if (!context.isStrict() && lookahead.getValue() == JSKeyword.IN) {
					src.skip(lookahead);
					VariableDeclarationTree var = new VariableDeclarationTreeImpl(t.getStart(), identifier.getEnd(), true, t.getValue() == JSKeyword.CONST, Arrays.asList(new VariableDeclaratorTreeImpl(identifier)));
					return parsePartialForEachLoopTree(forKeywordToken, false, var, src, context);
				} else {
					//TODO destructuring
					throw new UnsupportedOperationException();
				}
			}
		}
		context.push().allowIn(false);
		StatementTree statement0 = parseStatement(src, context);
		context.pop();
		
		Token separator = src.nextToken();
		if (separator.isSpecial()) {
			ensureToken(separator, JSSpecialGroup.SEMICOLON);
			return parsePartialForLoopTree(forKeywordToken, statement0, src, context);
		} else if (separator.isKeyword()
				&& (separator.getValue() == JSKeyword.IN || separator.getValue() == JSKeyword.OF)) {
			//return this.parsePartialForEachLoopTree(forKeywordToken, separator.getValue() == JSKeyword.OF, statement0,
					//src, context);
			//TODO finish
			throw new UnsupportedOperationException();
		}
		throw new JSSyntaxException("Invalid 'for' loop", src.getPosition());
	}
	
	protected ForLoopTree parseForLoopTree(Token forKeywordToken, JSLexer src, Context context) {
		forKeywordToken = expect(forKeywordToken, TokenKind.KEYWORD, JSKeyword.FOR, src, context);
		ensureToken(src, JSOperator.LEFT_PARENTHESIS);
		StatementTree initializer = parseStatement(src, context);
		
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
	protected ForLoopTree parsePartialForLoopTree(Token forKeywordToken, StatementTree initializer, JSLexer src,
			Context context) {
		ExpressionTree condition = parseNextExpression(src, context);
		ensureToken(src, JSSpecialGroup.SEMICOLON);
		ExpressionTree update = parseNextExpression(src, context);
		ensureToken(src, JSOperator.RIGHT_PARENTHESIS);
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
			case TEMPLATE_LITERAL:
			case STRING_LITERAL:
			case NULL_LITERAL:
			case NUMERIC_LITERAL:
			case OBJECT_LITERAL:
			case ARRAY_LITERAL:
			case BOOLEAN_LITERAL:
			case IDENTIFIER:
				//These can't be broken down at all
				return expression;
			case PARENTHESIZED:
				//Precedence 19
				return new ParenthesizedTreeImpl(expression.getStart(), expression.getEnd(), recalculateO3(((ParenthesizedTree)expression).getExpression()));
			case SEQUENCE: {
				List<ExpressionTree> expressions = ((SequenceTree)expression).getExpressions();
				List<ExpressionTree> recalculated = new ArrayList<>(expressions.size());
				for (ExpressionTree expr : expressions)
					recalculated.add(recalculateO3(expr));
				return new SequenceTreeImpl(expression.getStart(), expression.getEnd(), recalculated);
			}
			//TODO finish
			case ADDITION:
			case BITWISE_AND:
			case BITWISE_OR:
			case BITWISE_XOR:
			case EQUAL:
			case EXPONENTIATION:
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
			case LEFT_SHIFT:
			case LESS_THAN:
			case LESS_THAN_EQUAL:
			case LOGICAL_AND:
			case LOGICAL_OR:
			case MULTIPLICATION:
			case NOT_EQUAL:
			case PROPERTY:
			case REMAINDER:
			case RIGHT_SHIFT:
			case STRICT_EQUAL:
			case STRICT_NOT_EQUAL:
			case SUBTRACTION:
			case UNSIGNED_RIGHT_SHIFT:
			{
				BinaryTree binary = (BinaryTree) expression;
				ExpressionTree left = recalculateO3(binary.getLeftOperand());
				ExpressionTree right = recalculateO3(binary.getRightOperand());
				
				return new BinaryTreeImpl(binary.getKind(), left, right);
			}
			case ADDITION_ASSIGNMENT:
			case BITWISE_AND_ASSIGNMENT:
			case BITWISE_OR_ASSIGNMENT:
			case BITWISE_XOR_ASSIGNMENT:
			case DIVISION_ASSIGNMENT:
			case EXPONENTIATION_ASSIGNMENT:
			case LEFT_SHIFT_ASSIGNMENT:
			case MULTIPLICATION_ASSIGNMENT:
			case REMAINDER_ASSIGNMENT:
			case RIGHT_SHIFT_ASSIGNMENT:
			case SUBTRACTION_ASSIGNMENT:
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
				break;
			case ARRAY_ACCESS:
				break;
			case ASSIGNMENT:
				break;
			case BITWISE_NOT:
				break;
			case BLOCK:
				break;
			case CLASS_DECLARATION:
				break;
			case CLASS_EXPRESSION:
				break;
			case DELETE:
				break;
			case DIVISION:
				break;
			case FUNCTION:
				break;
			case FUNCTION_EXPRESSION:
				break;
			case FUNCTION_INVOCATION: {
				FunctionCallTree invoc = (FunctionCallTree) expression;
			}
				break;
			case IN:
				break;
			case INSTANCE_OF:
				break;
			case INTERFACE_DECLARATION:
				break;
			case LABELED_STATEMENT:
				break;
			case LOGICAL_NOT:
				break;
			case MEMBER_SELECT:
				break;
			case NEW:
				break;
			case POSTFIX_DECREMENT:
				break;
			case POSTFIX_INCREMENT:
				break;
			case PREFIX_DECREMENT:
				break;
			case PREFIX_INCREMENT:
				break;
			case SPREAD:
				break;
			case TYPEOF:
				break;
			case UNARY_MINUS:
				break;
			case UNARY_PLUS:
				break;
			case VOID:
				break;
			default:
				break;
		}
		throw new JSSyntaxException("Unexpected branch: " + expression, expression.getStart());
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
		final ExpressionTree callee = parseLeftSideExpression(src, context, false);
		final List<ExpressionTree> args;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null) {
			args = parseArguments(t, src, context);
		} else {
			args = Collections.emptyList();
		}
		return new NewTreeImpl(newKeywordToken.getStart(), src.getPosition(), callee, args);
	}
	
	protected ExpressionTree parseLeftSideExpression(JSLexer src, Context context, boolean allowCall) {
		context.push().allowIn(true);
		if (!context.allowIn())
			throw new IllegalStateException();
		ExpressionTree expr;

		Token t = src.nextToken();
		if (t.matches(TokenKind.KEYWORD, JSKeyword.SUPER) && context.inFunction()) {
			expr = parseSuper(t, src, context);
		} else if (t.matches(TokenKind.KEYWORD, JSKeyword.NEW)) {
			expr = parseNew(t, src, context);
		} else {
			expr = parsePrimaryExpression(t, src, context);
		}
		
		while (true) {
			t = src.nextToken();
			Token lookahead;
			if (t.matches(TokenKind.BRACKET, '[')) {
				ExpressionTree property = parseNextExpression(t, src, context.pushed().exitBinding().isAssignmentTarget(true));
				expect(TokenKind.BRACKET, ']', src, context);
				expr = new BinaryTreeImpl(t.getStart(), src.getPosition(), Kind.ARRAY_ACCESS, expr, property);
			} else if (allowCall && t.matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				List<ExpressionTree> arguments = parseArguments(t, src, context.pushed().exitBinding().isAssignmentTarget(false));
				expr = new FunctionCallTreeImpl(t.getStart(), src.getPosition(), expr, arguments);
			} else if (t.matches(TokenKind.OPERATOR, JSOperator.PERIOD)) {
				ExpressionTree property = parseIdentifier(t, src, context.pushed().exitBinding().isAssignmentTarget(true));
				expr = new BinaryTreeImpl(t.getStart(), src.getPosition(), Kind.MEMBER_SELECT, expr, property);
			} else if ((lookahead = src.peek()).getKind() == TokenKind.TEMPLATE_LITERAL) {
				//TODO tagged template literal
				throw new UnsupportedOperationException();
			} else {
				break;
			}
		}
		
		context.pop();
		return expr;
	}
	
	protected LiteralTree parseLiteral(Token literalToken, JSLexer src, Context context) {
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
	
	protected ExpressionTree parseNextExpression(JSLexer src, Context context) {
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
		ExpressionTree result = null;
		do {
			switch (t.getKind()) {
				case KEYWORD:
					switch (t.<JSKeyword>getValue()) {
						case NEW:
							src.skip(t);
							result = parseNew(t, src, context);
							continue;
						case DELETE:
						case TYPEOF:
						case VOID:
							src.skip(t);
							result = parseUnaryPrefix(t, src, context);
							continue;
					}
					throw new JSUnexpectedTokenException(t);
				case OPERATOR:
					switch (t.<JSOperator>getValue()) {
						case LEFT_PARENTHESIS:
							src.skip(t);
							result = parseGroupExpression(t, src, context);
							continue;
						case PLUS:
						case MINUS:
							if (result != null) {
								if (context.inBinding())
									return result;
								src.skip(t);
								context.push().enterBinding();
								ExpressionTree right = parseNextExpression(src, context);
								context.pop();
								result = new BinaryTreeImpl(t.getValue() == JSOperator.PLUS ? Kind.ADDITION : Kind.SUBTRACTION, result, right);
								continue;
							}
						case INCREMENT:
						case DECREMENT:
						case LOGICAL_NOT:
						case BITWISE_NOT:
							src.skip(t);
							result = parseUnaryPrefix(t, src, context);
							continue;
						case EQUAL:
						case NOT_EQUAL:
						case STRICT_EQUAL:
						case STRICT_NOT_EQUAL:
						case GREATER_THAN:
						case LESS_THAN:
						case MULTIPLICATION:
						case DIVISION:
						case REMAINDER:
						case EXPONENTIATION:
							if (result != null) {
								if (context.inBinding())
									return result;
								src.skip(t);
								context.push().enterBinding();
								ExpressionTree right = parseNextExpression(src, context);
								context.pop();
								result = new BinaryTreeImpl(t.getValue() == JSOperator.PLUS ? Kind.ADDITION : Kind.SUBTRACTION, result, right);
								continue;
							} else
								throw new JSSyntaxException("Illegal right-hand side expression", t.getStart());
					}
					throw new JSUnexpectedTokenException(t);
				case IDENTIFIER:
					src.skip(t);
					result = parseIdentifier(t, src, context);
					continue;
				case NUMERIC_LITERAL:
				case STRING_LITERAL:
				case BOOLEAN_LITERAL:
				case TEMPLATE_LITERAL:
				case REGEX_LITERAL:
					src.skip(t);
					result = parseLiteral(t, src, context);
					continue;
			}
		} while (!isWeakExpressionEnd((t = src.peek()), context));
		return result;
	}
	
	boolean isWeakExpressionEnd(Token t, Context context) {
		Object v = t.getValue();
		return v == JSOperator.COMMA
				|| v == JSOperator.RIGHT_PARENTHESIS
				|| v == JSSpecialGroup.SEMICOLON
				|| v == JSSpecialGroup.EOF
				|| (t.getKind() == TokenKind.BRACKET
					&& (((char)v) == ']' || ((char)v) == '}'))
				|| (!context.allowIn() && v == JSKeyword.IN)
			;
	}
	
	protected FunctionCallTree parseFunctionCall(ExpressionTree functionSelectExpression, Token openParenToken,
			JSLexer src, Context context) {
		openParenToken = expect(openParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		List<? extends ExpressionTree> arguments = parseArguments(openParenToken, src, context);
		return new FunctionCallTreeImpl(functionSelectExpression.getStart(), src.getPosition(), functionSelectExpression, arguments);
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
	
	protected UnaryTree parseUnaryPrefix(Token operatorToken, JSLexer src, Context context) {
		if (operatorToken == null)
			operatorToken = src.nextToken();
		boolean modifiesArg = false;
		Tree.Kind kind = null;
		switch (operatorToken.getKind()) {
			case KEYWORD:
				switch ((JSKeyword) operatorToken.getValue()) {
					case VOID: {
						Token next = src.peek();
						if ((next = src.nextToken()).getKind() == TokenKind.SPECIAL
								&& next.getValue() == JSSpecialGroup.SEMICOLON) {
							src.skip(next);
							return new UnaryTreeImpl(operatorToken.getStart(), src.getPosition(), null, Tree.Kind.VOID);
						}
					}
						kind = Tree.Kind.VOID;
						break;
					case TYPEOF:
						kind = Tree.Kind.TYPEOF;
						break;
					case DELETE:
						kind = Tree.Kind.DELETE;
						modifiesArg = true;
						break;
					default:
						break;
				}
				break;
			case OPERATOR:
				switch ((JSOperator) operatorToken.getValue()) {
					case INCREMENT:
						kind = Tree.Kind.PREFIX_INCREMENT;
						modifiesArg = true;
						break;
					case DECREMENT:
						kind = Tree.Kind.PREFIX_DECREMENT;
						modifiesArg = true;
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
					case SPREAD:
						return parseSpread(operatorToken, src, context);
					default:
						break;
				}
				break;
			default:
				break;
		}
		if (kind == null)
			throw new JSUnexpectedTokenException(operatorToken);
		context.push().enterBinding();
		ExpressionTree expression = parseNextExpression(src, context);
		context.pop();
		
		//If the operator modifies its argument, check if the argument can be modified
		if (modifiesArg && !Validator.canBeAssigned(expression, dialect))
			throw new JSSyntaxException("Invalid right-hand side expression in " + kind + " expression", operatorToken.getStart());
		
		if (kind == Tree.Kind.VOID)
			//Because it's a special little snowflake
			return new UnaryTreeImpl.VoidTreeImpl(operatorToken, expression);
		return new UnaryTreeImpl(operatorToken.getStart(), src.getPosition(), expression, kind);
	}
	
	protected UnaryTree parseUnaryPostfix(ExpressionTree expr, Token operatorToken, JSLexer src, Context context) {
		if (!Validator.canBeAssigned(expr, dialect))
			throw new JSSyntaxException("Invalid left-hand side expression in " + operatorToken.getKind() + " expression", expr.getStart());
		Kind kind;
		if (operatorToken.matches(TokenKind.OPERATOR, JSOperator.INCREMENT))
			kind = Kind.POSTFIX_INCREMENT;
		else if (operatorToken.matches(TokenKind.OPERATOR, JSOperator.DECREMENT))
			kind = Kind.POSTFIX_DECREMENT;
		else
			throw new IllegalArgumentException(operatorToken.toString() + " is not a postfix unary operator.");
		return new UnaryTreeImpl(expr.getStart(), operatorToken.getEnd(), expr, kind);
	}
	
	protected ExpressiveStatementTree parseUnaryStatement(Token keywordToken, JSLexer src, Context context) {
		if (keywordToken == null)
			keywordToken = src.nextToken();
		if (!(keywordToken.matches(TokenKind.KEYWORD, JSKeyword.RETURN) || keywordToken.matches(TokenKind.KEYWORD, JSKeyword.THROW)))
			throw new JSUnexpectedTokenException(keywordToken);
		ExpressionTree expr = parseNextExpression(null, src, context);
		if (keywordToken.matches(TokenKind.KEYWORD, JSKeyword.RETURN))
			return new ReturnTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
		else
			return new ThrowTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
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
	
	public static class Context {
		ContextData data;
		
		public Context() {
			data = new ContextData();
		}
		
		public Context(ContextData data) {
			data = data;
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
		
		public void allowIn(boolean value) {
			data.allowIn = value;
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
		
		public Context inBinding(boolean value) {
			data.isBindingElement = value;
			return this;
		}
		
		public boolean inFunction() {
			return data.inFunction;
		}
		
		public Context enterFunction() {
			data.inFunction = true;
			return this;
		}
		
		public Context enterSwitch() {
			data.inSwitch = true;
			return this;
		}
		
		public Context enterGenerator() {
			data.inGenerator = true;
			return this;
		}
		
		public void enterLoop() {
			data.inLoop = true;
		}
		
		public void enterStrict() {
			data.isStrict = true;
		}
		
		public void enterBinding() {
			data.isBindingElement = true;
		}
		
		public Context exitBinding() {
			data.isBindingElement = false;
			return this;
		}
		
		public boolean isAssignmentTarget() {
			return data.isAssignmentTarget;
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