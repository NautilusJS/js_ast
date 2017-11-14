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
import com.mindlin.jsast.impl.lexer.JSLexer.RegExpTokenInfo;
import com.mindlin.jsast.impl.lexer.JSLexer.TemplateTokenInfo;
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
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.BooleanLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.CaseTreeImpl;
import com.mindlin.jsast.impl.tree.CastTreeImpl;
import com.mindlin.jsast.impl.tree.CatchTreeImpl;
import com.mindlin.jsast.impl.tree.ClassDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.ClassPropertyTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.CompositeTypeTreeImpl;
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
import com.mindlin.jsast.impl.tree.IndexSignatureTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.InterfacePropertyTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceTypeTreeImpl;
import com.mindlin.jsast.impl.tree.KeyofTypeTreeImpl;
import com.mindlin.jsast.impl.tree.LabeledStatementTreeImpl;
import com.mindlin.jsast.impl.tree.LineMap;
import com.mindlin.jsast.impl.tree.LiteralTypeTreeImpl;
import com.mindlin.jsast.impl.tree.MemberExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.MemberTypeTreeImpl;
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
import com.mindlin.jsast.impl.tree.SpecialTypeTreeImpl;
import com.mindlin.jsast.impl.tree.StringLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.SuperExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.SwitchTreeImpl;
import com.mindlin.jsast.impl.tree.TemplateElementTreeImpl;
import com.mindlin.jsast.impl.tree.TemplateLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ThisExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ThrowTreeImpl;
import com.mindlin.jsast.impl.tree.TryTreeImpl;
import com.mindlin.jsast.impl.tree.TupleTypeTreeImpl;
import com.mindlin.jsast.impl.tree.TypeAliasTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclaratorTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WithTreeImpl;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentTree;
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
import com.mindlin.jsast.tree.MethodDefinitionTree;
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
import com.mindlin.jsast.tree.TemplateElementTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeAliasTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.VoidTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.EnumDeclarationTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.GenericParameterTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TypeTree;

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
		if (t.isSpecial() && (!context.isStrict() || t.getValue() == JSSpecialGroup.SEMICOLON))
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
		LineMap lines = src.getLines();
		return new CompilationUnitTreeImpl(0, src.getPosition(), source, lines, elements, false);
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
					case ENUM:
						return this.parseEnum(next, src, context);
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
						return this.parseTypeAlias(next, src, context);
					case "async":
						if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
							return this.finishExpressionStatement(this.parseFunctionExpression(next, src, context), src, context);
					case "await":
						if (!context.allowAwait())
							break;
						return this.parseAwait(next, src, context);
					default:
						break;
				}
				if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COLON))
					return this.parseLabeledStatement(this.parseIdentifier(next, src, context, false), src, context);
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
		long end = expectEOL(src, context).getEnd();
		//TODO convert to directive tree
		if (!context.isStrict() && expression.getKind() == Kind.STRING_LITERAL && "use strict".equals(((LiteralTree<?>)expression).getValue()))
			context.enterStrict();
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
		try {
			switch (expr.getKind()) {
				case IDENTIFIER:
					return new ParameterTreeImpl((IdentifierTree)expr);
				case ASSIGNMENT: {
					dialect.require("js.parameter.default", expr.getStart());
					//Turn into default parameter
					AssignmentTree assignment = (AssignmentTree) expr;
					PatternTree identifier = this.reinterpretExpressionAsPattern(assignment.getLeftOperand());
					
					if (identifier.getKind() != Kind.IDENTIFIER)
						dialect.require("js.parameter.destructured", identifier.getStart());
					
					// I can't find any example of an expression that can't be used
					// as a default value (except ones that won't run at all)
					
					return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), null, identifier, false, false, null, assignment.getRightOperand());
				}
				case SPREAD: {
					dialect.require("js.parameter.rest", expr.getStart());
					//Turn into rest parameter
					PatternTree identifier = this.reinterpretExpressionAsPattern(((UnaryTree)expr).getExpression());
					return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), null, identifier, true, false, null, null);
				}
				case ARRAY_LITERAL:
				case OBJECT_LITERAL:
					dialect.require("js.parameter.destructured", expr.getStart());
					//Turn into destructuring parameter
					return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), null, this.reinterpretExpressionAsPattern(expr), false, false, null, null);
				default:
					break;
			}
		} catch (JSSyntaxException e) {
			//Betterize error messages
			throw new JSSyntaxException("Cannot reinterpret " + expr + " as parameter", expr.getStart(), e);
		}
		
		throw new JSSyntaxException("Cannot reinterpret " + expr + " as parameter", expr.getStart());
	}
	
	protected ExpressionTree parsePrimaryExpression(Token t, JSLexer src, Context context) {
		switch (t.getKind()) {
			case IDENTIFIER:
				if (dialect.supports("js.function.async") && t.getValue().equals("async") && src.peek().matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
					//Async function
					return this.parseFunctionExpression(t, src, context);
				else if (t.getValue().equals("abstract") && src.peek().matches(TokenKind.KEYWORD, JSKeyword.CLASS))
					//Abstract class
					return this.parseClass(t, src, context);
				
				return this.parseIdentifier(t, src, context, false);
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
						return this.parseThis(t, src, context);
					case CLASS:
						return this.parseClass(t, src, context);
					default:
						throw new JSUnexpectedTokenException(t);
				}
			default:
				throw new JSUnexpectedTokenException(t);
		}
	}
	
	protected List<TypeTree> parseGenericArguments(JSLexer src, Context context) {
		Token openChevron = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LESS_THAN);
		if (openChevron == null)
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
	protected List<GenericParameterTree> parseGenericParametersMaybe(JSLexer src, Context context) {
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LESS_THAN))
			return Collections.emptyList();
		
		ArrayList<GenericParameterTree> generics = new ArrayList<>();
		
		//There are no generics (empty '<>')
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.GREATER_THAN))
			return generics;
		
		do {
			IdentifierTree identifier = this.parseIdentifier(null, src, context, false);
			
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
	
	protected List<ExpressionTree> parseArguments(Token startParenToken, JSLexer src, Context context) {
		startParenToken = expect(startParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		 
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			return Collections.emptyList();
		
		ArrayList<ExpressionTree> result = new ArrayList<>();
		do {
			ExpressionTree expr;
			if (src.peek().matches(TokenKind.OPERATOR, JSOperator.SPREAD))
				expr = this.parseSpread(null, src, context);
			else
				expr = this.parseAssignment(null, src, context.coverGrammarIsolated());
			result.add(expr);
		} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
		
		expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
		
		result.trimToSize();
		return result;
	}
	
	//Various statements
	
	protected ImportTree parseImportStatement(Token importKeywordToken, JSLexer src, Context context) {
		importKeywordToken = expect(importKeywordToken, TokenKind.KEYWORD, JSKeyword.IMPORT, src, context);
		
		ArrayList<ImportSpecifierTree> importSpecifiers = new ArrayList<>();
		
		IdentifierTree defaultMemberIdentifier = this.parseIdentifier(null, src, context, true);
		if (defaultMemberIdentifier != null) {
			//import defaultMember...
			importSpecifiers.add(new ImportSpecifierTreeImpl(defaultMemberIdentifier));
			if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.FROM)) {
				//import defaultMember from "module-name";
				StringLiteralTree source = (StringLiteralTree)parseLiteral(null, src, context);
				expectEOL(src, context);
				return new ImportTreeImpl(importKeywordToken.getStart(), source.getEnd(), importSpecifiers, source);
			} else {
				expect(TokenKind.OPERATOR, JSOperator.COMMA, src, context);
			}
		}
		
		Token t;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) != null) {
			//import (defaultMember,)? * as name ...
			IdentifierTree identifier = new IdentifierTreeImpl(t.getStart(), t.getEnd(), "*");
			t = expect(TokenKind.KEYWORD, JSKeyword.AS, src, context);
			IdentifierTree alias = this.parseIdentifier(null, src, context, false);
			importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
		} else if (src.nextTokenIs(TokenKind.BRACKET, '{')) {
			//import (defaultMember,)? {...} ...
			do {
				//member (as alias)?,
				IdentifierTree identifier = this.parseIdentifier(null, src, context, false);
				
				IdentifierTree alias = identifier;
				if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS))
					alias = this.parseIdentifier(null, src, context, false);
				
				importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			expect(TokenKind.BRACKET, '}', src, context);
		}
		
		// ... from "module-name";
		
		if (!importSpecifiers.isEmpty())
			expect(TokenKind.KEYWORD, JSKeyword.FROM, src, context);
		
		StringLiteralTree source = (StringLiteralTree)parseLiteral(null, src, context);
		
		expectEOL(src, context);
		
		importSpecifiers.trimToSize();
		return new ImportTreeImpl(importKeywordToken.getStart(), src.getPosition(), importSpecifiers, source);
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
	
	protected TypeAliasTree parseTypeAlias(Token typeToken, JSLexer src, Context context) {
		typeToken = expect(typeToken, TokenKind.IDENTIFIER, "type", src, context);
		
		IdentifierTree identifier = this.parseIdentifier(null, src, context, false);
		
		List<GenericParameterTree> genericParams = this.parseGenericParametersMaybe(src, context);
		
		expect(TokenKind.OPERATOR, JSOperator.ASSIGNMENT, src, context);
		
		TypeTree value = parseType(src, context);
		
		expectEOL(src, context);
		
		return new TypeAliasTreeImpl(typeToken.getStart(), src.getPosition(), identifier, genericParams, value);
	}
	
	protected StatementTree parseAwait(Token awaitToken, JSLexer src, Context context) {
		//TODO finish
		throw new UnsupportedOperationException("awaiting on await: " + awaitToken.getStart());
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
			IdentifierTree identifier = this.parseIdentifier(null, src, context, false);
			
			//Check if a type is available
			TypeTree type = this.parseTypeMaybe(src, context, false);
			
			//Check if an initializer is available
			ExpressionTree initializer = null;
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) {
				initializer = this.parseAssignment(null, src, context.coverGrammarIsolated());
				if (initializer.getKind() == Kind.FUNCTION_EXPRESSION && ((FunctionExpressionTree) initializer).getName() == null) {
					//Infer fn name from variable id
					FunctionExpressionTree fn = (FunctionExpressionTree) initializer;
					initializer = new FunctionExpressionTreeImpl(fn.getStart(), fn.getEnd(), fn.isAsync(), identifier, fn.getParameters(), fn.getReturnType(), fn.isArrow(), fn.getBody(), fn.isStrict(), fn.isGenerator());
				}
			} else if (isConst) {
				//No initializer
				throw new JSSyntaxException("Missing initializer in constant declaration", identifier.getStart());
			}
			
			if (type == null && initializer != null) {
				//Halfhearted attempt at calculating type
				switch (initializer.getKind()) {
					case STRING_LITERAL:
					case TYPEOF:
						type = new SpecialTypeTreeImpl(SpecialType.STRING);
						break;
					case SUBTRACTION:
					case MULTIPLICATION:
					case DIVISION:
					case REMAINDER:
					case EXPONENTIATION:
					case BITWISE_XOR:
					case BITWISE_XOR_ASSIGNMENT:
					case BITWISE_OR:
					case BITWISE_OR_ASSIGNMENT:
					case BITWISE_NOT:
					case UNARY_MINUS:
					case UNARY_PLUS:
					case LEFT_SHIFT:
					case LEFT_SHIFT_ASSIGNMENT:	
					case RIGHT_SHIFT:
					case RIGHT_SHIFT_ASSIGNMENT:
					case UNSIGNED_RIGHT_SHIFT:
					case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
					case NUMERIC_LITERAL:
					case PREFIX_INCREMENT:
					case PREFIX_DECREMENT:
					case POSTFIX_INCREMENT:
					case POSTFIX_DECREMENT:
						type = new SpecialTypeTreeImpl(SpecialType.NUMBER);
						break;
					case LOGICAL_NOT:
					case EQUAL:
					case NOT_EQUAL:
					case STRICT_EQUAL:
					case STRICT_NOT_EQUAL:
					case GREATER_THAN:
					case GREATER_THAN_EQUAL:
					case LESS_THAN:
					case LESS_THAN_EQUAL:
					case BOOLEAN_LITERAL:
					case DELETE:
					case IN:
					case INSTANCEOF:
						type = new SpecialTypeTreeImpl(SpecialType.BOOLEAN);
						break;
					case NULL_LITERAL:
						type = new SpecialTypeTreeImpl(SpecialType.NULL);
						break;
					default:
						break;
				}
			}
			declarations.add(new VariableDeclaratorTreeImpl(identifier.getStart(), src.getPosition(), identifier, type, initializer));
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

	protected LabeledStatementTree parseLabeledStatement(IdentifierTree identifier, JSLexer src, Context context) {
		StatementTree statement = this.parseStatement(src, context);
		context.registerStatementLabel(identifier.getName(), identifier.getStart());
		return new LabeledStatementTreeImpl(identifier.getStart(), src.getPosition(), identifier, statement);
	}
	
	//SECTION: Type structures
	
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
			dialect.require("ts.class.abstract", classKeywordToken.getStart());
			classKeywordToken = src.nextToken();
			isClassAbstract = true;
		}
		
		classKeywordToken = expect(classKeywordToken, TokenKind.KEYWORD, JSKeyword.CLASS, src, context);
		
		dialect.require("js.class", classKeywordToken.getStart());
		
		//Read optional class identifier
		IdentifierTree classIdentifier = this.parseIdentifier(null, src, context, true);
		List<GenericParameterTree> generics = Collections.emptyList();
		if (classIdentifier != null)
			generics = this.parseGenericParametersMaybe(src, context);
		
		Token next = src.nextToken();
		
		//Read 'extends' and 'implements' clauses
		TypeTree superClass = null;
		List<TypeTree> interfaces = new ArrayList<>();
		//We don't care in which order the 'extends' and 'implements' clauses come in
		for (int i = 0; i < 2 && next.isKeyword(); i++) {
			if (next.matches(TokenKind.KEYWORD, JSKeyword.EXTENDS)) {
				dialect.require("js.class.inheritance", next.getStart());
				if (superClass != null)
					throw new JSSyntaxException("Classes may not extend multiple classes", next.getStart(), next.getEnd());
				superClass = this.parseType(src, context);
				next = src.nextToken();
			}
			if (next.matches(TokenKind.KEYWORD, JSKeyword.IMPLEMENTS)) {
				dialect.require("js.class.inheritance", next.getStart());
				if (!interfaces.isEmpty())
					throw new JSUnexpectedTokenException(next);
				//We can add multiple interfaces here
				do {
					interfaces.add(parseType(src, context));
				} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
				next = src.nextToken();
			}
		}
		
		//Read class body
		expect(next, TokenKind.BRACKET, '{', src, context);
		
		next = null;
		
		ArrayList<ClassPropertyTree<?>> properties = new ArrayList<>();
		while (!src.peek().matches(TokenKind.BRACKET, '}')) {
			//Start position of our next index
			final long propertyStartPos = src.peek().getStart();
			
			//Aspects of property
			PropertyDeclarationType methodType = null;
			AccessModifier accessModifier = null;//Defaults to PUBLIC
			boolean readonly = false;//Readonly modifier
			boolean isStatic = false;
			boolean isPropertyAbstract = false;
			Token modifierToken = null;//Store token of modifier for later error stuff
			
			//We can have up to 3 modifiers (ex. 'public static readonly')
			for (int i = 0; i < 3; i++) {
				//Check for 'readonly' modifier
				Token lookahead = src.peek();
				if (lookahead.matches(TokenKind.IDENTIFIER, "readonly")) {
					src.skip(lookahead);
					if (readonly)//Readonly already set
						throw new JSUnexpectedTokenException(lookahead);
					readonly = true;
				} else if (lookahead.matches(TokenKind.IDENTIFIER, "abstract")) {
					src.skip(lookahead);
					//Check for 'abstract' keyword
					if (!isClassAbstract)
						throw new JSSyntaxException("Can't have an abstract field in a non-abstract class", lookahead.getStart(), lookahead.getEnd());
					if (isPropertyAbstract)
						throw new JSUnexpectedTokenException(lookahead);
					
					modifierToken = lookahead;
					isPropertyAbstract = true;
				} else if (lookahead.isKeyword()) {
					JSKeyword keyword = lookahead.getValue();
					
					if (keyword == JSKeyword.STATIC) {
						src.skip(lookahead);
						if (isStatic)
							throw new JSUnexpectedTokenException(lookahead);
						dialect.require("js.class.static", lookahead.getStart());
						isStatic = true;
					} else if (keyword == JSKeyword.PUBLIC || keyword == JSKeyword.PROTECTED || keyword == JSKeyword.PRIVATE) {
						src.skip(lookahead);
						//Check that there weren't other access modifiers.
						if (accessModifier != null)
							throw new JSUnexpectedTokenException(lookahead);
						
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
			}
			
			//Default accessModifier to PUBLIC if not set
			if (accessModifier == null)
				accessModifier = AccessModifier.PUBLIC;
			

			//Parse property key and optionally more modifiers
			//TODO make sure that this works with the other modifiers (ex., is 'get readonly foo();' allowed?)
			//I'm not sure if this is *correct* js/ts, but let's go with it.
			ObjectPropertyKeyTree key = null;
			
			//Now check if it's a generator/getter/setter
			if ((next = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) != null) {
				//Generator function
				dialect.require("js.method.generator", next.getStart());
				methodType = PropertyDeclarationType.GENERATOR;
				
				modifierToken = next;
			} else if (src.peek().isIdentifier()) {
				//Handle getter/setter/async methods
				Token id = src.nextToken();
				String name = id.getValue();
				if ((name.equals("async") || name.equals("get") || name.equals("set")) && this.isQualifiedPropertyName(src.peek(), context)) {
					key = this.parseObjectPropertyKey(src, context);
					switch (name) {
						case "async":
							dialect.require("js.method.async", id.getStart());
							methodType = PropertyDeclarationType.ASYNC_METHOD;
							break;
						case "get":
							dialect.require("js.accessor", id.getStart());
							methodType = PropertyDeclarationType.GETTER;
							break;
						case "set":
							dialect.require("js.accessor", id.getStart());
							methodType = PropertyDeclarationType.SETTER;
					}
				} else {
					key = new IdentifierTreeImpl(id);
				}
			}
			
			if (key == null)
				key = this.parseObjectPropertyKey(src, context);
			
			//Now let's start on reading the property
			ClassPropertyTree<?> property;
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				//Some type of method
				
				if (!key.isComputed() && key.getKind() == Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("constructor")) {
					//Promote to constructor
					if (methodType != null || isPropertyAbstract) {
						String modifierName = isPropertyAbstract ? "abstract" : methodType.name();
						throw new JSSyntaxException("Modifier '" + modifierName + "' not allowed in constructor declaration", modifierToken.getStart(), modifierToken.getEnd());
					}
					
					dialect.require("js.class.constructor", key.getStart());
					methodType = PropertyDeclarationType.CONSTRUCTOR;
				} else if (methodType == null) {
					//It's a not a getter/setter, but it's still a method
					methodType = PropertyDeclarationType.METHOD;
				}
				
				property = this.parseMethodDefinition(propertyStartPos, isPropertyAbstract, isStatic, readonly, accessModifier, methodType, key, src, context);
			} else if (methodType != null || isPropertyAbstract || (key.getKind() == Tree.Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("constructor"))) {
				//TODO also check for fields named 'new'?
				throw new JSSyntaxException("Key " + key + " must be a method.", key.getStart(), key.getEnd());
			} else {
				methodType = PropertyDeclarationType.FIELD;
				//Field with (optional) type
				TypeTree fieldType = this.parseTypeMaybe(src, context, false);
				//Optional value expression
				ExpressionTree value = null;
				if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT))
					value = this.parseAssignment(null, src, context);
				//Fields end with a semicolon
				expectEOL(src, context);
				property = new ClassPropertyTreeImpl<ExpressionTree>(propertyStartPos, src.getPosition(), accessModifier, readonly, isStatic, methodType, key, fieldType, value);
			}
			
			properties.add(property);
			
			//TODO better expect EOS
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
				type = new IndexSignatureTreeImpl(next.getStart(), src.getPosition(), false, idxType, returnType);
			} else if (next.matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				//Parse function type
				List<ParameterTree> params = this.parseParameters(src, context, false, null);
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
		
		IdentifierTree name = this.parseIdentifier(null, src, context, false);
		List<TypeTree> superClasses;
		if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.EXTENDS)) {
			superClasses = new ArrayList<>();
			do {
				superClasses.add(parseType(src, context));
			} while (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		} else {
			superClasses = Collections.emptyList();
		}
		
		expect(TokenKind.BRACKET, '{', src, context);
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
				return this.parseFunctionType(src, context);
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
			switch (startToken.<String>getValue()) {
				case "keyof": {
					TypeTree baseType = parseType(src, context);
					return new KeyofTypeTreeImpl(startToken.getStart(), baseType.getEnd(), false, baseType);
				}
				case "Array": {
					//Array<X> => X[]
					List<TypeTree> arrayGenericArgs = this.parseGenericArguments(src, context);
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
				case "this":
					throw new UnsupportedOperationException("'this' type not implemented yet");
				case "any":
					return new AnyTypeTreeImpl(startToken.getStart(), startToken.getEnd(), false);
				case "string":
				case "number":
				case "boolean":
				case "null":
				case "undefined":
				case "never":
					return new SpecialTypeTreeImpl(startToken);
				default: {
					IdentifierTree identifier = new IdentifierTreeImpl(startToken);
					
					List<TypeTree> generics = this.parseGenericArguments(src, context);
					
					return new IdentifierTypeTreeImpl(identifier.getStart(), startToken.getEnd(), false, identifier, generics);
				}
			}
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.VOID)) {
			return new SpecialTypeTreeImpl(startToken);
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION)) {
			//Function
			return parseFunctionType(src, context);
		} else if (startToken.matches(TokenKind.BRACKET, '{')) {
			//Inline interface (or object type '{}')
			List<InterfacePropertyTree> properties = this.parseInterfaceBody(src, context);
			return new InterfaceTypeTreeImpl(startToken.getStart(), src.getPosition(), false, properties);
		} else if (startToken.matches(TokenKind.BRACKET, '[')) {
			//Tuple (or array type '[]')
			if (src.nextTokenIs(TokenKind.BRACKET, ']')) {
				//Array type
				//TODO figure out what base type to use here
				return new ArrayTypeTreeImpl(startToken.getStart(), src.getPosition(), false, null);
			}
			
			
			List<TypeTree> slots = new ArrayList<>();
			do {
				slots.add(parseType(src, context));
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			
			Token endToken = expect(TokenKind.BRACKET, ']', src, context);
			return new TupleTypeTreeImpl(startToken.getStart(), endToken.getEnd(), false, slots);
		} else if (startToken.isLiteral()) {
			if (startToken.getKind() == TokenKind.NULL_LITERAL)
				return new SpecialTypeTreeImpl(startToken.getStart(), startToken.getEnd(), SpecialType.NULL, false);
			return new LiteralTypeTreeImpl<>(parseLiteral(startToken, src, context), false);
		} else {
			throw new JSUnexpectedTokenException(startToken);
		}
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
			type = this.parseImmediateType(src, context);
		}
		
		//Support member types in form of 'A.B'
		while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.PERIOD)) {
			type = new MemberTypeTreeImpl(type, parseImmediateType(src, context), false);
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
		
		
		TypeTree left = type;
		boolean union = next.getValue() == JSOperator.BITWISE_OR;
		
		List<TypeTree> constituents = new ArrayList<>();
		constituents.add(left);
		
		//TODO: make less recursive
		TypeTree right = parseType(src, context);
		if (right.getKind() == (union ? Kind.TYPE_UNION : Kind.TYPE_INTERSECTION))//Merge nested unions/intersections
			constituents.addAll(((CompositeTypeTree)right).getConstituents());
		else
			constituents.add(right);
		
		return new CompositeTypeTreeImpl(left.getStart(), right.getEnd(), false, union ? Kind.TYPE_UNION : Kind.TYPE_INTERSECTION, constituents	);
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
			statements.add(this.parseStatement(src, context));
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
		
		IdentifierTree label = this.parseIdentifier(null, src, context, true);
		
		final long start = keywordToken.getStart();
		expectEOL(src, context);
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
		
		if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.ELSE)) {
			// This if statement isn't really needed, but it speeds up 'else if'
			// statements by a bit, and else if statements are more common than
			// else statements (IMHO)
			if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.IF))
				elseStatement = this.parseIfStatement(null, src, context);
			else
				elseStatement = this.parseStatement(src, context);
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
			IdentifierTree param = this.parseIdentifier(null, src, context, false);
			
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
	
	protected WithTree parseWithStatement(Token withKeywordToken, JSLexer src, Context context) {
		withKeywordToken = expect(withKeywordToken, TokenKind.KEYWORD, JSKeyword.WITH, src, context);
		
		if (context.isStrict())
			throw new JSSyntaxException("'with' blocks may not be used in strict mode", withKeywordToken.getStart());
		
		src.expect(JSOperator.LEFT_PARENTHESIS);
		
		context.push();
		context.allowIn(true);
		ExpressionTree expression = parseNextExpression(src, context);
		context.pop();
		
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		
		//TODO check if statement is valid (isLabelledFunction)
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
		
		Token lookahead = src.peek();
		if (lookahead.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)) {
			src.skip(lookahead);
			//Empty initializer statement
			return this.parsePartialForLoopTree(forKeywordToken, new EmptyStatementTreeImpl(lookahead), src, context);
		}
		
		StatementTree initializer = null;
		if (TokenPredicate.VARIABLE_START.test(lookahead)) {
			context.push().allowIn(false);
			VariableDeclarationTree declarations = this.parseVariableDeclaration(null, src, context, true);
			context.pop();
			
			Token next;
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				
				if (declarations.getDeclarations().size() != 1)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Must have 1 binding", next.getStart());
				if (declarations.getDeclarations().get(0).getInitializer() != null)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Variable may not have an initializer", declarations.getDeclarations().get(0).getInitializer().getStart());
				
				return parsePartialForEachLoopTree(forKeywordToken, declarations, isOf, src, context);
			}
			initializer = declarations;
			expectEOL(src, context);
		} else {
			context.push();
			context.allowIn(false);
			ExpressionTree expr = this.parseNextExpression(src, context);
			context.pop();
			
			Token next;
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				PatternTree left = this.reinterpretExpressionAsPattern(expr);
				return this.parsePartialForEachLoopTree(forKeywordToken, left, isOf, src, context);
			}
			
			initializer = this.finishExpressionStatement(expr, src, context);
		}
		
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
	
	
	//SECTION: Expressions

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
					return 11;
				case AS:
					return 15;
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
		ExpressionTree expr = this.parseExponentiation(startToken, src, context);
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
		
		stack.add(this.parseExponentiation(null, src, context.coverGrammarIsolated()));
		
		
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
				
				ExpressionTree expression;
				if (kind == Kind.MEMBER_SELECT || kind == Kind.ARRAY_ACCESS)
					expression = new MemberExpressionTreeImpl(kind, left, right);
				else if (operator.isOperator() && operator.<JSOperator>getValue().isAssignment())
					throw new JSSyntaxException("This shouldn't be happening", operator.getStart());
				else
					expression = new BinaryTreeImpl(kind, left, right);
				stack.add(expression);
				
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
				stack.add(this.parseExponentiation(null, src, context.coverGrammarIsolated()));
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
			case ARRAY_LITERAL: {
				ArrayList<PatternTree> elements = new ArrayList<>();
				for (ExpressionTree elem : ((ArrayLiteralTree)expr).getElements())
					elements.add(elem == null ? null : reinterpretExpressionAsPattern(elem));
				elements.trimToSize();
				return new ArrayPatternTreeImpl(expr.getStart(), expr.getEnd(), elements);
			}
			case ASSIGNMENT:
				return new AssignmentPatternTreeImpl(expr.getStart(), expr.getEnd(), reinterpretExpressionAsPattern(((AssignmentTree)expr).getLeftOperand()), reinterpretExpressionAsPattern(((AssignmentTree)expr).getRightOperand()));
			case ASSIGNMENT_PATTERN:
			case ARRAY_PATTERN:
			case IDENTIFIER:
			case ARRAY_ACCESS:
			case MEMBER_SELECT:
				return (PatternTree) expr;
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
	 * Parse function parameters. This method will consume all parameters up to
	 * a closing (right) parenthesis (which will also be consumed).
	 * 
	 * Note: parameters are in the function <i>declaration</i>, while arguments
	 * are used when invoking a function.
	 * 
	 * @param previous
	 *            Previous parameters parsed, if available. Null if no
	 *            parameters were parsed for the same list before this method
	 *            was called
	 *            TODO clarify
	 */
	protected List<ParameterTree> parseParameters(JSLexer src, Context context, boolean allowAccessModifiers, List<ParameterTree> previous) {
		if (src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			return Collections.emptyList();
		
		ArrayList<ParameterTree> result = new ArrayList<>();
		
		//Flag to remember that all parameters after the first optional parameter must also be
		// optional
		boolean prevOptional = false;
		
		if (previous != null && !previous.isEmpty()) {
			result.addAll(previous);
			
			for (ParameterTree param : result) {
				boolean optional = param.isOptional();
				if (prevOptional && !optional)
					throw new JSSyntaxException("A required parameter cannot follow an optional parameter", param.getStart(), param.getEnd());
				prevOptional |= optional;
				
				if (param.isRest()) {
					if (optional)
						throw new JSSyntaxException("Rest parameters cannot be optional", param.getStart(), param.getEnd());
					if (param.getInitializer() != null)
						throw new JSSyntaxException("Rest parameters cannot have a default value", param.getStart(), param.getEnd());
					if (param != result.get(result.size() - 1))
						throw new JSSyntaxException("Rest parameter must be the last", param.getStart(), param.getEnd());
					
					expect(src.peek(), TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS);
					result.trimToSize();
					return result;
				}
				//We could probably check satisficaiton of the accessModifier constraints, but it wouldn't be that useful.
			}
		}
		
		do {
			Token identifier = src.nextToken();
			AccessModifier access = null;
			if (allowAccessModifiers && identifier.isKeyword() && identifier.getValue() == JSKeyword.PUBLIC || identifier.getValue() == JSKeyword.PROTECTED || identifier.getValue() == JSKeyword.PRIVATE) {
				dialect.require("ts.parameter.accessModifier", identifier.getStart());
				switch (identifier.<JSKeyword>getValue()) {
					case PUBLIC:
						access = AccessModifier.PUBLIC;
						break;
					case PROTECTED:
						access = AccessModifier.PROTECTED;
						break;
					case PRIVATE:
						access = AccessModifier.PRIVATE;
						break;
					default:
						//Should *never* happen
						throw new JSSyntaxException("Unexpected keyword: " + identifier, identifier.getStart());
				}
				identifier = src.nextToken();
			}
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
					
					result.add(new ParameterTreeImpl(identifier.getStart(), src.getPosition(), null, (IdentifierTree)expr.getExpression(), true, false, type, null));
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
			
			result.add(new ParameterTreeImpl(identifier.getStart(), src.getPosition(), access, new IdentifierTreeImpl(identifier), false, optional, type, defaultValue));
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
		
		List<ParameterTree> parameters = new ArrayList<>();
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
		
		parameters.add(new ParameterTreeImpl(lastParam.getStart(), src.getPosition(), null, (IdentifierTree)lastParam, false, optionalToken != null, type, defaultValue));
		((ArrayList<?>) parameters).trimToSize();
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
			parameters = this.parseParameters(src, context, false, parameters);
		
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		expectOperator(JSOperator.LAMBDA, src, context);
		
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
		
		//Check for easy upgrades to lambda expression
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			//Is lambda w/ no args ("()=>???")
			dialect.require("js.function.lambda", leftParenToken.getStart());
			expectOperator(JSOperator.LAMBDA, src, context);
			return finishFunctionBody(leftParenToken.getStart(), false, null, Collections.emptyList(), null, true, false, src, context);
		} else if (src.peek().matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			//Lambda w/ 1 rest operator
			dialect.require("js.function.lambda", leftParenToken.getStart());
			dialect.require("js.parameter.rest", src.peek().getStart());
			List<ParameterTree> param = reinterpretExpressionAsParameterList(parseSpread(null, src, context));
			expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
			expectOperator(JSOperator.LAMBDA, src, context);
			return this.finishFunctionBody(leftParenToken.getStart(), false, null, param, null, true, false, src, context);
		}
		
		context.isBindingElement(true);
		context.isolateCoverGrammar();
		context.isMaybeParam(true);
		ExpressionTree expr = this.parseAssignment(null, src, context);
		context.inheritCoverGrammar();
		
		
		if (TokenPredicate.PARAMETER_TYPE_START.test(src.peek())) {
			//Lambda expression where the first parameter has an explicit type/is optional/has default value
			
			return upgradeGroupToLambdaFunction(leftParenToken.getStart(), null, expr, src, context);
		}
		
		//There are multiple expressions here
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA)) {
			List<Tree> expressions = new ArrayList<>();
			expressions.add(expr);
			
			do {
				if (src.peek().matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
					dialect.require("js.parameter.rest", leftParenToken.getStart());
					//Rest parameter. Must be lambda expression
					expressions.add(this.parseSpread(null, src, context));
					
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
					final ExpressionTree expression = this.parseAssignment(null, src, context);
					context.inheritCoverGrammar();
					
					
					// Check for declared types (means its a lambda param)
					Token lookahead = src.peek();
					if (lookahead.matches(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) || lookahead.matches(TokenKind.OPERATOR, JSOperator.COLON))
						return upgradeGroupToLambdaFunction(leftParenToken.getStart(), (List<ExpressionTree>)(List<?>)expressions, expression, src, context);
					
					
					expressions.add(expression);
				}
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			
			//Ensure that it exited the loop with a closing paren
			context.pop();
			expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
			
			//Sequence, but not lambda
			return new ParenthesizedTreeImpl(leftParenToken.getStart(), src.getPosition(), new SequenceTreeImpl((List<ExpressionTree>)(List<?>)expressions));
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
		return new ParenthesizedTreeImpl(leftParenToken.getStart(), src.getPosition(), expr);
	}
	
	protected UnaryTree parseSpread(Token spreadToken, JSLexer src, Context context) {
		spreadToken = expect(spreadToken, TokenKind.OPERATOR, JSOperator.SPREAD, src, context);
		dialect.require("js.operator.spread", spreadToken.getStart());
		
		context.isolateCoverGrammar();
		final ExpressionTree expr = this.parseAssignment(null, src, context);
		context.inheritCoverGrammar();
		
		return new UnaryTreeImpl(spreadToken.getStart(), expr.getEnd(), expr, Kind.SPREAD);
	}

	protected ExpressionTree parseNew(Token newKeywordToken, JSLexer src, Context context) {
		newKeywordToken = expect(newKeywordToken, TokenKind.KEYWORD, JSKeyword.NEW, src, context);
		Token t;
		
		//Support 'new.target'
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.PERIOD)) != null) {
			Token r = src.nextToken();
			if (context.inFunction() && r.matches(TokenKind.IDENTIFIER, "target"))
				//See developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/new.target
				return new BinaryTreeImpl(Tree.Kind.MEMBER_SELECT, new IdentifierTreeImpl(newKeywordToken.getStart(), newKeywordToken.getEnd(), "new"), new IdentifierTreeImpl(r));
			throw new JSUnexpectedTokenException(t);
		}
		
		final ExpressionTree callee = parseLeftSideExpression(null, src, context.coverGrammarIsolated(), false);
		
		final List<ExpressionTree> args;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null)
			args = parseArguments(t, src, context);
		else
			args = Collections.emptyList();
		
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
				expr = new MemberExpressionTreeImpl(expr.getStart(), src.getPosition(), Kind.ARRAY_ACCESS, expr, property);
			} else if (allowCall && (t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null) {
				//Function call
				context.isBindingElement(false);
				context.isAssignmentTarget(false);
				//TODO delegate to parseFunctionCall()?
				List<ExpressionTree> arguments = this.parseArguments(t, src, context);
				expr = new FunctionCallTreeImpl(expr.getStart(), src.getPosition(), expr, arguments);
			} else if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.PERIOD)) {
				//Static member access
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = this.parseIdentifier(null, src, context, false);
				expr = new MemberExpressionTreeImpl(expr.getStart(), src.getPosition(), Kind.MEMBER_SELECT, expr, property);
			} else if ((t = src.nextTokenIf(token -> (token.getKind() == TokenKind.TEMPLATE_LITERAL && token.<TemplateTokenInfo>getValue().head))) != null) {
				//TODO Tagged template literal
				return this.parseLiteral(t, src, context);
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
	
	protected IdentifierTree parseIdentifier(Token identifierToken, JSLexer src, Context context, boolean optional) {
		//Keep reference to lookahead (if applicable) to skip over it at the end
		Token lookahead = null;
		if (identifierToken == null)
			lookahead = identifierToken = src.peek();
		
		//'yield' can be an identifier if yield expressions aren't allowed in the current context
		if (identifierToken.matches(TokenKind.KEYWORD, JSKeyword.YIELD)) {
			if (!context.allowYield())
				identifierToken = identifierToken.reinterpretAsIdentifier();
			else if (optional)
				return null;
			else
				throw new JSSyntaxException("'yield' not allowed as identifier", identifierToken.getStart());
		}
		
		
		if (!identifierToken.isIdentifier()) {
			if (optional)
				return null;
			throw new JSUnexpectedTokenException(identifierToken, TokenKind.IDENTIFIER);
		}
		
		
		//Check that our identifier has a valid name
		String name = identifierToken.getValue();
		
		//'await' not allowed when in a context that it could be a keyword.
		if (context.allowAwait() && name.equals("await")) {
			if (optional)
				return null;
			throw new JSSyntaxException("'await' not allowed as indentifier in context", identifierToken.getStart());
		}
		
		//'arguments' and 'eval' not allowed as identifiers in strict mode
		//TODO support 'implements', 'interface', 'let', 'package', 'private', 'protected', 'public', 'static' as illegal strict mode identifiers
		if (context.isStrict() && (name.equals("arguments") || name.equals("eval") || name.equals("yield"))) {
			if (optional)
				return null;
			throw new JSSyntaxException("'" + name + "' not allowed as identifier in strict mode", identifierToken.getStart());
		}
		
		//Skip lookahead if applicable
		if (lookahead != null)
			src.skip(lookahead);
		
		return new IdentifierTreeImpl(identifierToken.getStart(), identifierToken.getEnd(), name);
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

	FunctionExpressionTree finishFunctionBody(long startPos, boolean async, IdentifierTree identifier, List<GenericParameterTree> generics, List<ParameterTree> parameters, TypeTree returnType, boolean arrow, boolean generator, JSLexer src, Context ctx) {
		Token startBodyToken = src.peek();
		
		//Update context for function
		ctx.push();
		if (async) {
			dialect.require("js.function.async", startPos);
			ctx.allowAwait(true);
		}
		if (generator)
			ctx.enterGenerator();
		else
			ctx.enterFunction();
		
		
		//Read function body
		StatementTree body;
		//TODO require block if not lambda?
		if (startBodyToken.matches(TokenKind.BRACKET, '{')) {
			src.skip(startBodyToken);
			body = this.parseBlock(startBodyToken, src, ctx);
		} else
			body = new ReturnTreeImpl(parseNextExpression(src, ctx.coverGrammarIsolated()));
		
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
		List<ParameterTree> params = this.parseParameters(src, context, false, null);
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
			case REGEX_LITERAL: {
				context.isAssignmentTarget(false);
				context.isBindingElement(false);
				RegExpTokenInfo info = literalToken.getValue();
				return new RegExpLiteralTreeImpl(literalToken.getStart(), literalToken.getEnd(), info.body, info.flags);
			}
			case TEMPLATE_LITERAL: {
				ArrayList<TemplateElementTree> quasis = new ArrayList<>();
				ArrayList<ExpressionTree> expressions = new ArrayList<>();
				
				TemplateTokenInfo quasi = literalToken.getValue();
				if (!quasi.head)
					throw new IllegalStateException("Not head");
				quasis.add(new TemplateElementTreeImpl(literalToken.getStart(), literalToken.getEnd(), literalToken.getText(), quasi.cooked));
				
				while (!quasi.tail) {
					expressions.add(this.parseNextExpression(src, context));
					
					Token t = src.nextToken();
					quasi = t.getValue();
					quasis.add(new TemplateElementTreeImpl(t.getStart(), t.getEnd(), t.getText(), quasi.cooked));
				}
				return new TemplateLiteralTreeImpl(literalToken.getStart(), src.getPosition(), quasis, expressions);
			}
			default:
				throw new JSUnexpectedTokenException(literalToken);
		}
	}
	
	/**
	 * Parse array literal.
	 */
	protected ArrayLiteralTree parseArrayInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '[', src, context);
		
		if (src.nextTokenIs(TokenKind.BRACKET, ']'))
			return new ArrayLiteralTreeImpl(startToken.getStart(), src.getPosition(), Collections.emptyList());
		
		ArrayList<ExpressionTree> values = new ArrayList<>();
		
		for (Token lookahead = src.peek(); !lookahead.matches(TokenKind.BRACKET, ']') && !src.isEOF(); lookahead = src.peek()) {
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA)) {
				values.add(null);
				continue;
			} else if (lookahead.matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
				values.add(this.parseSpread(null, src, context));
				if (!src.peek().matches(TokenKind.BRACKET, ']')) {
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				}
			} else {
				context.isolateCoverGrammar();
				values.add(this.parseAssignment(null, src, context));
				context.inheritCoverGrammar();
			}
			
			if (!src.peek().matches(TokenKind.BRACKET, ']'))
				expect(TokenKind.OPERATOR, JSOperator.COMMA, src, context);
		}
		
		expect(TokenKind.BRACKET, ']', src, context);
		
		values.trimToSize();
		
		return new ArrayLiteralTreeImpl(startToken.getStart(), src.getPosition(), values);
	}
	
	protected ObjectPropertyKeyTree parseObjectPropertyKey(JSLexer src, Context context) {
		Token lookahead = src.peek();
		long start = lookahead.getStart();
		IdentifierTree id = this.parseIdentifier(null, src, context, true);
		if (id != null)
			return id;
		
		switch (lookahead.getKind()) {
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case TEMPLATE_LITERAL:
				return (ObjectPropertyKeyTree) this.parseLiteral(null, src, context);
			case IDENTIFIER:
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
			case KEYWORD:
				return new IdentifierTreeImpl(src.skip(lookahead).reinterpretAsIdentifier());
			case BRACKET:
				if (lookahead.<Character>getValue() == '[') {
					ExpressionTree expr = this.parseNextExpression(src, context);
					expect(TokenKind.BRACKET, ']', src, context);
					return new ComputedPropertyKeyTreeImpl(start, src.getPosition(), expr);
				}
				//Fallthrough intentional
			default:
				throw new JSUnexpectedTokenException(lookahead);
		}
	}
	
	boolean isQualifiedPropertyName(Token t, Context context) {
		switch (t.getKind()) {
			case BRACKET:
				return t.<Character>getValue() == '[';
			case IDENTIFIER:
			case STRING_LITERAL:
			case NUMERIC_LITERAL:
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
			case KEYWORD:
				return true;
			default:
				return false;
		}
	}
	
	protected MethodDefinitionTree parseMethodDefinition(long startPos, boolean isAbstract, boolean isStatic, boolean isReadonly, AccessModifier accessModifier, PropertyDeclarationType methodType, ObjectPropertyKeyTree key, JSLexer src, Context context) {
		List<ParameterTree> params = this.parseParameters(src, context, methodType == PropertyDeclarationType.CONSTRUCTOR, null);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		TypeTree returnType = null;
		//Return type may not be set on constructor
		if (methodType != PropertyDeclarationType.CONSTRUCTOR)
			returnType = this.parseTypeMaybe(src, context, false);
		
		FunctionTypeTree type = new FunctionTypeTreeImpl(startPos, src.getPosition(), true, params, Collections.emptyList(), returnType);
		
		FunctionExpressionTree fn = null;
		if (!isAbstract)
			fn = this.finishFunctionBody(startPos, false, key.getKind() == Kind.IDENTIFIER ? ((IdentifierTree)key) : null, params, returnType, false, methodType == PropertyDeclarationType.GENERATOR, src, context);
		else
			expectEOL(src, context);
		
		return new MethodDefinitionTreeImpl(startPos, src.getPosition(), accessModifier, isAbstract, isReadonly, isStatic, methodType, key, type, fn);
	}
	
	protected ObjectLiteralPropertyTree parseObjectProperty(JSLexer src, Context context) {
		final long startPos = src.getPosition();
		
		PropertyDeclarationType methodType = null;
		ObjectPropertyKeyTree key = null;
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) {
			//Handle generator methods
			dialect.require("js.method.generator", startPos);
			methodType = PropertyDeclarationType.GENERATOR;
		} else if (src.peek().isIdentifier()) {
			//Handle getter/setter/async methods
			Token id = src.nextToken();
			String name = id.getValue();
			if ((name.equals("async") || name.equals("get") || name.equals("set")) && this.isQualifiedPropertyName(src.peek(), context)) {
				key = this.parseObjectPropertyKey(src, context);
				switch (name) {
					case "async":
						dialect.require("js.method.async", id.getStart());
						methodType = PropertyDeclarationType.ASYNC_METHOD;
						break;
					case "get":
						dialect.require("js.accessor", id.getStart());
						methodType = PropertyDeclarationType.GETTER;
						break;
					case "set":
						dialect.require("js.accessor", id.getStart());
						methodType = PropertyDeclarationType.SETTER;
				}
			} else {
				key = new IdentifierTreeImpl(id);
			}
		}
		
		if (key == null)
			key = this.parseObjectPropertyKey(src, context);
		
		if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS) != null) {
			if (methodType == null)
				methodType = PropertyDeclarationType.METHOD;
			
			return this.parseMethodDefinition(startPos, false, false, false, null, methodType, key, src, context);
		} else if (methodType != null)
			throw new JSSyntaxException("Key " + key + " must be a method.", key.getStart(), key.getEnd());
		else if (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COLON) != null) {
			ExpressionTree value = this.parseAssignment(null, src, context);
			return new ObjectLiteralPropertyTreeImpl(startPos, value.getEnd(), key, value);
		} else if (src.peek().matches(TokenKind.OPERATOR, JSOperator.COMMA) || src.peek().matches(TokenKind.BRACKET, '}')) {
			//ES6 shorthand property
			dialect.require("js.property.shorthand", key.getStart());
			
			return new ObjectLiteralPropertyTreeImpl(startPos, key.getEnd(), key, key);
		} else {
			throw new JSUnexpectedTokenException(src.peek());
		}
	}
	
	/**
	 * Parse object literal.
	 */
	protected ObjectLiteralTree parseObjectInitializer(Token startToken, JSLexer src, Context context) {
		startToken = expect(startToken, TokenKind.BRACKET, '{', src, context);
		
		ArrayList<ObjectLiteralPropertyTree> properties = new ArrayList<>();
		
		while (!src.peek().matches(TokenKind.BRACKET, '}') && !src.isEOF()) {
			properties.add(this.parseObjectProperty(src, context));
			
			if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
				break;
		}
		//TODO betterize
		expect(TokenKind.BRACKET, '}', src, context);
		
		properties.trimToSize();
		return new ObjectLiteralTreeImpl(startToken.getStart(), src.getPosition(), properties);
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
					case LESS_THAN: {
						//Bracket casting
						dialect.require("ts.types.cast", src.getPosition());
						TypeTree type = this.parseType(src, context);
						expect(TokenKind.OPERATOR, JSOperator.GREATER_THAN, src, context);
						ExpressionTree rhs = this.parseBinaryExpression(null, src, context);
						return new CastTreeImpl(operatorToken.getStart(), src.getPosition(), rhs, type);
					}
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
			ExpressionTree result = this.parseLeftSideExpression(operatorToken, src, context, true);
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
		yieldKeywordToken = expect(yieldKeywordToken, TokenKind.KEYWORD, JSKeyword.YIELD, src, context);
		
		dialect.require("js.yield", src.getPosition());
		
		//Check if it's a 'yield*'
		boolean delegates = src.nextTokenIs(TokenKind.OPERATOR, JSOperator.MULTIPLICATION);
		
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
