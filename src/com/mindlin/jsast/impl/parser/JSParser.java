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
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mindlin.jsast.exception.JSEOFException;
import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.exception.JSUnsupportedException;
import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.JSLexer.RegExpTokenInfo;
import com.mindlin.jsast.impl.lexer.JSLexer.TemplateTokenInfo;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;
import com.mindlin.jsast.impl.tree.AbstractFunctionTree.FunctionDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractFunctionTree.FunctionExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.ArrayLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayTypeTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentPatternTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.BooleanLiteralTreeImpl;
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
import com.mindlin.jsast.impl.tree.FunctionTypeTreeImpl;
import com.mindlin.jsast.impl.tree.TypeParameterDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTypeTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.ImportSpecifierTreeImpl;
import com.mindlin.jsast.impl.tree.ImportDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.IndexSignatureTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.InterfacePropertyTreeImpl;
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
import com.mindlin.jsast.impl.tree.ObjectPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectTypeTreeImpl;
import com.mindlin.jsast.impl.tree.ParameterTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.RegExpLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ReturnTreeImpl;
import com.mindlin.jsast.impl.tree.SpecialTypeTreeImpl;
import com.mindlin.jsast.impl.tree.StringLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.SuperExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.SwitchCaseTreeImpl;
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
import com.mindlin.jsast.impl.util.Pair;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.ClassElementTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DeclarationName;
import com.mindlin.jsast.tree.DirectiveTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionDeclarationTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.ImportDeclarationTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.MethodDeclarationTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ObjectLiteralElement;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree.ObjectPatternElement;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.SwitchCaseTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.TemplateElementTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.VariableDeclarationOrPatternTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.EnumDeclarationTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TypeAliasTree;
import com.mindlin.jsast.tree.type.TypeElementTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class JSParser {
	/**
	 * A convenience method, to assert a token's kind
	 * @param token
	 * @param value
	 */
	private static void expect(Token token, TokenKind kind, JSLexer src) {
		if (token.getKind() != kind)
			throw new JSSyntaxException("Illegal token " + token + "; expected kind " + kind, token.getRange());
	}
	
	private static Token expect(TokenKind kind, JSLexer src, Context context) {
		Token token = src.nextToken();
		expect(token, kind, src);
		return token;
	}
	
	/**
	 * A convenience method, to assert a token's value
	 * @param token
	 * @param value
	 */
	private static void expect(Token token, Object value, JSLexer src) {
		if (!Objects.equals(token.getValue(), value))
			throw new JSSyntaxException("Illegal token " + token + "; expected value " + value, token.getRange());
	}
	
	private static Token expect(Token token, TokenKind kind, Object value, JSLexer src, Context context) {
		if (token == null)
			token = src.nextToken();
		if (token.getKind() != kind)
			throw new JSSyntaxException("Illegal token " + token + "; expected kind " + kind, token.getRange());
		if (!Objects.equals(token.getValue(), value))
			throw new JSSyntaxException("Illegal token " + token + "; expected value " + value, token.getRange());
		return token;
	}
	
	private static Token expect(TokenKind kind, Object value, JSLexer src, Context context) {
		Token t = src.nextToken();
		expect(t, kind, src);
		expect(t, value, src);
		return t;
	}
	
	private static void expect(Token token, TokenKind kind, Object value, JSLexer src) {
		expect(token, kind, src);
		expect(token, value, src);
	}
	
	private static Token expect(Token token, TokenKind kind, JSLexer src, Context context) {
		if (token == null)
			token = src.nextToken();
		expect(token, kind, src);
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
		throw new JSSyntaxException("Illegal token " + t + "; expected EOL", t.getRange());
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
		context.setDirectiveTarget(true);
		SourcePosition start = src.getPosition();
		
		while ((value = parseStatement(src, context)) != null) {
			elements.add(value);
			
			if (context.isDirectiveTarget())
				context.setDirectiveTarget(value instanceof DirectiveTree);
		}
		
		SourceFile source = null;
		LineMap lines = src.getLines();
		
		return new CompilationUnitTreeImpl(start, src.getPosition(), source, lines, elements, false);
	}
	
	protected StatementTree parseStatement(JSLexer src, Context context) {
		Token lookahead = src.peek();
		switch (lookahead.getKind()) {
			case KEYWORD: {
				switch (lookahead.<JSKeyword>getValue()) {
					case BREAK:
					case CONTINUE:
						return this.parseGotoStatement(src, context);
					case DEBUGGER:
						return this.parseDebugger(src, context);
					case CLASS:
						return this.parseClassDeclaration(src, context);
					case CONST:
					case LET:
					case VAR:
						return this.parseVariableDeclaration(false, src, context);
					case DO:
						return this.parseDoWhileLoop(src, context);
					case ENUM:
						return this.parseEnumDeclaration(src, context);
					case EXPORT:
						return this.parseExportStatement(src, context);
					case FOR:
						return this.parseForStatement(src, context);
					case FUNCTION:
					case FUNCTION_GENERATOR:
						return this.parseFunctionDeclaration(src, context);
					case IF:
						return this.parseIfStatement(src, context);
					case IMPORT:
						return this.parseImportStatement(src, context);
					case INTERFACE:
						return this.parseInterfaceDeclaration(src, context);
					case RETURN:
					case THROW:
						return this.parseUnaryStatement(src, context);
					case SWITCH:
						return this.parseSwitchStatement(src, context);
					case TRY:
						return this.parseTryStatement(src, context);
					case WHILE:
						return this.parseWhileLoop(src, context);
					case WITH:
						return this.parseWithStatement(src, context);
					case VOID:
					case DELETE:
					case NEW:
					case TYPEOF:
					case YIELD:
					case SUPER:
					case THIS:
						return this.parseExpressionStatement(src, context);
					case CASE:
					case FINALLY:
					case DEFAULT:
					case ELSE:
					case EXTENDS:
					case IN:
					case INSTANCEOF:
					default:
						throw new JSSyntaxException("Unexpected keyword " + lookahead.getValue(), lookahead.getRange());
				}
			}
			case BRACKET:
				if (lookahead.<Character>getValue() == '{')
					return this.parseBlock(src, context);
				else
					return this.parseExpressionStatement(src, context);
			case OPERATOR:
				if (lookahead.<JSOperator>getValue() == JSOperator.AT_SYMBOL)
					return this.parseDecoratedStatement(src, context);
				return this.parseExpressionStatement(src, context);
			case IDENTIFIER: {
				// We need another lookahead to make this decision
				Token lookahead1 = src.peek(1);
				switch (lookahead.<String>getValue()) {
					case "type":
						if (lookahead1.getKind() != TokenKind.IDENTIFIER)
							break;
						return this.parseTypeAlias(src, context);
					case "async":
						if (lookahead1.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION) || lookahead1.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION_GENERATOR))
							return this.parseFunctionDeclaration(src, context);
					case "await":
						if (!context.allowAwait())
							break;
						return this.parseAwait(src, context);
					default:
						break;
				}
				if (lookahead1.matchesOperator(JSOperator.COLON))
					return this.parseLabeledStatement(src, context);
			}
			//Fallthrough intentional
			case BOOLEAN_LITERAL:
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
			case REGEX_LITERAL:
			case TEMPLATE_LITERAL:
			case NULL_LITERAL:
				return this.parseExpressionStatement(src, context);
			case SPECIAL:
				switch (lookahead.<JSSpecialGroup>getValue()) {
					case EOF:
						return null;
					case EOL:
					case SEMICOLON:
						return new EmptyStatementTreeImpl(src.skip(lookahead));
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
	
	protected StatementTree parseExpressionStatement(JSLexer src, Context context) {
		return this.finishExpressionStatement(this.parseNextExpression(src, context), src, context);
	}
	
	protected StatementTree finishExpressionStatement(ExpressionTree expression, JSLexer src, Context context) {
		SourcePosition end = expectEOL(src, context).getEnd();
		if (context.isDirectiveTarget() && expression.getKind() == Kind.STRING_LITERAL) {
			Object value = ((LiteralTree<?>) expression).getValue();
			
			if ("use strict".equals(value)) {
				context.enterStrict();
				return new DirectiveTreeImpl(expression.getStart(), end, (String) value);
			}
		}
		
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
	
	protected boolean parseCommaSeparator(JSLexer src, Context context) {
		return src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA);
	}
	
	protected void parseTypeMemberSemicolon(JSLexer src, Context context) {
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
			return;
		
		expectEOL(src, context);
	}
	
	protected <T extends Tree> List<T> parseDelimitedList(BiFunction<JSLexer, Context, T> elementParser, BiPredicate<JSLexer, Context> tokenParser, Predicate<Token> isTerminator, JSLexer src, Context context) {
		List<T> result = new ArrayList<>();
		
		while (isTerminator == null || !isTerminator.test(src.peek())) {
			if (src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
				throw new JSEOFException("Unexpected EOF while parsing list", src.getPosition());
			
			T value = elementParser.apply(src, context);
			result.add(value);
			
			if (!tokenParser.test(src, context))
				break;
		}
		
		return result;
	}
	
	protected <T extends Tree> List<T> parseList(BiFunction<JSLexer, Context, T> elementParser, Predicate<Token> isTerminator, JSLexer src, Context context) {
		List<T> result = new ArrayList<>();
		
		while (!isTerminator.test(src.peek())) {
			if (src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
				throw new JSEOFException("Unexpected EOF while parsing list", src.getPosition());
			
			T value = elementParser.apply(src, context);
			result.add(value);
		}
		
		return result;
	}
	/**
	 * Map a JSOperator type to a Tree.Kind type. Does not support operators not binary.
	 * 
	 * @param operator
	 * @return
	 */
	protected Tree.Kind mapTokenToBinaryKind(Token token) {
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
		throw new JSSyntaxException(token + "is not a binary operator", token.getRange());
	}
	
	List<ParameterTree> reinterpretExpressionAsParameterList(ExpressionTree expr) {
		//Unwrap parentheses
		if (expr.getKind() == Kind.PARENTHESIZED)
			return reinterpretExpressionAsParameterList(((ParenthesizedTree)expr).getExpression());
		
		//Convert a sequence tree to a list of parameters
		if (expr.getKind() == Kind.SEQUENCE) {
			List<ExpressionTree> exprs= ((SequenceExpressionTree)expr).getElements();
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
					dialect.require("js.parameter.default", expr.getRange());
					//Turn into default parameter
					final AssignmentTree assignment = (AssignmentTree) expr;
					final PatternTree identifier = assignment.getVariable();
					
					if (identifier.getKind() != Kind.IDENTIFIER)
						dialect.require("js.parameter.destructured", identifier.getRange());
					
					// I can't find any example of an expression that can't be used
					// as a default value (except ones that won't run at all)
					
					return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), Modifiers.NONE, identifier, false, null, assignment.getValue());
				}
				case SPREAD: {
					dialect.require("js.parameter.rest", expr.getRange());
					//Turn into rest parameter
					ExpressionTree inner = ((SpreadElementTree) expr).getExpression();
					PatternTree identifier = this.reinterpretExpressionAsPattern(inner, false);
					return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), Modifiers.NONE, identifier, true, null, null);
				}
				case ARRAY_LITERAL:
				case OBJECT_LITERAL:
					dialect.require("js.parameter.destructured", expr.getRange());
					//Turn into destructuring parameter
					return new ParameterTreeImpl(expr.getStart(), expr.getEnd(), this.reinterpretExpressionAsPattern(expr, false));
				default:
					break;
			}
		} catch (JSSyntaxException e) {
			//TODO: Betterize error messages
			throw new JSSyntaxException("Cannot reinterpret " + expr + " as parameter", expr.getRange(), e);
		}
		
		throw new JSSyntaxException("Cannot reinterpret " + expr + " as parameter", expr.getRange());
	}
	
	protected Modifiers mapPrefixModifier(Token token, Context context) {
		switch (token.getKind()) {
			case IDENTIFIER:
				switch (token.<String>getValue()) {
					case "readonly":
						return Modifiers.READONLY;
					case "abstract":
						return Modifiers.ABSTRACT;
					case "declare":
						return Modifiers.DECLARE;
					case "get":
						return Modifiers.GETTER;
					case "set":
						return Modifiers.SETTER;
					default:
						break;
				}
				break;
			case KEYWORD:
				switch (token.<JSKeyword>getValue()) {
					case STATIC:
						return Modifiers.STATIC;
					case PUBLIC:
						return Modifiers.PUBLIC;
					case PRIVATE:
						return Modifiers.PRIVATE;
					case PROTECTED:
						return Modifiers.PROTECTED;
					case CONST:
						return Modifiers.CONST;
					default:
						break;
				}
				break;
			case OPERATOR:
				switch (token.<JSOperator>getValue()) {
					case QUESTION_MARK: // Postfix
						return Modifiers.OPTIONAL;
					case LOGICAL_NOT: // Postfix
						return Modifiers.DEFINITE;
					case MULTIPLICATION:
						return Modifiers.GENERATOR;
					default:
						break;
				}
				break;
			default:
				break;
		}
		
		return null;
	}
	
	protected Modifiers parseModifiers(Modifiers filter, boolean expectPatternAfter, JSLexer src, Context context) {
		return this.parseModifiers((next, token) -> !next.subtract(filter).any(), expectPatternAfter, src, context);
	}
	
	protected Modifiers parseModifiers(BiPredicate<Modifiers, Token> filter, boolean expectPatternAfter, JSLexer src, Context context) {
		Modifiers result = Modifiers.NONE;
		List<Pair<Token, Modifiers>> modifiers = new ArrayList<>();
		//TODO: better error messages
		
		if (expectPatternAfter)
			src.mark();
		
		while (true) {
			Token next = src.peek();
			Modifiers modifier = this.mapPrefixModifier(next, context);
			
			if (modifier == null || !filter.test(modifier, next)) {
				// We're done here
				break;
			}
			
			dialect.require("ts.parameter.accessModifier", next.getRange());
			
			// Overlap (should we move this to validation?)
			if (Modifiers.intersection(result, modifier).any()) {
				//TODO: Emit other token for duplicated modifier
				throw new JSSyntaxException("Duplicate modifier: '" + modifier + "'", next.getRange());
			} else if (modifier.getAccess() != null && result.getAccess() != null) {
				throw new JSSyntaxException("Duplicate visibility modifier", next.getRange());
			}
			
			// Be able to backtrack if we have a variable with a modifier-like name
			if (expectPatternAfter) {
				//Move up mark
				src.unmark();
				src.mark();
			}
			src.skip(next);
			
			result = Modifiers.union(result, modifier);
			modifiers.add(new Pair<>(next, modifier));
		}
		
		if (expectPatternAfter && !modifiers.isEmpty()) {
			//TODO: better signature for parseIdentifierMaybe that fits better here?
			src.mark();
			if (this.parseIdentifierMaybe(src, context) == null) {
				
			} else {
				src.reset();
			}
		}
		
		return result;
	}
	
	
	protected ExpressionTree parsePrimaryExpression(JSLexer src, Context context) {
		Token lookahead = src.peek();
		switch (lookahead.getKind()) {
			case IDENTIFIER: {
				switch (lookahead.<String>getValue()) {
					case "async":
						if (!dialect.supports("js.function.async"))
							break;
						if (src.peek(1).matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
							//Async function
							return this.parseFunctionExpression(src, context);
						break;
					case "abstract":
						if (src.peek(1).matches(TokenKind.KEYWORD, JSKeyword.CLASS))
							// Abstract class
							return this.parseClassExpression(src, context);
						break;
					default:
						break;
				}
				return this.parseIdentifier(src, context);
			}
			case NUMERIC_LITERAL:
				if (context.isStrict()) {
					//TODO throw error on implicit octal
				}
			case STRING_LITERAL:
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
			case TEMPLATE_LITERAL:
				return this.parseLiteral(null, src, context);
			case OPERATOR:
				switch (src.peek().<JSOperator>getValue()) {
					case LEFT_PARENTHESIS: {
						context.isBindingElement(false);
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseGroupExpression(src, context);
						context.inheritCoverGrammar();
						return result;
					}
					case DIVISION:
					case DIVISION_ASSIGNMENT: {
						context.isAssignmentTarget(false);
						context.isBindingElement(false);
						Token regex = src.finishRegExpLiteral(src.skip(lookahead));
						return this.parseLiteral(regex, src, context);
					}
					default:
						break;
				}
				break;
			case BRACKET:
				switch ((char)lookahead.getValue()) {
					case '[': {
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseArrayInitializer(null, src, context);
						context.inheritCoverGrammar();
						return result;
					}
					case '{': {
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseObjectInitializer(src, context);
						context.inheritCoverGrammar();
						return result;
					}
					default:
						break;
				}
				break;
			case KEYWORD:
				context.isAssignmentTarget(false).isBindingElement(false);
				switch (lookahead.<JSKeyword>getValue()) {
					case FUNCTION:
						return this.parseFunctionExpression(src, context);
					case THIS:
						return this.parseThis(src, context);
					case CLASS:
						return this.parseClassExpression(src, context);
					default:
						break;
				}
				break;
			default:
				break;
		}
		throw new JSUnexpectedTokenException(lookahead);
	}
	
	protected List<TypeTree> parseTypeArguments(JSLexer src, Context context) {
		Token openChevron = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LESS_THAN);
		if (openChevron == null)
			return Collections.emptyList();
		
		List<TypeTree> arguments = this.parseList(this::parseType, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.GREATER_THAN), src, context);
		
		expect(TokenKind.OPERATOR, JSOperator.GREATER_THAN, src, context);
		
		return arguments;
	}
	
	protected TypeParameterDeclarationTree parseTypeParameter(JSLexer src, Context context) {
		// <name> [extends <type>] [= <type>]
		IdentifierTree identifier = this.parseIdentifier(src, context);
		
		TypeTree supertype = null;
		if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.EXTENDS))
			supertype = this.parseType(src, context);
		
		TypeTree defaultValue = null;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT))
			defaultValue = this.parseType(src, context);
		
		return new TypeParameterDeclarationTreeImpl(identifier.getStart(), src.getPosition(), identifier, supertype, defaultValue);
	}
	
	/**
	 * Parse a list of generic type parameters
	 * @param src
	 * @param context
	 * @return
	 */
	protected List<TypeParameterDeclarationTree> parseTypeParametersMaybe(JSLexer src, Context context) {
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LESS_THAN))
			//There are no generics (empty '<>')
			return Collections.emptyList();
		
		List<TypeParameterDeclarationTree> params = this.parseDelimitedList(this::parseTypeParameter, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.GREATER_THAN), src, context);
		
		expect(TokenKind.OPERATOR, JSOperator.GREATER_THAN, src, context);
		return params;
	}
	
	protected List<ExpressionTree> parseArguments(Token startParenToken, JSLexer src, Context context) {
		startParenToken = expect(startParenToken, TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		 
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			return Collections.emptyList();
		
		ArrayList<ExpressionTree> result = new ArrayList<>();
		do {
			ExpressionTree expr;
			if (src.peek().matches(TokenKind.OPERATOR, JSOperator.SPREAD))
				expr = this.parseSpread(src, context);
			else
				expr = this.parseAssignment(src, context.coverGrammarIsolated());
			result.add(expr);
		} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
		
		expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
		
		result.trimToSize();
		return result;
	}
	
	//Various statements
	
	protected ImportDeclarationTree parseImportStatement(JSLexer src, Context context) {
		Token importKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.IMPORT, src, context);
		
		ArrayList<ImportSpecifierTree> importSpecifiers = new ArrayList<>();
		
		IdentifierTree defaultMemberIdentifier = this.parseIdentifierMaybe(src, context);
		if (defaultMemberIdentifier != null) {
			//import defaultMember...
			importSpecifiers.add(new ImportSpecifierTreeImpl(defaultMemberIdentifier));
			if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.FROM)) {
				//import defaultMember from "module-name";
				StringLiteralTree source = (StringLiteralTree)this.parseLiteral(null, src, context);
				expectEOL(src, context);
				return new ImportDeclarationTreeImpl(importKeywordToken.getStart(), source.getEnd(), importSpecifiers, source);
			} else {
				expect(TokenKind.OPERATOR, JSOperator.COMMA, src, context);
			}
		}
		
		Token t;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) != null) {
			//import (defaultMember,)? * as name ...
			IdentifierTree identifier = new IdentifierTreeImpl(t.getStart(), t.getEnd(), "*");
			t = expect(TokenKind.KEYWORD, JSKeyword.AS, src, context);
			IdentifierTree alias = this.parseIdentifier(src, context);
			importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
		} else if (src.nextTokenIs(TokenKind.BRACKET, '{')) {
			//import (defaultMember,)? {...} ...
			do {
				//member (as alias)?,
				IdentifierTree identifier = this.parseIdentifier(src, context);
				
				IdentifierTree alias = identifier;
				if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS))
					alias = this.parseIdentifier(src, context);
				
				importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			expect(TokenKind.BRACKET, '}', src, context);
		}
		
		// ... from "module-name";
		
		if (!importSpecifiers.isEmpty())
			expect(TokenKind.KEYWORD, JSKeyword.FROM, src, context);
		
		StringLiteralTree source = (StringLiteralTree)this.parseLiteral(null, src, context);
		
		expectEOL(src, context);
		
		importSpecifiers.trimToSize();
		return new ImportDeclarationTreeImpl(importKeywordToken.getStart(), src.getPosition(), importSpecifiers, source);
	}
	
	protected ExportTree parseExportStatement(JSLexer src, Context context) {
		Token exportKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.EXPORT, src, context);
		boolean isDefault = false;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.MULTIPLICATION)) {
			expect(TokenKind.KEYWORD, JSKeyword.FROM, src, context);
		} else if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.DEFAULT)) {
			// TODO finish
			isDefault = true;
			throw new JSUnsupportedException("export default", src.getPosition());
		} else if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS)) {
			// TODO finish
			throw new JSUnsupportedException("export as", src.getPosition());
		}
		ExpressionTree expr = parseNextExpression(src, context);
		
		//Optionally consume semicolon
		src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON);
		
		return new ExportTreeImpl(expr.getStart(), src.getPosition(), isDefault, expr);
	}
	
	protected TypeAliasTree parseTypeAlias(JSLexer src, Context context) {
		Token typeToken = expect(TokenKind.IDENTIFIER, "type", src, context);
		
		IdentifierTree identifier = this.parseIdentifier(src, context);
		
		List<TypeParameterDeclarationTree> genericParams = this.parseTypeParametersMaybe(src, context);
		
		expect(TokenKind.OPERATOR, JSOperator.ASSIGNMENT, src, context);
		
		TypeTree value = parseType(src, context);
		
		expectEOL(src, context);
		
		return new TypeAliasTreeImpl(typeToken.getStart(), src.getPosition(), identifier, genericParams, value);
	}
	
	protected StatementTree parseAwait(JSLexer src, Context context) {
		Token awaitToken = expect(TokenKind.IDENTIFIER, "await", src, context);
		//TODO finish
		throw new JSUnsupportedException("awaiting on await", awaitToken.getRange());
	}
	
	protected VariableDeclaratorTree parseVariableDeclarator(JSLexer src, Context context) {
		PatternTree name = this.parsePattern(src, context);
		
		//Check if a type is available
		TypeTree type = this.parseTypeMaybe(src, context, false);
		
		//Check if an initializer is available
		ExpressionTree initializer = null;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) {
			// If we're assigning a function expression, propagate the variable name
			initializer = this.parseAssignment(src, context.coverGrammarIsolated());
			if (name.getKind() == Kind.IDENTIFIER
					&& initializer.getKind() == Kind.FUNCTION_EXPRESSION
					&& ((FunctionExpressionTree) initializer).getName() == null) {
				//Infer fn name from variable id
				FunctionExpressionTree fn = (FunctionExpressionTree) initializer;
				initializer = new FunctionExpressionTreeImpl(fn.getStart(), fn.getEnd(), fn.getModifiers(), (IdentifierTree) name, fn.getTypeParameters(), fn.getParameters(), fn.getReturnType(), fn.isArrow(), fn.getBody());
			}
		}
		
		return new VariableDeclaratorTreeImpl(name.getStart(), src.getPosition(), name, type, initializer);
	}
	
	/**
	 * @param inFor If this variable declaration is in a for loop
	 */
	@JSKeywordParser({ JSKeyword.CONST, JSKeyword.LET, JSKeyword.VAR })
	protected VariableDeclarationTree parseVariableDeclaration(boolean inFor, JSLexer src, Context context) {
		Token keywordToken = expect(TokenKind.KEYWORD, src, context);
		VariableDeclarationKind style;
		switch (keywordToken.<JSKeyword>getValue()) {
			case VAR:
				style = VariableDeclarationKind.VAR;
				break;
			case LET:
				style = VariableDeclarationKind.LET;
				break;
			case CONST:
				style = VariableDeclarationKind.CONST;
				break;
			default:
				throw new JSUnexpectedTokenException(keywordToken);
		}
		
		//Check if allowed
		if (style != VariableDeclarationKind.VAR) {
			dialect.require("js.variable.scoped", keywordToken.getRange());
			if (style == VariableDeclarationKind.CONST)
				dialect.require("js.variable.const", keywordToken.getRange());
		}
		
		//Build list of declarations
		List<VariableDeclaratorTree> declarations = new ArrayList<>();
		//Parse identifier(s)
		//TODO: convert into parseDelimitedList form
		do {
			VariableDeclaratorTree declarator = this.parseVariableDeclarator(src, context);
			
			// Const declarations require initializer
			if (style == VariableDeclarationKind.CONST && declarator.getInitializer() == null)
				throw new JSSyntaxException("Missing initializer in constant declaration", declarator.getRange());
			
			declarations.add(declarator);
		} while (src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
		
		if (!inFor)
			expectEOL(src, context);
		
		return new VariableDeclarationTreeImpl(keywordToken.getStart(), src.getPosition(), style, declarations);
	}
	
	/**
	 * Parse a {@code return} or {@code throw} statement.
	 * <p>
	 * Note that {@code yield} expressions aren't unary statements, as they can
	 * evaluate to a value.
	 * </p>
	 * @param keywordToken
	 * @param src
	 * @param context
	 * @return
	 */
	protected StatementTree parseUnaryStatement(JSLexer src, Context context) {
		Token keywordToken = expect(TokenKind.KEYWORD, src, context);
		if (!(keywordToken.getValue() == JSKeyword.RETURN || keywordToken.getValue() == JSKeyword.THROW))
			throw new JSUnexpectedTokenException(keywordToken);
		
		ExpressionTree expr;
		if (keywordToken.getValue() == JSKeyword.RETURN && src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
			expr = null;
		else
			expr = this.parseNextExpression(src, context);
		
		expectEOL(src, context);
		
		if (keywordToken.getValue() == JSKeyword.RETURN)
			return new ReturnTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
		else
			return new ThrowTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
	}
	
	/**
	 * Parse labeled statement. In form of {@code LABEL: STATEMENT}.
	 * @param identifier
	 * @param src
	 * @param context
	 * @return
	 */
	protected LabeledStatementTree parseLabeledStatement(JSLexer src, Context context) {
		IdentifierTree identifier = this.parseIdentifier(src, context);
		expectOperator(JSOperator.COLON, src, context);
		StatementTree statement = this.parseStatement(src, context);
		return new LabeledStatementTreeImpl(identifier.getStart(), src.getPosition(), identifier, statement);
	}
	
	
	//SECTION: Type structures
	
	protected SignatureDeclarationTree parseCallSignature(JSLexer src, Context context) {
		SourcePosition start = src.getPosition();
		
		boolean constructSignature = src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.NEW);
		
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		List<ParameterTree> params = this.parseParameters(null, false, src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		TypeTree returnType = this.parseTypeMaybe(src, context, false);
		
		this.parseTypeMemberSemicolon(src, context);
		
		if (constructSignature)
			return new ConstructSignatureTreeImpl(start, src.getPosition(), null, typeParams, params, returnType);
		else
			return new CallSignatureTreeImpl(start, src.getPosition(), null, typeParams, params, returnType);
	}
	
	protected IndexSignatureTree parseIndexSignature(JSLexer src, Context context) {
		//TODO: finish
		throw new JSUnsupportedException("Index signatures", src.getPosition());
	}
	
	protected TypeElementTree parseTypeMember(JSLexer src, Context context) {
		Token next = src.peek();
		
		// Call/construct signature
		if (next.matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS) || next.matches(TokenKind.OPERATOR, JSOperator.GREATER_THAN))
			return (CallSignatureTree) this.parseCallSignature(src, context);
		else if (next.matches(TokenKind.KEYWORD, JSKeyword.NEW))
			return (ConstructSignatureTree) this.parseCallSignature(src, context);
		
		// Parse prefix modifiers
		Modifiers typeElementFilter = Modifiers.READONLY;
		Modifiers modifiers = this.parseModifiers(typeElementFilter, true, src, context);
		
		if (this.isIndexSignature(src, context))
			return this.parseIndexSignature(null, modifiers, src, context);
		
		// Method/property signature
		SourcePosition start = next.getStart();
		PropertyName propName = this.parsePropertyName(src, context);
		
		// Parse postfix modifiers
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		next = src.peek();
		if (next.matchesOperator(JSOperator.LESS_THAN) || next.matchesOperator(JSOperator.LEFT_PARENTHESIS))
			// Method signature
			return this.parseMethodSignature(start, modifiers, propName, src, context);
		
	}
	
	/**
	 * Parse a class declaration
	 */
	@JSKeywordParser(JSKeyword.CLASS)
	protected ClassExpressionTree parseClass(Token classKeywordToken, JSLexer src, Context context) {
		if (classKeywordToken == null)
			classKeywordToken = src.nextToken();
		
		final SourcePosition classStartPos = classKeywordToken.getStart();
		
		//Support abstract classes
		final boolean isClassAbstract;
		if (classKeywordToken.matches(TokenKind.IDENTIFIER, "abstract")) {
			dialect.require("ts.class.abstract", classKeywordToken.getStart());
			classKeywordToken = src.nextToken();
			isClassAbstract = true;
		} else {
			isClassAbstract = false;
		}
		
		classKeywordToken = expect(classKeywordToken, TokenKind.KEYWORD, JSKeyword.CLASS, src, context);
		
		dialect.require("js.class", classKeywordToken.getStart());
		
		//Read optional class identifier
		IdentifierTree classIdentifier = this.parseIdentifierMaybe(src, context);
		List<TypeParameterDeclarationTree> generics = Collections.emptyList();
		if (classIdentifier != null)
			generics = this.parseGenericParametersMaybe(src, context);
		
		Token next = src.nextToken();
		
		//Read 'extends' and 'implements' clauses
		TypeTree superClass = null;
		List<TypeTree> interfaces = new ArrayList<>();
		//We don't care in which order the 'extends' and 'implements' clauses come in
		for (int i = 0; i < 2 && next.isKeyword(); i++) {
			if (next.matches(TokenKind.KEYWORD, JSKeyword.EXTENDS)) {
				dialect.require("js.class.inheritance", next.getRange());
				if (superClass != null)
					throw new JSSyntaxException("Classes may not extend multiple classes", next.getRange());
				superClass = this.parseType(src, context);
				next = src.nextToken();
			}
			if (next.matches(TokenKind.KEYWORD, JSKeyword.IMPLEMENTS)) {
				dialect.require("js.class.inheritance", next.getRange());
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
		
		ArrayList<ClassElementTree> properties = new ArrayList<>();
		final Modifiers propModFilter = Modifiers.union(Modifiers.MASK_VISIBILITY, Modifiers.STATIC, Modifiers.READONLY, Modifiers.ABSTRACT);
		
		while (!src.peek().matches(TokenKind.BRACKET, '}')) {
			//TODO: refactor into own method
			//Start position of our next index
			final SourcePosition propertyStartPos = src.peek().getStart();
			
			//Aspects of property
			PropertyDeclarationType methodType = null;
			Modifiers modifier = this.parseModifiers((nextMod, token) -> {
				if (nextMod.isStatic())
					dialect.require("js.class.static", token.getStart());
				
				if (nextMod.isAbstract() && !isClassAbstract)
					throw new JSSyntaxException("Can't have an abstract field in a non-abstract class", token.getRange());
				return !nextMod.subtract(propModFilter).any();
			}, true, src, context);
			
			Token modifierToken = null;//Store token of modifier for later error stuff
			
			//Parse property key and optionally more modifiers
			//TODO make sure that this works with the other modifiers (ex., is 'get readonly foo();' allowed?)
			//I'm not sure if this is *correct* js/ts, but let's go with it.
			DeclarationName key = null;
			
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
					key = this.parsePropertyName(src, context);
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
				key = this.parsePropertyName(src, context);
			
			//Now let's start on reading the property
			ClassElementTree<?> property;
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				//Some type of method
				
				if (key.getKind() == Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("constructor")) {
					//Promote to constructor
					if (methodType != null || modifier.isAbstract()) {
						String modifierName = modifier.isAbstract() ? "abstract" : methodType.name();
						throw new JSSyntaxException("Modifier '" + modifierName + "' not allowed in constructor declaration", modifierToken.getStart(), modifierToken.getEnd());
					}
					
					dialect.require("js.class.constructor", key.getStart());
					methodType = PropertyDeclarationType.CONSTRUCTOR;
				} else if (methodType == null) {
					//It's a not a getter/setter, but it's still a method
					methodType = PropertyDeclarationType.METHOD;
				}
				
				property = this.parseMethodDefinition(propertyStartPos, modifier, methodType, key, src, context);
			} else if (methodType != null || modifier.isAbstract() || (key.getKind() == Tree.Kind.IDENTIFIER && ((IdentifierTree)key).getName().equals("constructor"))) {
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
				property = new ClassPropertyTreeImpl<ExpressionTree>(propertyStartPos, src.getPosition(), modifier, methodType, key, fieldType, value);
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
	List<TypeElementTree> parseObjectTypeMembers(JSLexer src, Context context) {
		List<TypeElementTree> properties = this.parseList(this::parseTypeMember, TokenPredicate.match(TokenKind.BRACKET, '}'), src, context);
		
		expect(TokenKind.BRACKET, '}', src, context);
		
		if (properties.isEmpty())
			return Collections.emptyList();
		return properties;
	}
	
	/**
	 * Parse an interface declaration
	 */
	protected InterfaceDeclarationTree parseInterfaceDeclaration(Token interfaceKeywordToken, JSLexer src, Context context) {
		interfaceKeywordToken = expect(interfaceKeywordToken, TokenKind.KEYWORD, JSKeyword.INTERFACE, src, context);
		
		dialect.require("ts.types.interface", interfaceKeywordToken.getRange());
		
		//Get declared name
		IdentifierTree name = this.parseIdentifier(src, context);
		
		// Type parameters
		List<TypeParameterDeclarationTree> typeParams = this.parseGenericParametersMaybe(src, context);
		
		//...extends A, B, ..., C
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
		//Parse body
		List<TypeElementTree> properties = this.parseObjectTypeMembers(src, context);
		
		return new InterfaceDeclarationTreeImpl(interfaceKeywordToken.getStart(), src.getPosition(), name, typeParams, superClasses, properties);
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
		return this.parseType(src, context);
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
			return new IdentifierTypeTreeImpl(identifier.getStart(), startToken.getEnd(), identifier, Collections.emptyList());
		} else if (startToken.isIdentifier()) {
			switch (startToken.<String>getValue()) {
				case "keyof": {
					//Check for 'keyof X'
					TypeTree baseType = parseType(src, context);
					return new KeyofTypeTreeImpl(startToken.getStart(), baseType.getEnd(), baseType);
				}
				case "Array": {
					//Array<X> => X[]
					List<TypeTree> arrayGenericArgs = this.parseTypeArguments(src, context);
					if (arrayGenericArgs.size() > 1)
						throw new JSSyntaxException("Cannot have more than one type for Array", arrayGenericArgs.get(2).getStart());
					
					TypeTree arrayBaseType = null;
					if (arrayGenericArgs.size() == 1)
						arrayBaseType = arrayGenericArgs.get(0);
					else
						//Fall back on 'any[]'
						arrayBaseType = new SpecialTypeTreeImpl(SpecialType.ANY);
					
					return new ArrayTypeTreeImpl(startToken.getStart(), src.getPosition(), arrayBaseType);
				}
				case "this":
					throw new UnsupportedOperationException("'this' type not implemented yet");
				case "any":
				case "string":
				case "number":
				case "boolean":
				case "null":
				case "undefined":
				case "never":
					return new SpecialTypeTreeImpl(startToken);
				default: {
					IdentifierTree identifier = new IdentifierTreeImpl(startToken);
					
					List<TypeTree> generics = this.parseTypeArguments(src, context);
					
					return new IdentifierTypeTreeImpl(identifier.getStart(), startToken.getEnd(), identifier, generics);
				}
			}
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.VOID)) {
			//void type
			return new SpecialTypeTreeImpl(startToken);
		} else if (startToken.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION)) {
			//Function
			return parseFunctionType(src, context);
		} else if (startToken.matches(TokenKind.BRACKET, '{')) {
			//Inline interface (or object type '{}')
			//TODO: change to object type
			List<TypeElementTree> properties = this.parseObjectTypeMembers(src, context);
			return new ObjectTypeTreeImpl(startToken.getStart(), src.getPosition(), properties);
		} else if (startToken.matches(TokenKind.BRACKET, '[')) {
			//Tuple (or array type '[]')
			if (src.nextTokenIs(TokenKind.BRACKET, ']')) {
				//Array type
				//TODO figure out what base type to use here
				return new ArrayTypeTreeImpl(startToken.getStart(), src.getPosition(), false, null);
			}
			
			
			List<TypeTree> slots = new ArrayList<>();
			do {
				slots.add(this.parseType(src, context));
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			
			Token endToken = expect(TokenKind.BRACKET, ']', src, context);
			return new TupleTypeTreeImpl(startToken.getStart(), endToken.getEnd(), slots);
		} else if (startToken.isLiteral()) {
			if (startToken.getKind() == TokenKind.NULL_LITERAL)
				return new SpecialTypeTreeImpl(startToken.getStart(), startToken.getEnd(), SpecialType.NULL);
			return new LiteralTypeTreeImpl<>(this.parseLiteral(startToken, src, context), false);
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
			type = new MemberTypeTreeImpl(type, parseImmediateType(src, context));
		}
		
		//Support array types in form of 'X[]'
		while (src.nextTokenIs(TokenKind.BRACKET, '[')) {
			Token endToken = expect(TokenKind.BRACKET, ']', src, context);
			type = new ArrayTypeTreeImpl(type.getStart(), endToken.getEnd(), type);
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
		
		return new CompositeTypeTreeImpl(left.getStart(), right.getEnd(), union ? Kind.TYPE_UNION : Kind.TYPE_INTERSECTION, constituents	);
	}
	
	
	//Control flows
	
	protected DebuggerTree parseDebugger(JSLexer src, Context context) {
		Token debuggerKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.DEBUGGER, src, context);
		expectEOL(src, context);
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	protected BlockTree parseBlock(JSLexer src, Context context) {
		Token openBraceToken = expect(TokenKind.BRACKET, '{', src, context);
		List<StatementTree> statements = new LinkedList<>();
		Token t;
		while ((t = src.nextTokenIf(TokenKind.BRACKET, '}')) == null) {
			StatementTree statement = this.parseStatement(src, context);
			statements.add(statement);
			
			if (context.isDirectiveTarget())
				context.setDirectiveTarget(statement instanceof DirectiveTree);
		}
		
		expect(t, '}', src);
		return new BlockTreeImpl(openBraceToken.getStart(), src.getPosition(), statements);
	}
	
	/**
	 * Parse a {@code break} or {@code continue} statement, with optional label.
	 */
	protected GotoTree parseGotoStatement(JSLexer src, Context context) {
		Token keywordToken = expect(TokenKind.KEYWORD, src, context);
		if (keywordToken.getValue() != JSKeyword.BREAK && keywordToken.getValue() != JSKeyword.CONTINUE)
			throw new JSUnexpectedTokenException(keywordToken);
		
		IdentifierTree label = this.parseIdentifierMaybe(src, context);
		
		final SourcePosition start = keywordToken.getStart();
		expectEOL(src, context);
		final SourcePosition end = src.getPosition();
		
		if (keywordToken.getValue() == JSKeyword.BREAK)
			return new AbstractGotoTree.BreakTreeImpl(start, end, label);
		else if (keywordToken.getValue() == JSKeyword.CONTINUE)
			return new AbstractGotoTree.ContinueTreeImpl(start, end, label);
		throw new JSUnexpectedTokenException(keywordToken);
	}
	
	protected IfTree parseIfStatement(JSLexer src, Context context) {
		Token ifKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.IF, src, context);
		
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		
		StatementTree thenStatement = this.parseStatement(src, context);
		StatementTree elseStatement = null;
		
		if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.ELSE)) {
			// This if statement isn't really needed, but it speeds up 'else if'
			// statements by a bit, and else if statements are more common than
			// else statements (IMHO)
			if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.IF))
				elseStatement = this.parseIfStatement(src, context);
			else
				elseStatement = this.parseStatement(src, context);
		}
		
		return new IfTreeImpl(ifKeywordToken.getStart(), src.getPosition(), expression, thenStatement, elseStatement);
	}
	
	protected SwitchTree parseSwitchStatement(JSLexer src, Context context) {
		Token switchKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.SWITCH, src, context);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		src.expect(TokenKind.BRACKET, '{');
		
		ArrayList<SwitchCaseTree> cases = new ArrayList<>();
		Token next;
		while ((next = src.nextTokenIf(TokenKind.KEYWORD)) != null) {
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
			
			cases.add(new SwitchCaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		src.expect(TokenKind.BRACKET, '}');
		cases.trimToSize();
		
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	/**
	 * Parse a try/catch, try/finally, or try/catch/finally statement
	 */
	protected TryTree parseTryStatement(JSLexer src, Context context) {
		Token tryKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.TRY, src, context);
		
		//Read the block that is in the try part
		BlockTree tryBlock = this.parseBlock(src, context);
		
		//Read all catch blocks
		ArrayList<CatchTree> catchBlocks = new ArrayList<>();
		
		Token next;
		while ((next = src.nextTokenIf(TokenKind.IDENTIFIER, "catch")) != null) {
			expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
			IdentifierTree param = this.parseIdentifier(src, context);
			
			//Optional param type
			TypeTree type = this.parseTypeMaybe(src, context, false);
			
			expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
			BlockTree block = parseBlock(null, src, context);
			catchBlocks.add(new CatchTreeImpl(next.getStart(), block.getEnd(), block, param, type));
		}

		//Optional finally block (must come after any & all catch blocks)
		BlockTree finallyBlock = null;
		if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.FINALLY))
			finallyBlock = this.parseBlock(src, context);
		else if (catchBlocks.isEmpty())
			//No catch nor finally blocks
			throw new JSSyntaxException("Incomplete try statement", src.getPosition());
		catchBlocks.trimToSize();
		return new TryTreeImpl(tryKeywordToken.getStart(), src.getPosition(), tryBlock, catchBlocks, finallyBlock);
	}
	
	protected WithTree parseWithStatement(JSLexer src, Context context) {
		Token withKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.WITH, src, context);
		
		if (context.isStrict())
			throw new JSSyntaxException("'with' blocks may not be used in strict mode", withKeywordToken.getStart());
		
		src.expect(JSOperator.LEFT_PARENTHESIS);
		
		context.push();
		context.allowIn(true);
		ExpressionTree expression = this.parseNextExpression(src, context);
		context.pop();
		
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		
		//TODO check if statement is valid (isLabelledFunction)
		return new WithTreeImpl(withKeywordToken.getStart(), src.getPosition(), expression, statement);
	}
	
	// Loops
	
	@JSKeywordParser({JSKeyword.WHILE})
	protected WhileLoopTree parseWhileLoop(JSLexer src, Context context) {
		Token whileKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.WHILE, src, context);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		ExpressionTree condition = this.parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		//Parse loop statement
		context.push().enterLoop();
		StatementTree statement = parseStatement(src, context);
		context.pop();
		
		return new WhileLoopTreeImpl(whileKeywordToken.getStart(), src.getPosition(), condition, statement);
	}
	
	@JSKeywordParser({JSKeyword.DO})
	protected DoWhileLoopTree parseDoWhileLoop(JSLexer src, Context context) {
		Token doKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.DO, src, context);
		
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
	protected LoopTree parseForStatement(JSLexer src, Context context) {
		Token forKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.FOR, src, context);
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
			VariableDeclarationTree declarations = this.parseVariableDeclaration(true, src, context);
			context.pop();
			
			Token next;
			if ((next = src.nextTokenIf(TokenPredicate.IN_OR_OF)) != null) {
				boolean isOf = next.getValue() == JSKeyword.OF;
				
				//TODO: fix ranges
				if (declarations.getDeclarations().size() != 1)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Must have exactly 1 binding", next.getStart());
				if (declarations.getDeclarations().get(0).getInitializer() != null)
					throw new JSSyntaxException("Invalid left-hand side in for-" + (isOf?"of":"in") + " loop: Variable may not have an initializer", declarations.getDeclarations().get(0).getInitializer().getStart());
				
				return this.parsePartialForEachLoopTree(forKeywordToken, declarations, isOf, src, context);
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
				PatternTree left = this.reinterpretExpressionAsPattern(expr, false);
				return this.parsePartialForEachLoopTree(forKeywordToken, left, isOf, src, context);
			}
			
			initializer = this.finishExpressionStatement(expr, src, context);
		}
		
		return this.parsePartialForLoopTree(forKeywordToken, initializer, src, context);
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
			condition = this.parseNextExpression(src, context);
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
	protected ForEachLoopTree parsePartialForEachLoopTree(Token forKeywordToken, VariableDeclarationOrPatternTree pattern, boolean isForEach, JSLexer src, Context context) {
		final ExpressionTree right = isForEach ? this.parseAssignment(src, context) : this.parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		StatementTree statement = this.parseStatement(src, context);
		return new ForEachLoopTreeImpl(forKeywordToken.getStart(), src.getPosition(), pattern, isForEach, right, statement);
	}
	
	
	//SECTION: Expressions

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
	protected ExpressionTree parseNextExpression(JSLexer src, Context context) {
		ExpressionTree result = this.parseAssignment(src, context.coverGrammarIsolated());
		
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
			return result;
		
		ArrayList<ExpressionTree> expressions = new ArrayList<>();
		SourcePosition start = result.getStart();
		expressions.add(result);
		
		do {
			expressions.add(this.parseAssignment(src, context.coverGrammarIsolated()));
		} while (!src.isEOF() && src.nextTokenIf(TokenKind.OPERATOR, JSOperator.COMMA) != null);
			
		return new SequenceExpressionTreeImpl(start, result.getEnd(), expressions);
	}

	/**
	 * Unlike most binary operators, {@code **} has right-associativity, which means:
	 * <ol>
	 * <li>{@code a**b**c} is interperted as {@code a**(b**c)}</li>
	 * <li>A lot of extra code has to be written to handle it</li>
	 * </ol>
	 * @param t
	 * @param src
	 * @param context
	 * @return
	 */
	protected ExpressionTree parseExponentiation(JSLexer src, Context context) {
		final ExpressionTree expr = this.parseUnaryExpression(src, context.coverGrammarIsolated());
		
		Token operatorToken = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.EXPONENTIATION);
		if (operatorToken == null)
			return expr;
		
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		context.push();
		final ExpressionTree right = this.parseExponentiation(src, context.coverGrammarIsolated());
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
					//Ternary expressions handled elsewhere
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
	
	/**
	 * Parse as-cast in form of {@code <expr> as <type>}. This method has been
	 * nerfed (it can take in the expression as a parameter) to make it work
	 * better with {@link #parseBinaryExpression(JSLexer, Context)}.
	 * 
	 * @param src
	 * @param context
	 * @return
	 */
	protected ExpressionTree parseAsCastExpression(ExpressionTree expr, JSLexer src, Context context) {
		while (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS)) {
			TypeTree type = this.parseType(src, context);
			expr = new CastExpressionTreeImpl(expr.getStart(), type.getEnd(), expr, type);
		}
		return expr;
	}
	
	private ExpressionTree reduceBinary(Stack<ExpressionTree> exprs, Stack<Token> operators, ExpressionTree right) {
		ExpressionTree left = exprs.pop();
		Token operator = operators.pop();
		Kind kind = this.mapTokenToBinaryKind(operator);
		
		if (kind == Kind.MEMBER_SELECT || kind == Kind.ARRAY_ACCESS)
			return new MemberExpressionTreeImpl(kind, left, right);
		else if (operator.isOperator() && operator.<JSOperator>getValue().isAssignment())
			throw new JSSyntaxException("This shouldn't be happening", operator.getRange());
		else
			return new BinaryTreeImpl(kind, left, right);
	}
	
	/**
	 * Parse binary expression
	 * @param startToken
	 * @param src
	 * @param context
	 * @return
	 */
	protected ExpressionTree parseBinaryExpression(JSLexer src, Context context) {
		context.isolateCoverGrammar();
		ExpressionTree expr = this.parseExponentiation(src, context);
		context.inheritCoverGrammar();
		
		/*
		 * Consume all 'as' expressions at the start.
		 * In practice, there is no good reason for there to be more than one,
		 * but there *could" be.
		 */
		expr = this.parseAsCastExpression(expr, src, context);
		
		Token token = src.peek();// Candidate for binary operator
		if (this.binaryPrecedence(token, context) < 0)
			return expr;
		
		src.skip(token);
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		//Accumulate-reduce binary trees on stack
		final Stack<Token> operators = new Stack<>();
		final Stack<ExpressionTree> stack = new Stack<>();
		
		
		stack.add(expr);
		operators.add(token);
		
		stack.add(this.parseExponentiation(src, context.coverGrammarIsolated()));
		
		
		for (int precedence; (precedence = binaryPrecedence(src.peek(), context)) >= 0; ) {
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
			ExpressionTree right = stack.pop();
			while ((!operators.isEmpty()) && precedence <= this.binaryPrecedence(operators.peek(), context))
				right = this.reduceBinary(stack, operators, right);
			stack.push(right);
			
			// Consume as-cast expressions
			//TODO: why not just put this in the call-chain before parseExponentiation?
			if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.AS)) {
				ExpressionTree left = stack.pop();
				stack.push(this.parseAsCastExpression(left, src, context));
			}
			
			//Push the newest operator/RHS argument onto their respective stacks
			operators.add(src.nextToken());
			stack.add(this.parseExponentiation(src, context.coverGrammarIsolated()));
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
		while (!stack.isEmpty())
			//TODO: check that we don't need to construct trees here
			expr = this.reduceBinary(stack, operators, expr);
		
		return expr;
	}
	
	protected ExpressionTree parseConditional(JSLexer src, Context context) {
		context.isolateCoverGrammar();
		ExpressionTree expr = this.parseBinaryExpression(src, context);
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
		ExpressionTree concequent = this.parseAssignment(src, context.coverGrammarIsolated());
		context.pop();
		
		expectOperator(JSOperator.COLON, src, context);
		ExpressionTree alternate = this.parseAssignment(src, context.coverGrammarIsolated());
		
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		return new ConditionalExpressionTreeImpl(expr, concequent, alternate);
	}
	
	private ObjectPatternElement reinterpretObjectPropertyAsPattern(ObjectLiteralElement property) {
		switch (property.getKind()) {
			case SPREAD: {
				//TODO: return rest
				throw new JSUnsupportedException("spread -> rest", property.getRange());
			}
			case METHOD_DECLARATION:
			case GET_ACCESSOR_DECLARATION:
			case SET_ACCESSOR_DECLARATION:
				throw new JSSyntaxException("Cannot reinterpret method definition " + ((MethodDeclarationTree) property).getName() + " as a pattern.", property.getStart(), property.getEnd());
			case ASSIGNMENT_PROPERTY:
			case SHORTHAND_ASSIGNMENT_PROPERTY:
				//TODO: finish
				throw new JSUnsupportedException("spread -> rest", property.getRange());
			default:
				throw new IllegalArgumentException("Cannot reinterpret object literal element of kind " + property.getKind());
		}
	}
	
	PatternTree reinterpretExpressionAsPattern(ExpressionTree expr, boolean arrayElement) {
		switch (expr.getKind()) {
			case OBJECT_LITERAL: {
				ObjectLiteralTree obj = (ObjectLiteralTree) expr;
				List<ObjectPatternElement> properties = obj.getProperties()
						.stream()
						.map(this::reinterpretObjectPropertyAsPattern)
						.collect(Collectors.toList());
				return new ObjectPatternTreeImpl(obj.getStart(), obj.getEnd(), properties);
			}
			case ARRAY_LITERAL: {
				ArrayList<PatternTree> elements = new ArrayList<>();
				for (ExpressionTree elem : ((ArrayLiteralTree) expr).getElements())
					elements.add(elem == null ? null : this.reinterpretExpressionAsPattern(elem, true));
				elements.trimToSize();
				return new ArrayPatternTreeImpl(expr.getStart(), expr.getEnd(), elements);
			}
			case SPREAD:
				if (arrayElement) {
					// Spread -> rest in array
					throw new JSUnsupportedException("spread -> rest", expr.getRange());
				}
				break;
			case ASSIGNMENT:
				// return new AssignmentPatternTreeImpl(expr.getStart(), expr.getEnd(), ((AssignmentTree)expr).getVariable(), ((AssignmentTree)expr).getValue());
				break;
			case OBJECT_PATTERN:
			case ARRAY_PATTERN:
			case IDENTIFIER:
			case ARRAY_ACCESS:
			case MEMBER_SELECT:
				return (PatternTree) expr;
			default:
				break;
		}
		throw new JSSyntaxException("Cannot reinterpret " + expr + " as a pattern.", expr.getRange());
	}
	
	protected PatternTree parsePattern(JSLexer src, Context context) {
		//TODO: can we assemble it correctly first pass?
		ExpressionTree expr = this.parsePrimaryExpression(src, context);
		return this.reinterpretExpressionAsPattern(expr, false);
	}
	
	protected ExpressionTree parseAssignment(JSLexer src, Context context) {
		Token lookahead = src.peek();
		
		//Check if this could possibly start a parameter
		if (context.isMaybeParam() && !TokenPredicate.START_OF_PARAMETER.test(lookahead))
			context.isMaybeParam(false);
		
		if (!context.allowYield() && lookahead.matches(TokenKind.KEYWORD, JSKeyword.YIELD))
			return this.parseYield(src, context);
		
		ExpressionTree expr = this.parseConditional(src, context);
		
		//Upgrade to lambda
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LAMBDA))
			//TODO: declared return value?
			return this.finishFunctionBody(expr.getStart(), Modifiers.NONE, null, null, this.reinterpretExpressionAsParameterList(expr), null, true, src, context);
		
		if (!(src.peek().isOperator() && src.peek().<JSOperator>getValue().isAssignment()))
			return expr;
		
		if (!context.isAssignmentTarget())
			throw new JSSyntaxException("Not assignment target", src.getPosition());
		
		Token assignmentOperator = src.nextToken();
		
		PatternTree variable;
		if (assignmentOperator.matches(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) {
			variable = this.reinterpretExpressionAsPattern(expr, false);
		} else {
			//For update-assignment operators (e.g., +=, *=), the LHS can't be a full pattern.
			//LHS can only be an IdentifierTree or MemberExpressionTree
			if (!(expr.getKind() == Kind.IDENTIFIER || expr.getKind() == Kind.MEMBER_SELECT || expr.getKind() == Kind.ARRAY_ACCESS))
				throw new JSSyntaxException("Update assignment LHS cannot be arbitrary pattern", expr.getRange());
			
			variable = (PatternTree) expr;//IdentifierTree/MemberExpressionTree already PatternTree's
			
			//No longer assignment/binding target
			context.isAssignmentTarget(false);
			context.isBindingElement(false);
		}
		
		
		final ExpressionTree right = this.parseAssignment(src, context.coverGrammarIsolated());
		return new AssignmentTreeImpl(lookahead.getStart(), right.getEnd(), this.mapTokenToBinaryKind(assignmentOperator), variable, right);
	}
	
	protected ParameterTree parseParameter(JSLexer src, Context context) {
		Token lookahead = src.peek();
		SourcePosition start = lookahead.getStart();
		
		if (lookahead.matches(TokenKind.KEYWORD, JSKeyword.THIS)) {
			// 'fake' this-parameter
			dialect.require("ts.types", lookahead.getRange());
			IdentifierTree name = new IdentifierTreeImpl(src.nextToken());
			TypeTree type = this.parseTypeMaybe(src, context, true);
			// Initializers not allowed
			lookahead = src.peek();
			if (lookahead.matches(TokenKind.OPERATOR, JSOperator.EQUAL))
				throw new JSSyntaxException("This-parameters may not have default values", lookahead.getRange());
			
			return new ParameterTreeImpl(start, src.getPosition(), Modifiers.NONE, name, false, type, null);
		}
		
		Modifiers modifiers = this.parseModifiers(Modifiers.union(Modifiers.MASK_VISIBILITY, Modifiers.READONLY), true, src, context);
		
		boolean rest = src.nextTokenIs(TokenKind.OPERATOR, JSOperator.SPREAD);
		PatternTree name = this.parsePattern(src, context);
		
		// Parse postfix modifiers
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.union(Modifiers.OPTIONAL, Modifiers.DEFINITE), false, src, context));
		
		// Parse type declaration
		TypeTree type = this.parseTypeMaybe(src, context, true);
		
		// Parse initializer
		ExpressionTree initializer = null;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.EQUAL))
			initializer = this.parseAssignment(src, context);
		
		return new ParameterTreeImpl(start, src.getPosition(), modifiers, name, rest, type, initializer);
	}
	
	/**
	 * Parse function parameters. This method will consume all parameters up to
	 * a closing (right) parenthesis (which will also be consumed).
	 * 
	 * Note: parameters are in the function <i>declaration</i>, while arguments
	 * are used when invoking a function.
	 * <br/>
	 * This method is pretty permissive in what it will accept; more stringent validation is done in the VALIDATION pass.
	 * 
	 * @param previous
	 *            Previous parameters parsed, if available. Null if no
	 *            parameters were parsed for the same list before this method
	 *            was called
	 *            TODO clarify
	 */
	protected List<ParameterTree> parseParameters(List<ParameterTree> previous, JSLexer src, Context context) {
		if (src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS))
			return Collections.emptyList();
		
		ArrayList<ParameterTree> result = new ArrayList<>();
		
		if (previous != null && !previous.isEmpty()) {
			result.addAll(previous);
		}
		
		result.addAll(this.parseDelimitedList(this::parseParameter, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS), src, context));
		
		//Expect to end with a right paren
		expect(src.peek(), TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src);
		
		//Compress ArrayList (not strictly needed, but why not?)
		result.trimToSize();
		return result;
	}
	
	/**
	 * Upgrade a group expression to a lambda function
	 */
	protected FunctionExpressionTree upgradeGroupToLambdaFunction(SourcePosition startPos, List<ExpressionTree> expressions, ExpressionTree lastParam, JSLexer src, Context context) {
		dialect.require("js.function.lambda", new SourceRange(startPos, src.getPosition()));
		
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
			dialect.require("ts.parameter.optional", optionalToken.getRange());
		
		//Parse type declaration, if exists
		TypeTree type = this.parseTypeMaybe(src, context, false);
		
		//Parse default value, if exists
		ExpressionTree defaultValue = ((optionalToken == null && type == null) || src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT)) ? this.parseAssignment(src, context) : null;
		
		Modifiers modifiers = Modifiers.NONE;
		if (optionalToken != null)
			modifiers = modifiers.combine(Modifiers.OPTIONAL);
		
		parameters.add(new ParameterTreeImpl(lastParam.getStart(), src.getPosition(), modifiers, (IdentifierTree) lastParam, false, type, defaultValue));
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA))
			parameters = this.parseParameters(parameters, src, context);
		
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		TypeTree returnType = this.parseTypeMaybe(src, context, false);
		
		expectOperator(JSOperator.LAMBDA, src, context);
		
		return this.finishFunctionBody(startPos, modifiers, null, null, parameters, returnType, true, src, context);
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
	protected ExpressionTree parseGroupExpression(JSLexer src, Context context) {
		Token leftParenToken = expect(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS, src, context);
		
		//Check for easy upgrades to lambda expression
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			//Is lambda w/ no args ("()=>???")
			dialect.require("js.function.lambda", leftParenToken.getRange());
			expectOperator(JSOperator.LAMBDA, src, context);
			return this.finishFunctionBody(leftParenToken.getStart(), Modifiers.NONE, null, null, Collections.emptyList(), null, true, src, context);
		} else if (src.peek().matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			//Lambda w/ 1 rest operator
			dialect.require("js.function.lambda", leftParenToken.getRange());
			dialect.require("js.parameter.rest", src.peek().getRange());
			List<ParameterTree> param = reinterpretExpressionAsParameterList(this.parseSpread(src, context));
			expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
			expectOperator(JSOperator.LAMBDA, src, context);
			return this.finishFunctionBody(leftParenToken.getStart(), Modifiers.NONE, null, null, param, null, true, src, context);
		}
		
		context.isBindingElement(true);
		context.isolateCoverGrammar();
		context.isMaybeParam(true);
		ExpressionTree expr = this.parseAssignment(src, context);
		context.inheritCoverGrammar();
		
		
		if (TokenPredicate.PARAMETER_TYPE_START.test(src.peek())) {
			//Lambda expression where the first parameter has an explicit type/is optional/has default value
			
			return this.upgradeGroupToLambdaFunction(leftParenToken.getStart(), null, expr, src, context);
		}
		
		//There are multiple expressions here
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA)) {
			List<Tree> expressions = new ArrayList<>();
			expressions.add(expr);
			
			do {
				if (src.peek().matches(TokenKind.OPERATOR, JSOperator.SPREAD)) {
					dialect.require("js.parameter.rest", leftParenToken.getRange());
					//Rest parameter. Must be lambda expression
					expressions.add(this.parseSpread(src, context));
					
					//Upgrade to lambda
					dialect.require("js.function.lambda", leftParenToken.getRange());
					List<ParameterTree> params = new ArrayList<>(expressions.size());
					for (ExpressionTree expression : (List<ExpressionTree>)(List<?>)expressions)
						params.add(reinterpretExpressionAsParameter(expression));
					//The rest parameter must be the last one
					expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
					expectOperator(JSOperator.LAMBDA, src, context);
					return this.finishFunctionBody(leftParenToken.getStart(), Modifiers.NONE, null, null, params, null, true, src, context);
				} else {
					context.isolateCoverGrammar();
					context.isMaybeParam(true);
					final ExpressionTree expression = this.parseAssignment(src, context);
					context.inheritCoverGrammar();
					
					
					// Check for declared types (means its a lambda param)
					Token lookahead = src.peek();
					if (lookahead.matches(TokenKind.OPERATOR, JSOperator.QUESTION_MARK) || lookahead.matches(TokenKind.OPERATOR, JSOperator.COLON))
						return upgradeGroupToLambdaFunction(leftParenToken.getStart(), (List<ExpressionTree>)(List<?>)expressions, expression, src, context);
					
					
					expressions.add(expression);
				}
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			
			//Ensure that it exited the loop with a closing paren
			expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
			
			//Sequence, but not lambda
			return new ParenthesizedTreeImpl(leftParenToken.getStart(), src.getPosition(), new SequenceExpressionTreeImpl((List<ExpressionTree>)(List<?>)expressions));
		}
		//Only one expression
		expect(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS, src, context);
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LAMBDA)) {
			//Upgrade to lambda
			dialect.require("js.function.lambda", leftParenToken.getRange());
			List<ParameterTree> params = this.reinterpretExpressionAsParameterList(expr);
			return this.finishFunctionBody(leftParenToken.getStart(), Modifiers.NONE, null, null, params, null, true, src, context);
		}
		//Not a lambda, just some parentheses around some expression.
		return new ParenthesizedTreeImpl(leftParenToken.getStart(), src.getPosition(), expr);
	}
	
	protected SpreadElementTree parseSpread(JSLexer src, Context context) {
		Token spreadToken = expect(TokenKind.OPERATOR, JSOperator.SPREAD, src, context);
		dialect.require("js.operator.spread", spreadToken.getRange());
		
		context.isolateCoverGrammar();
		final ExpressionTree expr = this.parseAssignment(src, context);
		context.inheritCoverGrammar();
		
		return new SpreadElementTreeImpl(spreadToken.getStart(), expr.getEnd(), expr);
	}

	protected ExpressionTree parseNew(JSLexer src, Context context) {
		Token newKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.NEW, src, context);
		Token t;
		
		//Support 'new.target'
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.PERIOD)) != null) {
			Token r = src.nextToken();
			if (context.inFunction() && r.matches(TokenKind.IDENTIFIER, "target")) {
				//See developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/new.target
				IdentifierTree idNew = new IdentifierTreeImpl(newKeywordToken.getStart(), newKeywordToken.getEnd(), "new");
				IdentifierTree idTarget = new IdentifierTreeImpl(r);
				return new BinaryTreeImpl(Tree.Kind.MEMBER_SELECT, idNew, idTarget);
			}
			
			throw new JSUnexpectedTokenException(t);
		}
		
		ExpressionTree callee = this.parseLeftSideExpression(false, src, context.coverGrammarIsolated());
		
		final List<ExpressionTree> args;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) != null)
			args = parseArguments(t, src, context);
		else
			args = Collections.emptyList();
		
		return new NewTreeImpl(newKeywordToken.getStart(), src.getPosition(), callee, args);
	}
	
	protected ExpressionTree parseLeftSideExpression(boolean allowCall, JSLexer src, Context context) {
		boolean prevAllowIn = context.allowIn();
		context.allowIn(true);
		
		ExpressionTree expr;
		if (context.inFunction() && src.peek().matches(TokenKind.KEYWORD, JSKeyword.SUPER))
			expr = this.parseSuper(src, context);
		else {
			context.isolateCoverGrammar();
			if (src.peek().matches(TokenKind.KEYWORD, JSKeyword.NEW))
				expr = this.parseNew(src, context);
			else
				expr = this.parsePrimaryExpression(src, context);
			context.inheritCoverGrammar();
		}
		
		while (true) {
			Token lookahead = src.peek();
			if (src.nextTokenIs(TokenKind.BRACKET, '[')) {
				//Computed member access expressions
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = this.parseNextExpression(src, context.coverGrammarIsolated());
				expect(TokenKind.BRACKET, ']', src, context);
				expr = new MemberExpressionTreeImpl(expr.getStart(), src.getPosition(), Kind.ARRAY_ACCESS, expr, property);
			} else if (allowCall && lookahead.matches(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS)) {
				//Function call
				context.isBindingElement(false);
				context.isAssignmentTarget(false);
				expr = this.parseFunctionCall(expr, null, src, context);
			} else if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.PERIOD)) {
				//Static member access
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = this.parseIdentifier(src, context);
				expr = new MemberExpressionTreeImpl(expr.getStart(), src.getPosition(), Kind.MEMBER_SELECT, expr, property);
			} else if (lookahead.getKind() == TokenKind.TEMPLATE_LITERAL && lookahead.<TemplateTokenInfo>getValue().head) {
				//TODO Tagged template literal
				return this.parseLiteral(null, src, context);
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
		List<? extends ExpressionTree> arguments = this.parseArguments(openParenToken, src, context);
		
		return new FunctionCallTreeImpl(functionSelectExpression.getStart(), src.getPosition(), functionSelectExpression, arguments);
	}
	
	/**
	 * Coerces {@code identifierToken} to an identifier, if possible in the current context.
	 * @param identifierToken
	 * @param src
	 * @param context
	 * @return
	 * @throws JSSyntaxException If {@code identifierToken} is an invalid 
	 */
	protected String asIdentifier(Token identifierToken, JSLexer src, Context context) throws JSSyntaxException {
		//'yield' can be an identifier if yield expressions aren't allowed in the current context
		if (identifierToken.matches(TokenKind.KEYWORD, JSKeyword.YIELD)) {
			if (context.allowYield())
				throw new JSSyntaxException("'yield' not allowed as identifier", identifierToken.getRange());
			else
				identifierToken = identifierToken.reinterpretAsIdentifier();
		}
		
		expect(identifierToken, TokenKind.IDENTIFIER, src);
		
		//Check that our identifier has a valid name
		String name = identifierToken.getValue();
		
		//'await' not allowed when in a context that it could be a keyword.
		if (context.allowAwait() && name.equals("await")) {
			throw new JSSyntaxException("'await' not allowed as indentifier in context", identifierToken.getRange());
		}
		
		//'arguments' and 'eval' not allowed as identifiers in strict mode
		//TODO support 'implements', 'interface', 'let', 'package', 'private', 'protected', 'public', 'static' as illegal strict mode identifiers
		//TODO: aren't we checking for 'yield' twice?
		if (context.isStrict() && (name.equals("arguments") || name.equals("eval") || name.equals("yield"))) {
			throw new JSSyntaxException("'" + name + "' not allowed as identifier in strict mode", identifierToken.getRange());
		}
		
		return name;
	}
	
	protected IdentifierTree parseIdentifierMaybe(JSLexer src, Context context) {
		//Keep reference to lookahead (if applicable) to skip over it at the end
		Token lookahead = src.peek();
		
		String name;
		try {
			name = this.asIdentifier(lookahead, src, context);
		} catch (JSSyntaxException e) {
			return null;
		}
		
		//Skip lookahead
		src.skip(lookahead);
		
		return new IdentifierTreeImpl(lookahead.getStart(), lookahead.getEnd(), name);
	}
	
	protected IdentifierTree parseIdentifier(JSLexer src, Context context) {
		return this.parseIdentifier(src.nextToken(), src, context);
	}
	
	//TODO: drop support for signature
	@Deprecated
	protected IdentifierTree parseIdentifier(Token identifierToken, JSLexer src, Context context) {
		if (identifierToken == null)
			identifierToken = src.nextToken();
		
		String name = this.asIdentifier(identifierToken, src, context);
		
		return new IdentifierTreeImpl(identifierToken.getStart(), identifierToken.getEnd(), name);
	}
	
	
	protected ThisExpressionTree parseThis(JSLexer src, Context ctx) {
		return new ThisExpressionTreeImpl(expect(TokenKind.KEYWORD, JSKeyword.THIS, src, ctx));
	}
	
	protected SuperExpressionTree parseSuper(JSLexer src, Context context) {
		SuperExpressionTree result = new SuperExpressionTreeImpl(expect(TokenKind.KEYWORD, JSKeyword.SUPER, src, context));
		Token tmp = src.peek();
		if (!(tmp.matches(TokenKind.BRACKET, '[') || tmp.matches(TokenKind.OPERATOR, JSOperator.PERIOD)))
			throw new JSUnexpectedTokenException(tmp);
		return result;
	}
	
	//Function stuff
	
	protected StatementTree parseFunctionBody(Modifiers modifiers, boolean arrow, JSLexer src, Context context) {
		//Update context for function
		if (modifiers.isGenerator())
			context.pushGenerator();
		else
			context.pushFunction();
		
		if (modifiers.isAsync())
			context.allowAwait(true);
		
		StatementTree body;
		if (src.peek().matches(TokenKind.BRACKET, '{')) {
			body = this.parseBlock(src, context);
		} else if (arrow) {
			body = new ReturnTreeImpl(this.parseNextExpression(src, context.coverGrammarIsolated()));
		} else {
			body = null;
		}
		context.pop();
		return body;
	}
	
	protected FunctionExpressionTree finishFunctionBody(SourcePosition start, Modifiers modifiers, IdentifierTree identifier, List<TypeParameterDeclarationTree> generics, List<ParameterTree> parameters, TypeTree returnType, boolean arrow, JSLexer src, Context context) {
		//Read function body
		StatementTree body = this.parseFunctionBody(modifiers, arrow, src, context);
		if (body == null) {
			//TODO: move to validation pass?
			throw new JSSyntaxException("Functions must have a body", src.getPosition());
		}
		
		// Get this before we pop the context
		if (context.isStrict())
			modifiers = modifiers.combine(Modifiers.STRICT);
		
		//You can't assign to a function
		context.isAssignmentTarget(false);
		context.isBindingElement(false);
		
		return new FunctionExpressionTreeImpl(start, src.getPosition(), modifiers, identifier, generics, parameters, returnType, arrow, body);
	}
	
	protected FunctionExpressionTree parseFunctionExpression(JSLexer src, Context context) {
		Token functionKeywordToken = src.nextToken();
		
		SourcePosition startPos = functionKeywordToken.getStart();
		
		Modifiers modifiers = Modifiers.NONE;
		
		//The first token could be an async modifier
		if (functionKeywordToken.matches(TokenKind.IDENTIFIER, "async")) {
			dialect.require("js.function.async", functionKeywordToken.getRange());
			modifiers = modifiers.combine(Modifiers.ASYNC);
			functionKeywordToken = src.nextToken();
		}
		
		
		//functionKeywordToken should be function or function*.
		if (!functionKeywordToken.isKeyword())
			throw new JSUnexpectedTokenException(functionKeywordToken);
		
		if (functionKeywordToken.getValue() == JSKeyword.FUNCTION_GENERATOR) {
			dialect.require("js.function.generator", functionKeywordToken.getRange());
			modifiers = modifiers.combine(Modifiers.GENERATOR);
		} else if (functionKeywordToken.getValue() != JSKeyword.FUNCTION) {
			throw new JSUnexpectedTokenException(functionKeywordToken);
		}
		
		
		//Parse function identifier
		IdentifierTree identifier = null;
		Token next = src.nextTokenIf(TokenKind.IDENTIFIER);
		if (next != null)
			identifier = new IdentifierTreeImpl(next);
		
		//Parse generics (if available)
		List<TypeParameterDeclarationTree> generics = this.parseTypeParametersMaybe(src, context);
		if (generics.isEmpty())
			generics = null;//Save space.
		
		//Parse parameters
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = this.parseParameters(null, src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		//Get return type, if provided
		TypeTree returnType = this.parseTypeMaybe(src, context, true);
		
		//Finish body
		return this.finishFunctionBody(startPos, modifiers, identifier, generics, params, returnType, false, src, context);
	}
	
	protected FunctionDeclarationTree reinterpretFunctionAsDeclaration(FunctionExpressionTree expr, JSLexer src, Context context) {
		//TODO: assert that this is good (e.g., not an arrow fn)
		return new FunctionDeclarationTreeImpl(expr.getStart(), expr.getEnd(), expr.getModifiers(), (IdentifierTree) expr.getName(), expr.getTypeParameters(), expr.getParameters(), expr.getReturnType(), expr.getBody());
	}
	
	protected FunctionDeclarationTree parseFunctionDeclaration(JSLexer src, Context context) {
		// Parse as expression, then reinterpret
		//TODO: find a better way to do this
		FunctionExpressionTree expr = this.parseFunctionExpression(src, context);
		
		return this.reinterpretFunctionAsDeclaration(expr, src, context);
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
				values.add(this.parseSpread(src, context));
				if (!src.peek().matches(TokenKind.BRACKET, ']')) {
					context.isAssignmentTarget(false);
					context.isBindingElement(false);
				}
			} else {
				context.isolateCoverGrammar();
				values.add(this.parseAssignment(src, context));
				context.inheritCoverGrammar();
			}
			
			if (!src.peek().matches(TokenKind.BRACKET, ']'))
				expect(TokenKind.OPERATOR, JSOperator.COMMA, src, context);
		}
		
		expect(TokenKind.BRACKET, ']', src, context);
		
		values.trimToSize();
		
		return new ArrayLiteralTreeImpl(startToken.getStart(), src.getPosition(), values);
	}
	
	protected PropertyName parsePropertyName(JSLexer src, Context context) {
		Token lookahead = src.peek();
		SourcePosition start = lookahead.getStart();
		IdentifierTree id = this.parseIdentifierMaybe(src, context);
		if (id != null)
			return id;
		
		switch (lookahead.getKind()) {
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
				return (PropertyName) this.parseLiteral(src.skip(lookahead), src, context);
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
				return new IdentifierTreeImpl(src.skip(lookahead).reinterpretAsIdentifier());
			case IDENTIFIER:
			case KEYWORD:
				return this.parseIdentifier(src, context);
			case BRACKET:
				//Computed property
				if ((char) lookahead.getValue() == '[') {
					src.skip(lookahead);
					ExpressionTree expr = this.parseNextExpression(src, context);
					expect(TokenKind.BRACKET, ']', src, context);
					return new ComputedPropertyKeyTreeImpl(start, src.getPosition(), expr);
				}
				break;
			default:
				break;
		}
		
		throw new JSUnexpectedTokenException(lookahead);
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
	
	protected MethodDeclarationTree parseMethodDeclaration(SourcePosition startPos, List<DecoratorTree> decorators, Modifiers modifiers, PropertyName name, JSLexer src, Context context) {
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = this.parseParameters(null, src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		TypeTree returnType = this.parseTypeMaybe(src, context, false);
		
		StatementTree body = this.parseFunctionBody(modifiers, false, src, context);
		if (body == null)
			expectEOL(src, context);
		
		return new MethodDeclarationTreeImpl(startPos, src.getPosition(), modifiers, name, typeParams, params, returnType, body);
	}
	
	protected ObjectLiteralElement parseObjectProperty(JSLexer src, Context context) {
		final SourcePosition start = src.getPosition();
		
		if (src.peek().matchesOperator(JSOperator.SPREAD)) {
			return this.parseSpread(src, context);
		}
		
		List<DecoratorTree> decorators = this.parseDecorators(src, context);
		Modifiers filter = Modifiers.union(Modifiers.ASYNC, Modifiers.GETTER, Modifiers.SETTER, Modifiers.STATIC, Modifiers.DECLARE, Modifiers.MASK_VISIBILITY);
		Modifiers modifiers = this.parseModifiers(filter, true, src, context);
		
		if (modifiers.isGetter() || modifiers.isSetter())
			return this.parseAccessorDeclaration(decorators, modifiers, src, context);
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.MULTIPLICATION))
			modifiers = modifiers.combine(Modifiers.GENERATOR);
		
		PropertyName name = this.parsePropertyName(src, context);
		
		// Parse optional/definite postfix modifiers 
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		Token lookahead = src.peek();
		if (modifiers.isGenerator() || lookahead.matchesOperator(JSOperator.LESS_THAN) || lookahead.matchesOperator(JSOperator.LEFT_PARENTHESIS)) {
			// Method declaration
			return this.parseMethodDeclaration(start, decorators, modifiers, name, src, context);
		} else if (name.getKind() == Kind.IDENTIFIER
				&& (lookahead.matchesOperator(JSOperator.COMMA)
						|| lookahead.matchesOperator(JSOperator.EQUAL)
						|| lookahead.matches(TokenKind.BRACKET, '}'))) {
			// Shorthand property assignment
			// <name> [= <initializer>]
			ExpressionTree initializer = null;
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.EQUAL))
				initializer = this.parseAssignment(src, context);
			
			return new ShorthandAssignmentPropertyTreeImpl(start, src.getPosition(), modifiers, (IdentifierTree) name, initializer);
		} else {
			// Normal property assignment
			// Form <name>: <value>
			expectOperator(JSOperator.COLON, src, context);
			ExpressionTree value = this.parseAssignment(src, context.pushed().allowIn(true));
			return new AssignmentPropertyTreeImpl(start, src.getPosition(), modifiers, name, value);
		}
	}
	
	/**
	 * Parse object literal.
	 */
	protected ObjectLiteralTree parseObjectInitializer(JSLexer src, Context context) {
		Token startToken = expect(TokenKind.BRACKET, '{', src, context);
		
		ArrayList<ObjectLiteralElement> properties = new ArrayList<>();
		
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
	 */
	protected ExpressionTree parseUnaryExpression(JSLexer src, Context context) {
		Token lookahead = src.peek();
		
		// Special cases
		if (lookahead.matches(TokenKind.KEYWORD, JSKeyword.YIELD))
			return this.parseYield(src, context);
		if (lookahead.matches(TokenKind.OPERATOR, JSOperator.SPREAD))
			return this.parseSpread(src, context);
		
		Tree.Kind kind = null;
		boolean updates = false;
		switch (lookahead.getKind()) {
			case KEYWORD:
				switch (lookahead.<JSKeyword>getValue()) {
					case VOID:
						kind = Tree.Kind.VOID;
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
				switch (lookahead.<JSOperator>getValue()) {
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
					case INCREMENT:
						kind = Tree.Kind.PREFIX_INCREMENT;
						updates = true;
						break;
					case DECREMENT:
						kind = Tree.Kind.PREFIX_DECREMENT;
						updates = false;
						break;
					case LESS_THAN: {
						//Angle bracket casting
						dialect.require("ts.types.cast", lookahead.getRange());
						SourcePosition start = src.skip(lookahead).getStart();
						TypeTree type = this.parseType(src, context);
						expect(TokenKind.OPERATOR, JSOperator.GREATER_THAN, src, context);
						ExpressionTree rhs = this.parseBinaryExpression(src, context);
						return new CastExpressionTreeImpl(start, src.getPosition(), rhs, type);
					}
					default:
						break;
				}
				break;
			default:
				break;
		}
		
		if (kind == null)
			return this.parseUnaryPostfix(src, context);
		
		SourcePosition start = src.skip(lookahead).getStart();
		
		ExpressionTree expression;
		if (kind == Kind.VOID && src.peek().getKind() == TokenKind.SPECIAL)
			expression = null;
		else
			expression = this.parseUnaryExpression(src, context.coverGrammarIsolated());
		
		if (updates) {
			//Check if the target can be modified
			if (!Validator.canBeAssigned(expression, dialect))
				throw new JSSyntaxException("Invalid right-hand side expression in " + kind + " expression", start, src.getPosition());
		} else if (context.isStrict() && kind == Tree.Kind.DELETE && expression.getKind() == Tree.Kind.IDENTIFIER) {
			//TODO: move to validation pass?
			String identName = ((IdentifierTree) expression).getName();
			throw new JSSyntaxException("Cannot delete unqualified identifier " + identName + " in strict mode", start, src.getPosition());
		}
		
		return new UnaryTreeImpl(start, src.getPosition(), expression, kind);
	}
	
	protected ExpressionTree parseUnaryPostfix(JSLexer src, Context context) {
		ExpressionTree expr = this.parseLeftSideExpression(true, src, context.pushed());
		
		Token operatorToken = src.nextTokenIf(t->(t.isOperator() && (t.getValue() == JSOperator.INCREMENT || t.getValue() == JSOperator.DECREMENT)));
		if (operatorToken == null)
			return expr;
		
		if (!Validator.canBeAssigned(expr, dialect))
			throw new JSSyntaxException("Invalid left-hand side expression in " + operatorToken.getKind() + " expression", expr.getRange());
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
	protected UnaryTree parseYield(JSLexer src, Context context) {
		Token yieldKeywordToken = expect(TokenKind.KEYWORD, JSKeyword.YIELD, src, context);
		
		dialect.require("js.yield", yieldKeywordToken.getRange());
		
		//Check if it's a 'yield*'
		boolean delegates = src.nextTokenIs(TokenKind.OPERATOR, JSOperator.MULTIPLICATION);
		
		//Parse RHS of expression
		ExpressionTree argument = delegates
				|| !(src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)
				|| src.peek().matches(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)
				|| src.peek().matches(TokenKind.BRACKET, '}')) ? this.parseAssignment(src, context) : null;
		
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
		
		public boolean isDirectiveTarget() {
			return data.isDirectiveTarget;
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
		 * the use of {@code return} statements.
		 * 
		 * @return self
		 */
		public Context pushFunction() {
			this.data = new ContextData(this.data, false);
			this.data.inFunction = true;
			return this;
		}

		/**
		 * Marks this level of the context as being in a switch statement.
		 * Allows the use of {@code break}.
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
		 * addition to {@code yield} and {@code yield*}.
		 * 
		 * @return self
		 */
		public Context pushGenerator() {
			this.data = new ContextData(this.data, false);
			data.inGenerator = true;
			return this;
		}

		/**
		 * Marks this level of the context as being in a loop. Allows
		 * {@code break} and {@code continue} statements.
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
		
		public Context setDirectiveTarget(boolean value) {
			data.isDirectiveTarget = value;
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
		
		public Context registerStatementLabel(String name, SourcePosition position) {
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
			boolean isDirectiveTarget = false;
			/**
			 * Whether the parser is currently inside a function statement.
			 * Allows the {@code return} keyword.
			 */
			boolean inFunction = false;
			/**
			 * Whether the parser is currently inside a switch statement. Allows
			 * the {@code break} keyword.
			 */
			boolean inSwitch = false;
			/**
			 * True if the parser is in a generator.
			 * Allows the {@code yield} and {@code yield*} keywords.
			 */
			boolean inGenerator = false;
			/**
			 * Whether the parser is currently inside a loop statement. Allows
			 * the {@code break} and {@code continue} statements.
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
			 * Set of statement labels
			 */
			Set<String> labels;

			public ContextData() {
				this(null, false);
			}
			
			public ContextData(ContextData parent) {
				this(parent, true);
			}

			public ContextData(ContextData parent, boolean inherit) {
				this.parent = parent;
				if (inherit)
					this.inheritFrom(parent);
			}
			
			private void inheritFrom(ContextData source) {
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
			}
		}
	}
}