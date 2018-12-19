package com.mindlin.jsast.impl.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import com.mindlin.jsast.impl.tree.AbstractClassTree.ClassDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractClassTree.ClassExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractFunctionTree.FunctionDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractFunctionTree.FunctionExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractFunctionTree.MethodDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractGotoTree;
import com.mindlin.jsast.impl.tree.AbstractSignatureDeclarationTree.CallSignatureTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractSignatureDeclarationTree.ConstructSignatureTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractSignatureDeclarationTree.ConstructorTypeTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractSignatureDeclarationTree.FunctionTypeTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ArrayTypeTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentPropertyTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentPropertyTreeImpl.ShorthandAssignmentPropertyTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.BooleanLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.CastExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.CatchTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.CompositeTypeTreeImpl;
import com.mindlin.jsast.impl.tree.ComputedPropertyKeyTreeImpl;
import com.mindlin.jsast.impl.tree.ConditionalExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ConditionalTypeTreeImpl;
import com.mindlin.jsast.impl.tree.DebuggerTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExportTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ForEachLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ForLoopTreeImpl;
import com.mindlin.jsast.impl.tree.FunctionCallTreeImpl;
import com.mindlin.jsast.impl.tree.HeritageClauseTreeImpl;
import com.mindlin.jsast.impl.tree.HeritageExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTypeTreeImpl;
import com.mindlin.jsast.impl.tree.IfTreeImpl;
import com.mindlin.jsast.impl.tree.ImportDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.ImportSpecifierTreeImpl;
import com.mindlin.jsast.impl.tree.IndexSignatureTreeImpl;
import com.mindlin.jsast.impl.tree.InterfaceDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.LabeledStatementTreeImpl;
import com.mindlin.jsast.impl.tree.LineMap;
import com.mindlin.jsast.impl.tree.LiteralTypeTreeImpl;
import com.mindlin.jsast.impl.tree.MappedTypeTreeImpl;
import com.mindlin.jsast.impl.tree.MemberExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.MemberTypeTreeImpl;
import com.mindlin.jsast.impl.tree.MethodSignatureTreeImpl;
import com.mindlin.jsast.impl.tree.NewTreeImpl;
import com.mindlin.jsast.impl.tree.NullLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectTypeTreeImpl;
import com.mindlin.jsast.impl.tree.ParameterTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.PropertyDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.PropertySignatureTreeImpl;
import com.mindlin.jsast.impl.tree.RegExpLiteralTreeImpl;
import com.mindlin.jsast.impl.tree.ReturnTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.SpecialTypeTreeImpl;
import com.mindlin.jsast.impl.tree.SpreadElementTreeImpl;
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
import com.mindlin.jsast.impl.tree.TypeParameterDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl.AwaitTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTypeTreeImpl;
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
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DecoratorTree;
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
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.HeritageExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.ImportDeclarationTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.LoopTree;
import com.mindlin.jsast.tree.MethodDeclarationTree;
import com.mindlin.jsast.tree.MethodSignatureTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ObjectLiteralElement;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree.ObjectPatternElement;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.PropertyDeclarationTree;
import com.mindlin.jsast.tree.PropertyName;
import com.mindlin.jsast.tree.PropertySignatureTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree.CallSignatureTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree.ConstructSignatureTree;
import com.mindlin.jsast.tree.SpreadElementTree;
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
import com.mindlin.jsast.tree.VariableDeclarationTree.VariableDeclarationKind;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.EnumDeclarationTree;
import com.mindlin.jsast.tree.type.EnumMemberTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.LiteralTypeTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
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
	
	private static Token expectKeyword(JSKeyword keyword, JSLexer src, Context context) {
		return expect(TokenKind.KEYWORD, keyword, src, context);
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
	
	private static boolean lookahead(BiPredicate<JSLexer, Context> tester, JSLexer src, Context context) {
		src.mark();
		boolean result = tester.test(src, context);
		src.reset();
		return result;
	}
	
	private static <T> T tryParse(ParseFunction<T> parser, LookaheadPredicate predicate, JSLexer src, Context context) {
		src.mark();
		T result = parser.apply(src, context);
		if (predicate.test(src, context)) {
			src.unmark();
			return result;
		} else {
			src.reset();
			return null;
		}
	}
	
	private static <T> T tryParse(ParseFunction<Optional<T>> parser, JSLexer src, Context context) {
		src.mark();
		Optional<T> result = parser.apply(src, context);
		if (result.isPresent()) {
			src.unmark();
			return result.get();
		} else {
			src.reset();
			return null;
		}
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
						return this.parseClassDeclaration(src.getNextStart(), Collections.emptyList(), Modifiers.NONE, src, context);
					case DO:
						return this.parseDoWhileLoop(src, context);
					case EXPORT:
						return this.parseExportStatement(src, context);
					case FOR:
						return this.parseForStatement(src, context);
					case FUNCTION:
						return this.parseFunctionDeclaration(src, context);
					case IF:
						return this.parseIfStatement(src, context);
					case IMPORT:
						return this.parseImportStatement(src, context);
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
					case INTERFACE:
						if (this.isIdentifier(src.peek(1), context))
							return this.parseInterfaceDeclaration(src, context);
						break;
					case TYPE:
						if (this.isIdentifier(src.peek(1), context))//TODO: allow await/yield?
							return this.parseDeclaration(src, context);
						break;
					case LET:
						// Let is a strict-mode keyword, so it might be used as an identifier
						if (this.isIdentifier(src.peek(1), context) || src.peek(1).matchesOperator(JSOperator.LEFT_BRACKET) || src.peek(1).matchesOperator(JSOperator.LEFT_BRACE))
							return this.parseDeclaration(src, context);
						break;
					case VAR:
					case CONST:
						return this.parseVariableDeclaration(false, src, context);
					case ENUM:
						// Always indicate start of declaration
						return this.parseDeclaration(src, context);
					case ABSTRACT:
					case ASYNC:
					case DECLARE:
					case PUBLIC:
					case PROTECTED:
					case PRIVATE:
					case READONLY:
					case STATIC:
						// Strict-mode keywords, otherwise muddle through with ASI
						if (context.isStrict() || !src.peek(1).hasPrecedingNewline())
							return this.parseDeclaration(src, context);
						break;
						
					// case INTERFACE:
					// case TYPE:
					case CASE:
					case FINALLY:
					case DEFAULT:
					case ELSE:
					case EXTENDS:
					case IN:
					case INSTANCEOF:
						throw new JSSyntaxException("Unexpected keyword " + lookahead.getValue(), lookahead.getRange());
					case AS:
					case AWAIT:
					case CONSTRUCTOR:
					case FROM:
					case IMPLEMENTS:
					case OF:
					case PACKAGE:
					default:
						break;
				}
				return this.parseLabeledOrExpressionStement(src, context);
			}
			case OPERATOR:
				switch (lookahead.<JSOperator>getValue()) {
					case AT_SYMBOL:
						return this.parseDeclaration(src, context);
					case LEFT_BRACE:
						return this.parseBlock(src, context);
					default:
						return this.parseExpressionStatement(src, context);
				}
			case IDENTIFIER:
				return this.parseLabeledOrExpressionStement(src, context);
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
	
	protected StatementTree parseLabeledOrExpressionStement(JSLexer src, Context context) {
		Token la2 = src.peek(1);
		if (this.isIdentifier(src.peek(), context) && src.peek(1).matchesOperator(JSOperator.COLON))
			return this.parseLabeledStatement(src, context);
		return this.parseExpressionStatement(src, context);
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
				// return new DirectiveTreeImpl(expression.getStart(), end, (String) value);
				throw new JSUnsupportedException("use strict directives", expression.getRange());
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
			if (commaToken.matchesOperator(JSOperator.COMMA)) {
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
	
	protected boolean parseEOL(JSLexer src, Context context) {
		if (src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON))
			return true;
		// ASI cases
		// Note: don't consume the '}'
		if (src.peek().matchesOperator(JSOperator.RIGHT_BRACE) || src.nextTokenIs(TokenKind.SPECIAL, JSSpecialGroup.EOF))
			return true;
		//TODO: newline ASI
		return false;
	}
	
	protected boolean parseTypeMemberSemicolon(JSLexer src, Context context) {
		return src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA) || this.parseEOL(src, context);
	}
	
	protected void expectTypeMemberSemicolon(JSLexer src, Context context) {
		if (!this.parseTypeMemberSemicolon(src, context)) {
			Token lookahead = src.peek();
			throw new JSUnexpectedTokenException("Illegal token " + lookahead + "; expected EOL", lookahead.getRange());
		}
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
		ArrayList<T> result = new ArrayList<>();
		
		while (!isTerminator.test(src.peek())) {
			if (src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.EOF))
				throw new JSEOFException("Unexpected EOF while parsing list", src.getPosition());
			
			T value = elementParser.apply(src, context);
			result.add(value);
		}
		
		if (result.isEmpty())
			return Collections.emptyList();
		
		result.trimToSize();
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
				case AMPERSAND:
					return Tree.Kind.BITWISE_AND;
				case BITWISE_AND_ASSIGNMENT:
					return Tree.Kind.BITWISE_AND_ASSIGNMENT;
				case BITWISE_NOT:
					return Tree.Kind.BITWISE_NOT;
				case VBAR:
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
				case ASTERISK:
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
			return ((SequenceExpressionTree) expr).getElements()
					.stream()
					.map(this::reinterpretExpressionAsParameter)
					.collect(Collectors.toList());
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
	
	/**
	 * <pre>
	 * PrimaryExpression[Yield, Await]:
	 * 		this
	 * 		IdentifierReference[?Yield, ?Await]
	 * 		Literal
	 * 		ArrayLiteral[?Yield, ?Await]
	 * 		ObjectLiteral[?Yield, ?Await]
	 * 		FunctionExpression
	 * 		ClassExpression[?Yield, ?Await]
	 * 		GeneratorExpression
	 * 		AsyncFunctionExpression
	 * 		AsyncGeneratorExpression
	 * 		RegularExpressionLiteral
	 * 		TemplateLiteral[?Yield, ?Await, ~Tagged]
	 * 		CoverParenthesizedExpressionAndArrowParameterList[?Yield, ?Await]
	 * </pre>
	 */
	protected ExpressionTree parsePrimaryExpression(JSLexer src, Context context) {
		Token lookahead = src.peek();
		switch (lookahead.getKind()) {
			case IDENTIFIER:
				return this.parseIdentifier(src, context);
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
						// Regular expression
						context.isAssignmentTarget(false);
						context.isBindingElement(false);
						Token regex = src.finishRegExpLiteral(src.skip(lookahead));
						return this.parseLiteral(regex, src, context);
					}
					case LEFT_BRACKET:{
						context.isolateCoverGrammar();
						ExpressionTree result = this.parseArrayInitializer(src, context);
						context.inheritCoverGrammar();
						return result;
					}
					case LEFT_BRACE: {
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
					case ASYNC:
						if (dialect.supports("js.function.async") && src.peek(1).matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
							//Async function
							return this.parseFunctionExpression(src, context);
						break;
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
	
	protected List<TypeTree> parseTypeArgumentsMaybe(JSLexer src, Context context) {
		Token openChevron = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.LESS_THAN);
		if (openChevron == null)
			return null;
		
		List<TypeTree> arguments = this.parseDelimitedList(this::parseType, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.GREATER_THAN), src, context);
		
		expectOperator(JSOperator.GREATER_THAN, src, context);
		
		return arguments;
	}
	
	protected boolean canFollowExpressionTypeArguments(JSLexer src, Context context) {
		Token lookahead = src.peek();
		return lookahead.matchesOperator(JSOperator.LEFT_PARENTHESIS) || lookahead.getKind() == TokenKind.TEMPLATE_LITERAL;
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
		
		expectOperator(JSOperator.GREATER_THAN, src, context);
		return params;
	}
	
	protected ExpressionTree parseArgument(JSLexer src, Context context) {
		if (src.peek().matchesOperator(JSOperator.SPREAD))
			return this.parseSpread(src, context);
		else
			return this.parseAssignment(src, context.coverGrammarIsolated());
	}
	
	protected List<ExpressionTree> parseArguments(JSLexer src, Context context) { 
		List<ExpressionTree> result = this.parseDelimitedList(this::parseArgument, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS), src, context);
		
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		return result;
	}
	
	//Various statements
	
	protected ImportDeclarationTree parseImportStatement(JSLexer src, Context context) {
		Token importKeywordToken = expectKeyword(JSKeyword.IMPORT, src, context);
		
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
				expectOperator(JSOperator.COMMA, src, context);
			}
		}
		
		Token t;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASTERISK)) != null) {
			//import (defaultMember,)? * as name ...
			IdentifierTree identifier = new IdentifierTreeImpl(t.getStart(), t.getEnd(), "*");
			t = expectKeyword(JSKeyword.AS, src, context);
			IdentifierTree alias = this.parseIdentifier(src, context);
			importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
		} else if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_BRACE)) {
			//import (defaultMember,)? {...} ...
			do {
				//member (as alias)?,
				IdentifierTree identifier = this.parseIdentifier(src, context);
				
				IdentifierTree alias = identifier;
				if (src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.AS))
					alias = this.parseIdentifier(src, context);
				
				importSpecifiers.add(new ImportSpecifierTreeImpl(identifier.getStart(), alias.getEnd(), identifier, alias, false));
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			expectOperator(JSOperator.RIGHT_BRACE, src, context);
		}
		
		// ... from "module-name";
		
		if (!importSpecifiers.isEmpty())
			expectKeyword(JSKeyword.FROM, src, context);
		
		StringLiteralTree source = (StringLiteralTree)this.parseLiteral(null, src, context);
		
		expectEOL(src, context);
		
		importSpecifiers.trimToSize();
		return new ImportDeclarationTreeImpl(importKeywordToken.getStart(), src.getPosition(), importSpecifiers, source);
	}
	
	protected ExportTree parseExportStatement(JSLexer src, Context context) {
		Token exportKeywordToken = expectKeyword(JSKeyword.EXPORT, src, context);
		boolean isDefault = false;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASTERISK)) {
			expectKeyword(JSKeyword.FROM, src, context);
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
		
		return new ExportTreeImpl(exportKeywordToken.getStart(), src.getPosition(), isDefault, expr);
	}
	
	protected TypeAliasTree parseTypeAlias(SourcePosition start, List<DecoratorTree> decorators, Modifiers modifiers, JSLexer src, Context context) {
		expectKeyword(JSKeyword.TYPE, src, context);
		
		// No modifiers permitted on type alias declaration
		if (modifiers.any())
			throw new JSSyntaxException("Illegal modifiers on type alias declaration: " + modifiers, new SourceRange(start, src.getPosition()));
		// No decorators permitted (yet?) on type alias declaration
		if (!decorators.isEmpty())
			throw new JSSyntaxException("Illegal decorators on type alias declaration: " + decorators, new SourceRange(start, src.getPosition()));
		
		IdentifierTree identifier = this.parseIdentifier(src, context);
		
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		expectOperator(JSOperator.ASSIGNMENT, src, context);
		
		TypeTree value = this.parseType(src, context);
		
		expectEOL(src, context);
		
		return new TypeAliasTreeImpl(start, src.getPosition(), identifier, typeParams, value);
	}
	
	protected ExpressionTree parseAwait(JSLexer src, Context context) {
		Token awaitToken = expectKeyword(JSKeyword.AWAIT, src, context);
		ExpressionTree expr = this.parseUnaryExpression(src, context);
		return new AwaitTreeImpl(awaitToken.getStart(), src.getPosition(), expr);
	}
	
	protected VariableDeclaratorTree parseVariableDeclarator(JSLexer src, Context context) {
		PatternTree name = this.parsePattern(src, context);
		
		//Check if a type is available
		TypeTree type = this.parseTypeMaybe(src, context, false);
		
		//Check if an initializer is available
		ExpressionTree initializer = this.parseInitializer(src, context);
		// If we're assigning a function expression, propagate the variable name
		if (initializer != null
				&& initializer.getKind() == Kind.FUNCTION_EXPRESSION
				&& name.getKind() == Kind.IDENTIFIER
				&& ((FunctionExpressionTree) initializer).getName() == null) {
			//Infer fn name from variable id
			FunctionExpressionTree fn = (FunctionExpressionTree) initializer;
			initializer = new FunctionExpressionTreeImpl(fn.getStart(), fn.getEnd(), fn.getModifiers(), (IdentifierTree) name, fn.getTypeParameters(), fn.getParameters(), fn.getReturnType(), fn.isArrow(), fn.getBody());
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
	 * Syntax:
	 * <pre>
	 * ReturnStatement[Yield, Await]:
	 * 		return ;
	 * 		return <no line terminator> Expression[+In, ?Yield, ?Await] ;
	 * ThrowStatement[Yield, Await]:
	 * 		throw <no line terminator> Expression[+In, ?Yield, ?Await] ;
	 * </pre>
	 */
	//TODO: split to parseThrow & parseReturn?
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
		//TODO: fix end to happen after EOL (also handle for expr == null)
		
		if (keywordToken.getValue() == JSKeyword.RETURN)
			return new ReturnTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
		else
			return new ThrowTreeImpl(keywordToken.getStart(), expr.getEnd(), expr);
	}
	
	/**
	 * Parse labeled statement.
	 * Syntax:
	 * <pre>
	 * LabelledStatement[Yield, Await, Return]:
	 * 		LabelIdentifier[?Yield, ?Await] : LabelledItem[?Yield, ?Await, ?Return]
	 * LabelledItem[Yield, Await, Return]:
	 * 		Statement[?Yield, ?Await, ?Return]
	 * 		FunctionDeclaration[?Yield, ?Await, ~Default] <static error>
	 * </pre>
	 */
	protected LabeledStatementTree parseLabeledStatement(JSLexer src, Context context) {
		IdentifierTree identifier = this.parseIdentifier(src, context);
		expectOperator(JSOperator.COLON, src, context);
		StatementTree statement = this.parseStatement(src, context);
		return new LabeledStatementTreeImpl(identifier.getStart(), src.getPosition(), identifier, statement);
	}
	
	
	//SECTION: Annotations
	
	protected Modifiers mapPrefixModifier(Token token, Context context) {
		switch (token.getKind()) {
			case IDENTIFIER:
				switch (token.<String>getValue()) {
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
					case ABSTRACT:
						return Modifiers.ABSTRACT;
					case DECLARE:
						return Modifiers.DECLARE;
					case READONLY:
						return Modifiers.READONLY;
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
					case ASTERISK:
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
		
		if (expectPatternAfter) {
			if (!modifiers.isEmpty() && !TokenPredicate.CAN_FOLLOW_MODIFIER.test(src.peek())) {
				// We need to backtrack
				src.reset();
				modifiers.remove(modifiers.size() - 1);
			} else {
				src.unmark();
			}
		}
		
		return result;
	}
	
	protected Tree parseJSDoc(JSLexer src, Context context) {
		//TODO: finish
		return null;
	}
	
	protected DecoratorTree parseDecorator(JSLexer src, Context context) {
		SourcePosition start = src.getNextStart();
		expectOperator(JSOperator.AT_SYMBOL, src, context);
		//TODO: finish
		throw new JSUnsupportedException("Decorators", src.getPosition());
	}
	
	/**
	 * <pre>
	 * DecoratorList[Yield, Await]:
	 * 		DecoratorList[?Yield, ?Await]? Decorator[?Yield, ?Await]
	 * Decorator[Yield, Await]:
	 * 		@ DecoratorMemberExpression[?Yield, ?Await]
	 * 		@ DecoratorCallExpression[?Yield, ?Await]
	 * DecoratorMemberExpression[Yield, Await]:
	 * 		IdentifierReference[?Yield, ?Await]
	 * 		DecoratorMemberExpression[?Yield, ?Await] . IdentifierName
	 * 		( Expression[+In, ?Yield, ?Await] )
	 * DecoratorCallExpression[Yield, Await]:
	 * 		DecoratorMemberExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
	 * </pre>
	 */
	protected List<DecoratorTree> parseDecorators(JSLexer src, Context context) {
		return this.parseList(this::parseDecorator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.AT_SYMBOL).negate(), src, context);
	}
	
	protected boolean isDeclaration(JSLexer src, Context context) {
		Token current;
		while (!(current = src.nextToken()).matches(TokenKind.SPECIAL, JSSpecialGroup.EOF)) {
			if (!current.isKeyword())
				return false;
			switch (current.<JSKeyword>getValue()) {
				case VAR:
				case LET:
				case CONST:
				case FUNCTION:
				case CLASS:
				case ENUM:
					return true;
				case INTERFACE:
				case TYPE:
					//TODO: must be followed by identifier
					return this.isIdentifier(src.peek(), context);
				case ABSTRACT:
				case ASYNC:
				case DECLARE:
				case PUBLIC:
				case PRIVATE:
				case PROTECTED:
				case READONLY:
					//Strict or ASI
					if (context.isStrict() || src.peek().hasPrecedingNewline())
						return false;
				case STATIC:
					continue;
				default:
					return false;
			}
		}
		return false;
	}
	
	protected StatementTree parseDeclaration(JSLexer src, Context context) {
		SourcePosition start = src.getNextStart();
		List<DecoratorTree> decorators = this.parseDecorators(src, context);
		Modifiers contextualModifiers = Modifiers.union(Modifiers.ABSTRACT, Modifiers.ASYNC, Modifiers.CONST, Modifiers.DECLARE, Modifiers.MASK_VISIBILITY);
		Modifiers modifiers = this.parseModifiers(contextualModifiers, true, src, context);
		
		if (modifiers.isDeclare() && !context.isAmbient())
			context.push().enterDeclare();
		StatementTree result = this.parseDeclarationInner(start, decorators, modifiers, src, context);
		if (modifiers.isDeclare() && !context.isAmbient())
			context.pop();
		
		return result;
	}
	
	protected StatementTree parseDeclarationInner(SourcePosition start, List<DecoratorTree> decorators, Modifiers modifiers, JSLexer src, Context context) {
		Token lookahead = src.peek();
		switch (lookahead.getKind()) {
			case KEYWORD:
				switch (lookahead.<JSKeyword>getValue()) {
					case VAR:
					case LET:
					case CONST:
						return this.parseVariableDeclaration(false, src, context);
					case TYPE:
						return this.parseTypeAlias(start, decorators, modifiers, src, context);
					case CLASS:
						return this.parseClassDeclaration(start, decorators, modifiers, src, context);
					case FUNCTION:
						return this.parseFunctionDeclaration(src, context);
					case INTERFACE:
						return this.parseInterfaceDeclaration(src, context);
					case ENUM:
						return this.parseEnumDeclaration(src, context);
					case EXPORT:
						return this.parseExportStatement(src, context);
					case IMPORT:
						//TODO: finish
					default:
						break;
				}
				break;
			default:
				break;
		}
		throw new JSUnexpectedTokenException(src.peek(), "expected a declaration");
	}
	
	
	//SECTION: Type structures
	
	/**
	 * Check if is start of index signature.
	 * Please use {@link #lookahead(BiPredicate, JSLexer, Context)} to call.
	 */
	protected boolean isIndexSignature(JSLexer src, Context context) {
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_BRACKET))
			return false;
		if (this.parseModifiers(Modifiers.READONLY, true, src, context).any()) {
			if (src.nextToken().isIdentifier())
				return true;
		} else if (!src.nextToken().isIdentifier()) {
			return false;
		}
		this.parseModifiers(Modifiers.union(Modifiers.OPTIONAL, Modifiers.DEFINITE), false, src, context);
		return src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COLON);
	}
	
	/**
	 * <pre>
	 * CallSignature:
	 * 		TypeParameterList[opt] ( ParameterList ) => Type
	 * ConstructSignature:
	 * 		new ( ParameterList ) => Type
	 * </pre>
	 */
	protected SignatureDeclarationTree parseCallSignature(JSLexer src, Context context) {
		SourcePosition start = src.getPosition();
		
		boolean constructSignature = src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.NEW);
		
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = this.parseParameters(null, src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		TypeTree returnType = this.parseTypeMaybe(src, context, false);
		
		this.parseTypeMemberSemicolon(src, context);
		
		if (constructSignature)
			return new ConstructSignatureTreeImpl(start, src.getPosition(), null, typeParams, params, returnType);
		else
			return new CallSignatureTreeImpl(start, src.getPosition(), null, typeParams, params, returnType);
	}
	
	/**
	 * <pre>
	 * MethodSignature:
	 * 		MethodModifiers PropertyName TypeParameters[opt] ( ParameterList ) TypeAnnotation[opt] EOL[opt]
	 * </pre>
	 */
	protected MethodSignatureTree parseMethodSignature(SourcePosition start, Modifiers modifiers, PropertyName name, JSLexer src, Context context) {
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = this.parseParameters(null, src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		TypeTree returnType = this.parseTypeMaybe(src, context, true);
		
		this.expectTypeMemberSemicolon(src, context);
		
		return new MethodSignatureTreeImpl(start, src.getPosition(), modifiers, name, typeParams, params, returnType);
	}
	
	protected PropertySignatureTree parsePropertySignature(SourcePosition start, Modifiers modifiers, PropertyName name, JSLexer src, Context context) {
		TypeTree type = this.parseTypeMaybe(src, context, true);
		
		this.expectTypeMemberSemicolon(src, context);
		
		return new PropertySignatureTreeImpl(start, src.getPosition(), modifiers, name, type);
	}
	
	protected PropertyDeclarationTree parsePropertyDeclaration(SourcePosition start, List<DecoratorTree> decorators, Modifiers modifiers, PropertyName name, JSLexer src, Context context) {
		//TODO: finish
		TypeTree type = this.parseTypeMaybe(src, context, true);
		
		context.push();
		context.allowIn();
		if (!modifiers.isStatic())
			context.allowAwait(false);
		ExpressionTree initializer = this.parseInitializer(src, context);
		context.pop();
		
		this.expectTypeMemberSemicolon(src, context);
		
		return new PropertyDeclarationTreeImpl(start, src.getPosition(), modifiers, name, type, initializer);
	}
	
	/**
	 * <pre>
	 * IndexSignature:
	 * 		readonly[opt] [ Identifier TypeAnnotation ] TypeAnnotation EOL[opt]
	 * </pre>
	 */
	protected IndexSignatureTree parseIndexSignature(List<DecoratorTree> decorators, SourcePosition start, Modifiers modifiers, JSLexer src, Context context) {
		expectOperator(JSOperator.LEFT_BRACKET, src, context);
		
		//TODO: TSC has IndexSignature inherit from Signature, and parses parameters in the brackets: why?
		IdentifierTree idxVar = this.parseIdentifier(src, context);
		expectOperator(JSOperator.COLON, src, context);
		TypeTree idxType = this.parseType(src, context);
		TypeParameterDeclarationTree idx = new TypeParameterDeclarationTreeImpl(idxVar.getStart(), idxType.getEnd(), idxVar, idxType, null);
		//List<ParameterTree> params = this.parseDelimitedList(this::parseParameter, this::parseCommaSeparator, TokenPredicate.match(TokenKind.BRACKET, ']'), src, context);
		
		expectOperator(JSOperator.RIGHT_BRACKET, src, context);
		
		//Parse postfix modifiers ('?'/'!')
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		expectOperator(JSOperator.COLON, src, context);
		TypeTree type = this.parseType(src, context);
		
		this.expectTypeMemberSemicolon(src, context);
		
		return new IndexSignatureTreeImpl(start, src.getPosition(), modifiers, idx, type);
	}
	
	/**
	 * Check that this is the start of a mapped type.
	 * Please call using {@link #lookahead(BiPredicate, JSLexer, Context)}.
	 */
	protected boolean isMappedTypeStart(JSLexer src, Context context) {
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_BRACE))
			return false;
		
		//TODO: support +/- readonly
		
		src.nextTokenIf(TokenKind.IDENTIFIER, "readonly");//Consume readonly if present
		
		return src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_BRACKET) && src.nextToken().isIdentifier() && src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.IN);
	}
	
	/**
	 * <pre>
	 * MappedType:
	 * 		{ readonly[opt] [ Identifier in Type ] ?[opt] : Type ;[opt] }
	 * </pre>
	 */
	protected TypeTree parseMappedType(JSLexer src, Context context) {
		SourcePosition start = expectOperator(JSOperator.LEFT_BRACE, src, context).getStart();
		Modifiers modifiers = this.parseModifiers(Modifiers.READONLY, false, src, context);
		
		expectOperator(JSOperator.LEFT_BRACKET, src, context);
		IdentifierTree name = this.parseIdentifier(src, context);
		expectKeyword(JSKeyword.IN, src, context);
		TypeTree constraint = this.parseType(src, context);
		expectOperator(JSOperator.RIGHT_BRACKET, src, context);
		TypeParameterDeclarationTree param = new TypeParameterDeclarationTreeImpl(name.getStart(), constraint.getEnd(), name, constraint, null);
		
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		expectOperator(JSOperator.COLON, src, context);
		
		TypeTree type = this.parseType(src, context);
		this.parseEOL(src, context);
		expectOperator(JSOperator.RIGHT_BRACE, src, context);
		
		return new MappedTypeTreeImpl(start, src.getPosition(), modifiers, param, type);
	}
	
	/**
	 * <pre>
	 * TypeMember:
	 * 		CallSignature
	 * 		ConstructSignature
	 * 		IndexSignature
	 * 		PropertySignature
	 * 		MethodSignature
	 * </pre>
	 */
	protected TypeElementTree parseTypeMember(JSLexer src, Context context) {
		Token next = src.peek();
		
		// Call/construct signature
		if (TokenPredicate.CALL_SIGNATURE_START.test(next))
			return (CallSignatureTree) this.parseCallSignature(src, context);
		else if (next.matches(TokenKind.KEYWORD, JSKeyword.NEW))
			return (ConstructSignatureTree) this.parseCallSignature(src, context);
		
		// Parse prefix modifiers
		Modifiers typeElementFilter = Modifiers.READONLY;
		Modifiers modifiers = this.parseModifiers(typeElementFilter, true, src, context);
		
		if (lookahead(this::isIndexSignature, src, context))
			return this.parseIndexSignature(null, next.getStart(), modifiers, src, context);
		
		// Method/property signature
		SourcePosition start = next.getStart();
		PropertyName propName = this.parsePropertyName(src, context);
		
		// Parse postfix modifiers
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		next = src.peek();
		if (TokenPredicate.CALL_SIGNATURE_START.test(next))
			// Method signature
			return this.parseMethodSignature(start, modifiers, propName, src, context);
		
		return this.parsePropertySignature(start, modifiers, propName, src, context);
	}
	
	/**
	 * Parse the body of an interface, whether it be inline or a declaration
	 * @param src
	 * @param context
	 * @return
	 */
	protected List<TypeElementTree> parseObjectTypeMembers(JSLexer src, Context context) {
		List<TypeElementTree> properties = this.parseList(this::parseTypeMember, TokenPredicate.RIGHT_BRACE, src, context);
		
		expectOperator(JSOperator.RIGHT_BRACE, src, context);
		
		return properties;
	}
	
	/**
	 * Parse an interface declaration
	 */
	protected InterfaceDeclarationTree parseInterfaceDeclaration(JSLexer src, Context context) {
		Token interfaceKeywordToken = expectKeyword(JSKeyword.INTERFACE, src, context);
		
		dialect.require("ts.types.interface", interfaceKeywordToken.getRange());
		
		//Get declared name
		IdentifierTree name = this.parseIdentifier(src, context);
		
		// Type parameters
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		//...extends A, B, ..., C
		List<HeritageClauseTree> heritage = this.parseHeritage(src, context);
		
		expectOperator(JSOperator.LEFT_BRACE, src, context);
		//Parse body
		List<TypeElementTree> properties = this.parseObjectTypeMembers(src, context);
		
		return new InterfaceDeclarationTreeImpl(interfaceKeywordToken.getStart(), src.getPosition(), name, typeParams, heritage, properties);
	}
	
	
	//SECTION: Enums
	
	/**
	 * <pre>
	 * EnumMember:
	 * 		Identifier Initializer[opt]
	 * </pre>
	 */
	protected EnumMemberTree parseEnumMember(JSLexer src, Context context) {
		IdentifierTree name = this.parseIdentifier(src, context);
		ExpressionTree initialier = this.parseInitializer(src, context.withIn());
		//TODO: finish
		throw new JSUnsupportedException("Enum members", src.getPosition());
	}
	
	/**
	 * Parse an enum declaration
	 * <pre>
	 * EnumDeclaration[declare, export]:
	 * 		const[opt] enum Identifier { EnumMember[] }
	 * </pre>
	 * @see #parseEnumMember(JSLexer, Context)
	 */
	protected EnumDeclarationTree parseEnumDeclaration(JSLexer src, Context context) {
		SourcePosition start = src.getNextStart();
		
		Modifiers modifiers = this.parseModifiers(Modifiers.union(Modifiers.CONST, Modifiers.DECLARE), true, src, context);
		
		Token enumKeyword = expectKeyword(JSKeyword.ENUM, src, context);
		
		IdentifierTree name = this.parseIdentifier(src, context);
		
		expectOperator(JSOperator.LEFT_BRACE, src, context);
		List<EnumMemberTree> members = this.parseDelimitedList(this::parseEnumMember, this::parseCommaSeparator, TokenPredicate.RIGHT_BRACE, src, context);
		expectOperator(JSOperator.RIGHT_BRACE, src, context);
		
		//TODO: finish
		throw new JSUnsupportedException("Enum declarations", src.getPosition());
	}
	
	
	//SECTION: Class elements
	
	protected ClassElementTree parseConstructorDeclaration(SourcePosition startPos, List<DecoratorTree> decorators, Modifiers modifiers, JSLexer src, Context context) {
		expectKeyword(JSKeyword.CONSTRUCTOR, src, context);
		throw new JSUnsupportedException("Constructor declarations", src.getPosition());
	}
	
	/**
	 * <pre>
	 * MethodDeclaration[declare, abstract]:
	 * 		
	 * </pre>
	 */
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
	
	protected MethodDeclarationTree parseAccessorDeclaration(List<DecoratorTree> decorators, Modifiers modifiers, JSLexer src, Context context) {
		//TODO: finish
		throw new JSUnsupportedException("Accessors", src.getPosition());
	}
	
	/**
	 * <pre>
	 * HeritageExpression:
	 * 		LeftSideExpression TypeArguments[opt]
	 * </pre>
	 */
	protected HeritageExpressionTree parseHeritageType(JSLexer src, Context context) {
		ExpressionTree expr = this.parseLeftSideExpression(true, src, context);
		List<TypeTree> typeArgs = this.parseTypeArgumentsMaybe(src, context);
		return new HeritageExpressionTreeImpl(expr.getStart(), src.getPosition(), expr, typeArgs);
	}
	
	/**
	 * <pre>
	 * HeritageClause:
	 * 		extends HeritageExpression[]
	 * 		implements HeritageExpression[]
	 * </pre>
	 */
	protected HeritageClauseTree parseHeritageClause(JSLexer src, Context context) {
		Token keyword = src.nextTokenIf(TokenPredicate.HERITAGE_START);
		if (keyword == null)
			return null;
		
		Kind kind = keyword.<JSKeyword>getValue() == JSKeyword.EXTENDS ? Kind.EXTENDS_CLAUSE : Kind.IMPLEMENTS_CLAUSE;
		List<HeritageExpressionTree> exprs = this.parseDelimitedList(this::parseHeritageType, this::parseCommaSeparator, null, src, context);
		
		return new HeritageClauseTreeImpl(keyword.getStart(), src.getPosition(), kind, exprs);
	}
	
	protected List<HeritageClauseTree> parseHeritage(JSLexer src, Context context) {
		return this.parseDelimitedList(this::parseHeritageClause, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.LEFT_BRACE), src, context);
	}
	
	/**
	 * <pre>
	 * ClassElement[Yield, Await, Ambient, Abstract]:
	 * 		EmptyClassElement
	 * 		IndexSignature
	 * 		ConstructorDeclaration[?Yield, ?Await]
	 * 		[~Ambient] ConstructorDefinition[?Yield, ?Await]
	 * 		MethodDeclaration[?Yield, ?Await]
	 * 		[~Ambient] MethodDefinition[?Yield, ?Await, ?Abstract]
	 * 		PropertyDeclaration
	 * 		[~Ambient] PropertyDefinition[?Yield, ?Await]
	 * EmptyClassElement:
	 * 		;
	 * </pre>
	 */
	protected ClassElementTree parseClassElement(JSLexer src, Context context) {
		SourcePosition start = src.peek().getStart();
		
		if (src.peek().matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)) {
			// Empty member
			//TODO: finish
			throw new JSUnsupportedException("Empty class members", src.getPosition());
		}
		
		List<DecoratorTree> decorators = this.parseDecorators(src, context);
		
		// Parse prefix modifiers
		final Modifiers classElementFilter = Modifiers.union(Modifiers.MASK_VISIBILITY, Modifiers.STATIC, Modifiers.READONLY, Modifiers.ABSTRACT, Modifiers.GETTER, Modifiers.SETTER, Modifiers.GENERATOR, Modifiers.ASYNC);
		Modifiers modifiers = this.parseModifiers((nextMod, token) -> {
			if (nextMod.isStatic())
				dialect.require("js.class.static", token.getRange());
			if (nextMod.isAsync())
				dialect.require("js.async", token.getRange());
			if (nextMod.isGenerator())
				dialect.require("js.method.generator", token.getRange());
			return !nextMod.subtract(classElementFilter).any();
		}, true, src, context);
		
		if (modifiers.isGetter() || modifiers.isSetter())
			return this.parseAccessorDeclaration(decorators, modifiers, src, context);
		
		if (src.peek().matches(TokenKind.IDENTIFIER, "constructor") && dialect.supports("js.class.constructor"))
			// Constructor declaration
			return this.parseConstructorDeclaration(start, decorators, modifiers, src, context);
		
		// Possibly index signature
		if (lookahead(this::isIndexSignature, src, context))
			return this.parseIndexSignature(decorators, start, modifiers, src, context);
		
		// At this point, it's either a method or property
		PropertyName name = this.parsePropertyName(src, context);
		
		// Parse postfix modifiers
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		Token lookahead = src.peek();
		if (modifiers.isGenerator()
				|| lookahead.matchesOperator(JSOperator.LEFT_PARENTHESIS)
				|| lookahead.matchesOperator(JSOperator.LESS_THAN)) {
			return this.parseMethodDeclaration(start, decorators, modifiers, name, src, context);
		}
		
		return this.parsePropertyDeclaration(start, decorators, modifiers, name, src, context);
	}
	
	protected List<ClassElementTree> parseClassBody(JSLexer src, Context context) {
		expectOperator(JSOperator.LEFT_BRACE, src, context);
		List<ClassElementTree> members = this.parseList(this::parseClassElement, TokenPredicate.RIGHT_BRACE, src, context);
		expectOperator(JSOperator.RIGHT_BRACE, src, context);
		return members;
	}
	
	protected ClassExpressionTree parseClassExpression(JSLexer src, Context context) {
		final SourcePosition start = src.peek().getStart();
		
		//Support abstract classes
		Modifiers classModifiers = this.parseModifiers(Modifiers.ABSTRACT, false, src, context);
		
		Token classKeywordToken = expectKeyword(JSKeyword.CLASS, src, context);
		
		dialect.require("js.class", classKeywordToken.getRange());
		
		//Read optional class identifier
		IdentifierTree className = this.parseIdentifierMaybe(src, context);
		List<TypeParameterDeclarationTree> typeParameters = this.parseTypeParametersMaybe(src, context);
		
		List<HeritageClauseTree> heritage = this.parseHeritage(src, context);
		
		List<ClassElementTree> members = this.parseClassBody(src, context);
		
		return new ClassExpressionTreeImpl(start, src.getPosition(), classModifiers, className, typeParameters, heritage, members);
	}
	
	/**
	 * <pre>
	 * ClassDeclaration[Yield, Await, Default]:
	 * 		DecoratorList[?Yield, ?Await] ClassModifiers BindingIdentifier[?Yield, ?Await] HeritageClauses[?Yield, ?Await] ClassTail[?Yield, ?Await] 
	 * ClassModifier:
	 * 		abstract
	 * 		declare
	 * </pre>
	 */
	protected ClassDeclarationTree parseClassDeclaration(SourcePosition start, List<DecoratorTree> decorators, Modifiers modifiers, JSLexer src, Context context) {
		// Filter valid modifiers
		final Modifiers modifierWL = Modifiers.union(Modifiers.ABSTRACT, Modifiers.DECLARE);
		if (modifiers.subtract(modifierWL).any())
			throw new JSSyntaxException("Illegal modifiers: " + modifiers.subtract(modifierWL), new SourceRange(start, src.getPosition()));
		
		Token classKeywordToken = expectKeyword(JSKeyword.CLASS, src, context);
		
		dialect.require("js.class", classKeywordToken.getRange());
		
		IdentifierTree className = this.parseIdentifier(src, context);
		List<TypeParameterDeclarationTree> typeParameters = this.parseTypeParametersMaybe(src, context);
		
		List<HeritageClauseTree> heritage = this.parseHeritage(src, context);
		
		List<ClassElementTree> members = this.parseClassBody(src, context);
		
		return new ClassDeclarationTreeImpl(start, src.getPosition(), modifiers, className, typeParameters, heritage, members);
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
			if (canBeFunction && src.peek().matchesOperator(JSOperator.LEFT_PARENTHESIS))
				return this.parseFunctionType(src, context);
			return null;
		}
		return this.parseType(src, context);
	}
	
	protected boolean skipParameterStart(JSLexer src, Context context) {
		this.parseModifiers(Modifiers.union(Modifiers.MASK_VISIBILITY, Modifiers.READONLY), true, src, context);
		Token lookahead = src.peek();
		if (this.isIdentifier(lookahead, context) || lookahead.matches(TokenKind.KEYWORD, JSKeyword.THIS)) {
			src.skip(lookahead);
			return true;
		}
		if (lookahead.matchesOperator(JSOperator.LEFT_BRACKET) || lookahead.matchesOperator(JSOperator.LEFT_BRACE)) {
			try {
				this.parsePattern(src, context);
				return true;
			} catch (JSSyntaxException e) {
			}
		}
		return false;
	}
	
	/**
	 * Please call with {@link #lookahead(BiPredicate, JSLexer, Context)}.
	 */
	protected boolean isStartOfFunctionType(JSLexer src, Context context) {
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS))
			return false;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS) || src.nextTokenIs(TokenKind.OPERATOR, JSOperator.SPREAD))
			return true;
		if (!this.skipParameterStart(src, context))
			return false;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COLON)
				|| src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA)
				|| src.nextTokenIs(TokenKind.OPERATOR, JSOperator.QUESTION_MARK)
				|| src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT))
			return true;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS) && src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LAMBDA))
			return true;
		return false;
	}
	
	/**
	 * <pre>
	 * FunctionSignatureType:
	 * 		TypeParameters? ( Parameters[~AccessModifiers] ) => Type[+Conditional]
	 * ConstructSignatureType:
	 * 		new ( Parameters[~AccessModifiers] ) => Type[+Conditional]
	 * </pre>
	 */
	protected TypeTree parseFunctionType(JSLexer src, Context context) {
		final SourcePosition start = src.getNextStart();
		boolean isConstructor = src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.NEW);
		
		List<TypeParameterDeclarationTree> typeParams = this.parseTypeParametersMaybe(src, context);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		List<ParameterTree> params = this.parseParameters(null, src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		expectOperator(JSOperator.LAMBDA, src, context);
		
		TypeTree returnType = this.parseType(src, context);
		
		SourcePosition end = src.getPosition();
		
		if (isConstructor)
			return new ConstructorTypeTreeImpl(start, end, typeParams, params, returnType);
		else
			return new FunctionTypeTreeImpl(start, end, typeParams, params, returnType);
	}
	
	/**
	 * <pre>
	 * TupleElement:
	 * 		RestTupleElement
	 * 		NullModifiedType
	 * 		Type
	 * RestTupleElement:
	 * 		`...` Type
	 * </pre>
	 */
	protected TypeTree parseTupleElement(JSLexer src, Context context) {
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.SPREAD)) {
			// RestTupleElement
			TypeTree base = this.parseType(src, context);
			//TODO: finish
			throw new JSUnsupportedException("Tuple rest elements", src.getPosition());
		}
		return this.parseType(src, context.pushed().allowOptionalTypes(true).allowDefiniteTypes(true));
	}
	
	/**
	 * <pre>
	 * TupleType:
	 * 		[ TupleBody ]
	 * TupleBody:
	 * 		TupleElement
	 * 		TupleBody, TupleElement
	 * </pre>
	 * @see #parseTupleElement(JSLexer, Context)
	 */
	protected TypeTree parseTupleType(JSLexer src, Context context) {
		SourcePosition start = expectOperator(JSOperator.LEFT_BRACKET, src, context).getStart();
		
		List<TypeTree> slots = this.parseDelimitedList(this::parseTupleElement, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.RIGHT_BRACKET), src, context);
		
		SourcePosition end = expectOperator(JSOperator.RIGHT_BRACKET, src, context).getEnd();
		return new TupleTypeTreeImpl(start, end, slots);
	}
	
	/**
	 * <pre>
	 * ObjectType:
	 * 		{ InterfaceBody }
	 * </pre>
	 * @see #parseObjectTypeMembers(JSLexer, Context)
	 */
	protected ObjectTypeTree parseObjectType(JSLexer src, Context context) {
		SourcePosition start = expectOperator(JSOperator.LEFT_BRACE, src, context).getStart();
		List<TypeElementTree> properties = this.parseObjectTypeMembers(src, context);
		// '}' consumed by parseObjectTypeMembers
		return new ObjectTypeTreeImpl(start, src.getPosition(), properties);
	}
	
	/**
	 * <pre>
	 * ParenthesizedType:
	 * 		( Type[+Conditional] )
	 * </pre>
	 */
	protected TypeTree parseParenthesizedType(JSLexer src, Context context) {
		SourcePosition start = expectOperator(JSOperator.LEFT_PARENTHESIS, src, context).getStart();
		TypeTree inner = this.parseType(src, context);
		SourcePosition end = expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context).getEnd();
		//TODO: custom node kind?
		return inner;
	}
	
	/**
	 * <pre>
	 * LiteralType:
	 * 		StringLiteral
	 * 		NumericLiteral
	 * 		BooleanLiteral
	 * </pre>
	 */
	protected LiteralTypeTree<?> parseLiteralType(JSLexer src, Context context) {
		LiteralTree<?> value = this.parseLiteral(null, src, context);
		return new LiteralTypeTreeImpl<>(value);
	}
	
	/**
	 * <pre>
	 * PrimitiveType:
	 * 		any
	 * 		boolean
	 * 		never
	 * 		null
	 * 		number
	 * 		object
	 * 		string
	 * 		symbol
	 * 		unknown
	 * 		void
	 * </pre>
	 */
	protected TypeTree parsePrimitiveType(JSLexer src, Context context) {
		//TODO: finish for VOID, NULL_LITERAL
		return new SpecialTypeTreeImpl(src.nextToken());
	}
	
	/**
	 * <pre>
	 * TypeReference:
	 * 		Identifier TypeArguments?
	 * </pre>
	 */
	protected IdentifierTypeTree parseTypeReference(JSLexer src, Context context) {
		//TODO: actually should be qualified name
		IdentifierTree identifier = this.parseIdentifier(src, context);
		if (src.peek().matchesOperator(JSOperator.PERIOD))
			throw new JSUnsupportedException("Type reference qualified names", src.peek().getRange());
		
		List<TypeTree> typeArgs = this.parseTypeArgumentsMaybe(src, context);
		
		return new IdentifierTypeTreeImpl(identifier.getStart(), src.getPosition(), identifier, typeArgs);
	}
	
	protected TypeTree parseTypeQuery(JSLexer src, Context context) {
		SourcePosition start = src.getNextStart();
		expectKeyword(JSKeyword.TYPEOF, src, context);
		
		ExpressionTree expr = this.parseIdentifier(src, context);//TODO: qualified name
		
		throw new JSUnsupportedException("Type queries", new SourceRange(start, src.getPosition()));
	}
	
	/**
	 * Immediate types:
	 * <pre>
	 * ImmediateType:
	 * 		PrimitiveType
	 * 		LiteralType
	 * 		TupleType
	 * 		ParenthesizedType
	 * 		ImportType
	 * 		TypeQuery
	 * 		ObjectType
	 * 		TypeReference
	 * </pre>
	 */
	protected TypeTree parseImmediateType(JSLexer src, Context context) {
		Token lookahead = src.peek();
		switch (lookahead.getKind()) {
			case KEYWORD:
				switch (lookahead.<JSKeyword>getValue()) {
					case THIS: {
						//'this' type
						//No generics on 'this'
						IdentifierTree ident = this.parseIdentifier(src, context);//TODO: does this work because lookahead is a keyword?
						return new IdentifierTypeTreeImpl(ident.getStart(), ident.getEnd(), ident, Collections.emptyList());
					}
					case VOID:
						return this.parseLiteralType(src, context);
					case FUNCTION:
						//Function
						return this.parseFunctionType(src, context);
					case TYPEOF:
						if (!src.peek(1).matches(TokenKind.KEYWORD, JSKeyword.IMPORT))
							return this.parseTypeQuery(src, context);
						//Fallthrough intentional
					case IMPORT:
						throw new JSUnsupportedException("Import types", src.getPosition());
					default:
						//TODO: try to see if we can convert to identifier?
						break;
				}
				break;
			case IDENTIFIER:
				switch (lookahead.<String>getValue()) {
					case "Array": {
						//TODO: should this special case be handled at all?
						//Array<X> => X[]
						src.skip(lookahead);
						List<TypeTree> arrayGenericArgs = this.parseTypeArgumentsMaybe(src, context);
						//TODO: should this be pushed back to validation?
						if (arrayGenericArgs.size() > 1)
							throw new JSSyntaxException("Cannot have more than one type for Array", arrayGenericArgs.get(2).getStart());
						
						TypeTree arrayBaseType = null;
						if (arrayGenericArgs.size() == 1)
							arrayBaseType = arrayGenericArgs.get(0);
						else
							//Fall back on 'any[]'
							arrayBaseType = new SpecialTypeTreeImpl(SpecialType.ANY);
						
						return new ArrayTypeTreeImpl(lookahead.getStart(), src.getPosition(), arrayBaseType);
					}
					case "any":
					case "unknown":
					case "string":
					case "number":
					case "symbol":
					case "boolean":
					case "null"://TODO: keyword?
					case "undefined"://TODO: keyword?
					case "never":
						if (!src.peek(1).matchesOperator(JSOperator.PERIOD))
							return this.parsePrimitiveType(src, context);
						// Fallthrough intentional
					default:
						return this.parseTypeReference(src, context);
				}
			case NULL_LITERAL:
				return this.parseLiteralType(src, context);
			case STRING_LITERAL:
			case BOOLEAN_LITERAL:
			case NUMERIC_LITERAL:
			case REGEX_LITERAL://TODO: is this right?
			case TEMPLATE_LITERAL://TODO: is this right?
				return this.parseLiteralType(src, context);
			case OPERATOR:
				switch (lookahead.<JSOperator>getValue()) {
					case LEFT_PARENTHESIS:
						return this.parseParenthesizedType(src, context);
					case LOGICAL_NOT:
						//TODO: JSDoc non-nullable
					case QUESTION_MARK:
						//TODO: JSDoc unknown/nullable
					case ASTERISK:
					case MULTIPLICATION_ASSIGNMENT:
						//TODO: JSDoc any
						throw new JSUnsupportedException("JSDoc types", src.getPosition());
					case LEFT_BRACE:
						if (lookahead(this::isMappedTypeStart, src, context))
							return this.parseMappedType(src, context);
						return this.parseObjectType(src, context);
					case LEFT_BRACKET:
						return this.parseTupleType(src, context);
					default:
						break;
				}
				break;
			default:
				break;
		}
		throw new JSUnexpectedTokenException(lookahead);
	}
	
	/**
	 * Unary-postfixed types:
	 * <pre>
	 * PostfixType:
	 * 		ImmediateType
	 * 		PostfixType !
	 * 		PostfixType ?
	 * 		ArrayType
	 * 		IndexedType
	 * ArrayType:
	 * 		PostfixType [ ]
	 * IndexedType:
	 * 		PostfixType [ Type ]
	 * </pre>
	 */
	protected TypeTree parsePostfixType(JSLexer src, Context context) {
		TypeTree type = this.parseImmediateType(src, context);
		while (!src.isEOF()) {
			if (context.allowDefiniteTypes() && src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LOGICAL_NOT)) {
				//TODO: context flags for JSDoc types
				// JSDoc non-nullable `T!`
				type = new UnaryTypeTreeImpl(type.getStart(), src.getPosition(), Kind.DEFINITE_TYPE, type);
			} else if (context.allowOptionalTypes() && src.nextTokenIs(TokenKind.OPERATOR, JSOperator.QUESTION_MARK)) {
				// JSDoc nullable `T?`
				type = new UnaryTypeTreeImpl(type.getStart(), src.getPosition(), Kind.OPTIONAL_TYPE, type);
			} else if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_BRACKET)) {
				if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_BRACKET)) {
					// Array type `T[]`
					type = new ArrayTypeTreeImpl(type.getStart(), src.getPosition(), type);
				} else {
					// Index type `T[k]`
					TypeTree index = this.parseType(src, context);
					expectOperator(JSOperator.RIGHT_BRACKET, src, context);
					type = new MemberTypeTreeImpl(type.getStart(), src.getPosition(), type, index);
				}
			} else {
				break;
			}
		}
		
		return type;
	}
	
	/**
	 * <pre>
	 * InferType:
	 * 		infer Type
	 * </pre>
	 */
	protected TypeTree parseInferType(JSLexer src, Context context) {
		throw new JSUnsupportedException("Infer types", src.getPosition());
	}
	
	/**
	 * Unary-prefixed types:
	 * <pre>
	 * PrefixType:
	 * 		keyof PrefixType
	 * 		unique PrefixType
	 * 		infer IdentifierType
	 * 		PostfixType
	 * </pre>
	 */
	protected TypeTree parsePrefixType(JSLexer src, Context context) {
		Token lookahead = src.peek();
		if (lookahead.isIdentifier()) {
			switch (lookahead.<String>getValue()) {
				case "keyof": {
					src.skip(lookahead);
					TypeTree base = this.parseType(src, context);
					return new UnaryTypeTreeImpl(lookahead.getStart(), src.getPosition(), Kind.KEYOF_TYPE, base);
				}
				case "unique": {
					src.skip(lookahead);
					TypeTree base = this.parseType(src, context);
					return new UnaryTypeTreeImpl(lookahead.getStart(), src.getPosition(), Kind.UNIQUE_TYPE, base);
				}
				case "infer":
					return this.parseInferType(src, context);
				default:
					break;
			}
		}
		return this.parsePostfixType(src, context);
	}
	
	protected TypeTree parseCompositeType(Tree.Kind kind, BiFunction<JSLexer, Context, TypeTree> consumer, Predicate<Token> separatorMatcher, JSLexer src, Context context) {
		TypeTree first = consumer.apply(src, context);
		if (!separatorMatcher.test(src.peek()))
			return first;
		
		List<TypeTree> constituents = new ArrayList<>();
		constituents.add(first);
		while (src.nextTokenIf(separatorMatcher) != null)
			constituents.add(consumer.apply(src, context));
		
		return new CompositeTypeTreeImpl(first.getStart(), src.getPosition(), kind, constituents);
	}
	
	/**
	 * <pre>
	 * IntersectionType:
	 * 		PrefixType
	 * 		IntersectionType & PrefixType
	 * </pre>
	 */
	protected TypeTree parseIntersectionType(JSLexer src, Context context) {
		return this.parseCompositeType(Tree.Kind.TYPE_INTERSECTION, this::parsePrefixType, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.AMPERSAND), src, context);
	}
	
	/**
	 * <pre>
	 * UnionType:
	 * 		IntersectionType
	 * 		UnionType | IntersectionType
	 * </pre>
	 */
	protected TypeTree parseUnionType(JSLexer src, Context context) {
		return this.parseCompositeType(Tree.Kind.TYPE_UNION, this::parseIntersectionType, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.VBAR), src, context);
	}
	
	/**
	 * <pre>
	 * ConditionalType:
	 * 		UnionType
	 * 		UnionType extends Type[~conditional] ? Type[+conditional] : Type[+conditional]
	 * </pre>
	 */
	protected TypeTree parseConditionalType(JSLexer src, Context context) {
		TypeTree base = this.parseUnionType(src, context);
		if (!src.nextTokenIs(TokenKind.KEYWORD, JSKeyword.EXTENDS))
			return base;
		
		TypeTree limit = this.parseType(false, src, context.pushed().allowOptionalTypes(false));
		expectOperator(JSOperator.QUESTION_MARK, src, context);
		TypeTree concequent = this.parseType(true, src, context);
		expectOperator(JSOperator.COLON, src, context);
		TypeTree alternate = this.parseType(true, src, context);
		
		return new ConditionalTypeTreeImpl(base.getStart(), src.getPosition(), base, limit, concequent, alternate);
	}
	
	/**
	 * Parse a type declaration
	 * <pre>
	 * Type[Conditional]:
	 * 		FunctionSignatureType
	 * 		ConstructSignatureType
	 * 		[+Conditional]ConditionalType
	 * 		[~Conditional]UnionType
	 * </pre>
	 */
	protected TypeTree parseType(boolean allowConditional, JSLexer src, Context context) {
		// Handle function/construct signature types
		Token lookahead = src.peek();
		if (lookahead.matchesOperator(JSOperator.LESS_THAN)
				|| lookahead.matches(TokenKind.KEYWORD, JSKeyword.NEW)
				|| (lookahead.matchesOperator(JSOperator.LEFT_PARENTHESIS) && lookahead(this::isStartOfFunctionType, src, context)))
			return this.parseFunctionType(src, context);
		
		if (allowConditional)
			return this.parseConditionalType(src, context);
		else
			return this.parseUnionType(src, context);
	}
	
	protected TypeTree parseType(JSLexer src, Context context) {
		return this.parseType(true, src, context);
	}
	
	
	//Control flows
	
	/**
	 * <pre>
	 * DebuggerStatement:
	 * 		debugger ;
	 * </pre>
	 */
	protected DebuggerTree parseDebugger(JSLexer src, Context context) {
		Token debuggerKeywordToken = expectKeyword(JSKeyword.DEBUGGER, src, context);
		expectEOL(src, context);
		return new DebuggerTreeImpl(debuggerKeywordToken.getStart(), debuggerKeywordToken.getEnd());
	}
	
	/**
	 * <pre>
	 * BlockStatement[Yield, Await, Return]:
	 * 		Block[?Yield, ?Await, ?Return]
	 * Block[Yield, Await, Return]:
	 * 		{ StatementList[?Yield, ?Await, ?Return] }
	 * </pre>
	 */
	protected BlockTree parseBlock(JSLexer src, Context context) {
		Token openBraceToken = expectOperator(JSOperator.LEFT_BRACE, src, context);
		List<StatementTree> statements = new ArrayList<>();
		while (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_BRACE)) {
			//TODO: EOF problems?
			StatementTree statement = this.parseStatement(src, context);
			statements.add(statement);
			
			if (context.isDirectiveTarget())
				context.setDirectiveTarget(statement instanceof DirectiveTree);
		}
		
		return new BlockTreeImpl(openBraceToken.getStart(), src.getPosition(), statements);
	}
	
	/**
	 * Parse a {@code break} or {@code continue} statement, with optional label.
	 * <pre>
	 * ContinueStatement[Yield, Await]:
	 * 		continue ;
	 * 		continue [no line terminator] LabelIdentifier[?Yield, ?Await] ;
	 * BreakStatement[Yield, Await]:
	 * 		break ;
	 * 		break [no line terminator] LabelIdentifier[?Yield, ?Await] ;
	 * </pre>
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
	
	/**
	 * <pre>
	 * IfStatement[Yield, Await]:
	 * 		if ( Expression[?await, ?yield] ) Statement[?await, ?yield]
	 * 		if ( Expression[?await, ?yield] ) Statement[?await, ?yield] else Statement[?await, ?yield]
	 * </pre>
	 */
	protected IfTree parseIfStatement(JSLexer src, Context context) {
		Token ifKeywordToken = expectKeyword(JSKeyword.IF, src, context);
		
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
	
	/**
	 * <pre>
	 * SwitchStatement[Yield, Await]:
	 * 		switch ( Expression[?await, ?yield] ) { CaseList[?await, ?yield] }
	 * CaseList[Yield, Await]:
	 * 		CaseList[?await, ?yield] Case[?await, ?yield]
	 * Case[Yield, Await]:
	 * 		case Expression[?await, ?yield] : Statement[?await, ?yield]
	 * 		default: Statement[?await, ?yield]
	 * </pre>
	 */
	protected SwitchTree parseSwitchStatement(JSLexer src, Context context) {
		Token switchKeywordToken = expectKeyword(JSKeyword.SWITCH, src, context);
		src.expect(JSOperator.LEFT_PARENTHESIS);
		ExpressionTree expression = this.parseNextExpression(src, context);
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		src.expect(JSOperator.LEFT_BRACE);
		
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
				if (lookahead.matches(TokenKind.KEYWORD, JSKeyword.CASE) || lookahead.matches(TokenKind.KEYWORD, JSKeyword.DEFAULT) || lookahead.matchesOperator(JSOperator.RIGHT_BRACE))
						break;
				statements.add(parseStatement(src, context));
			}
			statements.trimToSize();
			
			cases.add(new SwitchCaseTreeImpl(next.getStart(), src.getPosition(), caseExpr, statements));
		}
		src.expect(JSOperator.RIGHT_BRACE);
		cases.trimToSize();
		
		return new SwitchTreeImpl(switchKeywordToken.getStart(), src.getPosition(), expression, cases);
	}
	
	/**
	 * Parse a try/catch, try/finally, or try/catch/finally statement
	 * <pre>
	 * TryStatement[Yield, Await, Return]:
	 * 		try Block[?Yield, ?Await, ?Return] CatchClause[?Yield, ?Await, ?Return]
	 * 		try Block[?Yield, ?Await, ?Return] FinallyClause[?Yield, ?Await, ?Return]
	 * 		try Block[?Yield, ?Await, ?Return] CatchClause[?Yield, ?Await, ?Return] FinallyClause[?Yield, ?Await, ?Return]
	 * CatchClause[Yield, Await, Return]:
	 * 		catch ( CatchParameter[?Yield, ?Await] ) Block[?Yield, ?Await, ?Return]
	 * FinallyClause[Yield, Await, Return]:
	 * 		finally Block[?Yield, ?Await, ?Return]
	 * CatchParameter[Yield, Await]:
	 * 		Pattern[?Yield, ?Await]
	 * </pre>
	 */
	protected TryTree parseTryStatement(JSLexer src, Context context) {
		Token tryKeywordToken = expectKeyword(JSKeyword.TRY, src, context);
		
		//Read the block that is in the try part
		BlockTree tryBlock = this.parseBlock(src, context);
		
		//Read all catch blocks
		ArrayList<CatchTree> catchBlocks = new ArrayList<>();
		
		Token next;
		while ((next = src.nextTokenIf(TokenKind.IDENTIFIER, "catch")) != null) {
			expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
			//TODO: any restrictions on param?
			VariableDeclaratorTree param = this.parseVariableDeclarator(src, context);
			
			expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
			BlockTree block = parseBlock(src, context);
			catchBlocks.add(new CatchTreeImpl(next.getStart(), block.getEnd(), block, param));
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
	
	/**
	 * <pre>
	 * WithStatement[Yield, Await, Return]:
	 * 		with ( Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
	 * </pre>
	 */
	protected WithTree parseWithStatement(JSLexer src, Context context) {
		Token withKeywordToken = expectKeyword(JSKeyword.WITH, src, context);
		
		if (context.isStrict())
			throw new JSSyntaxException("'with' blocks may not be used in strict mode", withKeywordToken.getStart());
		
		src.expect(JSOperator.LEFT_PARENTHESIS);
		
		context.push();
		context.withIn();
		ExpressionTree expression = this.parseNextExpression(src, context);
		context.pop();
		
		src.expect(JSOperator.RIGHT_PARENTHESIS);
		StatementTree statement = this.parseStatement(src, context);
		
		//TODO check if statement is valid (isLabelledFunction)
		return new WithTreeImpl(withKeywordToken.getStart(), src.getPosition(), expression, statement);
	}
	
	// Loops
	
	/**
	 * <pre>
	 * WhileLoopStatement[Yield, Await, Return]:
	 * 		while ( Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
	 * </pre>
	 */
	@JSKeywordParser({JSKeyword.WHILE})
	protected WhileLoopTree parseWhileLoop(JSLexer src, Context context) {
		Token whileKeywordToken = expectKeyword(JSKeyword.WHILE, src, context);
		
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		ExpressionTree condition = this.parseNextExpression(src, context);
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
		//Parse loop statement
		context.push().enterLoop();
		StatementTree statement = parseStatement(src, context);
		context.pop();
		
		return new WhileLoopTreeImpl(whileKeywordToken.getStart(), src.getPosition(), condition, statement);
	}
	
	/**
	 * <pre>
	 * DoWhileLoopStatement[Yield, Await, Return]:
	 * 		do Statement[?Yield, ?Await, ?Return] while ( Expression[+In, ?Yield, ?Await] ) ;
	 * </pre>
	 */
	@JSKeywordParser({JSKeyword.DO})
	protected DoWhileLoopTree parseDoWhileLoop(JSLexer src, Context context) {
		Token doKeywordToken = expectKeyword(JSKeyword.DO, src, context);
		
		context.push().enterLoop();
		StatementTree statement = parseStatement(src, context);
		context.pop();
		
		expectKeyword(JSKeyword.WHILE, src, context);
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
		Token forKeywordToken = expectKeyword(JSKeyword.FOR, src, context);
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		
		Token lookahead = src.peek();
		if (lookahead.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)) {
			src.skip(lookahead);
			//Empty initializer statement
			return this.parsePartialForLoopTree(forKeywordToken, new EmptyStatementTreeImpl(lookahead), src, context);
		}
		
		StatementTree initializer = null;
		if (TokenPredicate.VARIABLE_START.test(lookahead)) {
			context.push();
			context.disallowIn();
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
			context.disallowIn();
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
		ExpressionTree update = src.peek().matchesOperator(JSOperator.RIGHT_PARENTHESIS) ? null : parseNextExpression(src, context);
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
	 * Syntax:
	 * <pre>
	 * Expression[In, Yield, Await]:
	 * 		AssignmentExpression[?In, ?Yield, ?Await]
	 * 		Expression[?In, ?Yield, ?Await] , AssignmentExpression[?In, ?Yield, ?Await]
	 * </pre>
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
	 * Syntax:
	 * <pre>
	 * ExponentiationExpression[Yield, Await]:
	 * 		UnaryExpression[?Yield, ?Await]
	 * 		UpdateExpression[?Yield, ?Await] ** ExponentiationExpression[?Yield, ?Await]
	 * </pre>
	 * @see #parseUnaryExpression(JSLexer, Context)
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
				case VBAR:
					return 7;
				case BITWISE_XOR:
					return 8;
				case AMPERSAND:
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
				case ASTERISK:
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
					if (!context.inAllowed())
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
	 * <pre>
	 * AsCastExpression[Yield, Await]:
	 * 		Expression[?Yield, ?Await] as Type
	 * </pre>
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
	
	/**
	 * <pre>
	 * ConditionalExpression[In, Yield, Await]:
	 * 		BinaryExpression[?In, ?Yield, ?Await]
	 * 		BinaryExpression[?In, ?Yield, ?Await] ? AssignmentExpression[+In, ?Yield, ?Await] : AssignmentExpression[?In, ?Yield, ?Await]
	 * </pre>
	 */
	protected ExpressionTree parseConditional(JSLexer src, Context context) {
		context.isolateCoverGrammar();
		ExpressionTree expr = this.parseBinaryExpression(src, context);
		context.inheritCoverGrammar();
		
		src.mark();
		if (!src.nextTokenIs(TokenKind.OPERATOR, JSOperator.QUESTION_MARK)) {
			src.unmark();
			return expr;
		}
		
		//Shortcut to optional property w/type
		//The only time when the sequences '?:', '?,', or '?)' will occur are in a function definition
		if (context.isMaybeParam()) {
			Token lookahead = src.peek();
			if (lookahead.matchesOperator(JSOperator.COLON) || lookahead.matchesOperator(JSOperator.COMMA) || lookahead.matchesOperator(JSOperator.RIGHT_PARENTHESIS)) {
				src.reset();
				return expr;
			}
		}
		src.unmark();
		
		
		context.push();
		context.allowIn();
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
	
	/**
	 * <pre>
	 * Initializer[Yield, Await]:
	 * 		<empty>
	 * 		= AssignmentExpression[+In, ?Yield, ?Await]
	 * </pre>
	 */
	protected ExpressionTree parseInitializer(JSLexer src, Context context) {
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASSIGNMENT))
			return this.parseAssignment(src, context);
		return null;
	}
	
	/**
	 * <pre>
	 * AssignmentExpression[In, Yield, Await]:
	 * 		ConditionalExpression[?In, ?Yield, ?Await]
	 * 		[+Yield]YieldExpression[?In, ?Await]
	 * 		ArrowFunction[?In, ?Yield, ?Await]
	 * 		AsyncArrowFunction[?In, ?Yield, ?Await]
	 * 		LeftHandSideExpression[?Yield, ?Await] = AssignmentExpression[?In, ?Yield, ?Await]
	 * 		LeftHandSideExpression[?Yield, ?Await] AssignmentOperator AssignmentExpression[?In, ?Yield, ?Await]	
	 * </pre>
	 */
	protected ExpressionTree parseAssignment(JSLexer src, Context context) {
		Token lookahead = src.peek();
		SourcePosition start = src.getPosition();
		
		//Check if this could possibly start a parameter
		if (context.isMaybeParam() && !TokenPredicate.START_OF_PARAMETER.test(lookahead))
			context.isMaybeParam(false);
		
		if (context.allowYield() && lookahead.matches(TokenKind.KEYWORD, JSKeyword.YIELD))
			return this.parseYield(src, context);
		
		ExpressionTree expr = this.parseConditional(src, context);
		
		//Upgrade to lambda
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LAMBDA))
			//TODO: declared return value?
			return this.finishFunctionBody(expr.getStart(), Modifiers.NONE, null, null, this.reinterpretExpressionAsParameterList(expr), null, true, src, context);
		
		lookahead = src.peek();
		if (!lookahead.isOperator() || !lookahead.<JSOperator>getValue().isAssignment())
			return expr;
		
		if (!context.isAssignmentTarget())
			throw new JSSyntaxException("Not assignment target", expr.getRange());
		
		Token assignmentOperator = src.nextToken();
		
		PatternTree variable;
		if (assignmentOperator.matchesOperator(JSOperator.ASSIGNMENT)) {
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
		return new AssignmentTreeImpl(start, right.getEnd(), this.mapTokenToBinaryKind(assignmentOperator), variable, right);
	}
	
	/**
	 * <pre>
	 * Parameter[Yield, Await]:
	 * 		ThisParameter
	 * 		RestParameter[Yield, Await]
	 * 		ParamPrefixModifiers Pattern ParamPostfixModifiers TypeAnnotation? Initializer[+In, ?Yield, ?Await]
	 * ThisParameter:
	 * 		this TypeAnnotation?
	 * RestParameter[Yield, Await]:
	 * 		ParamPrefixModifiers BindingRestElement[?Yield, ?Await] TypeAnnotation? Initializer[+In, ?Yield, ?Await]
	 * ParamPrefixModifiers:
	 * 		public
	 * 		private
	 * 		protected
	 * 		readonly
	 * </pre>
	 */
	protected ParameterTree parseParameter(JSLexer src, Context context) {
		Token lookahead = src.peek();
		SourcePosition start = lookahead.getStart();
		
		List<DecoratorTree> decorators = this.parseDecorators(src, context);
		if (!decorators.isEmpty())
			//TODO: parameter decorators
			throw new JSUnsupportedException("Parameter decorators", src.getPosition());
		
		if (lookahead.matches(TokenKind.KEYWORD, JSKeyword.THIS)) {
			// 'fake' this-parameter
			dialect.require("ts.types", lookahead.getRange());
			IdentifierTree name = this.asIdentifier(src.skip(lookahead));
			TypeTree type = this.parseTypeMaybe(src, context, true);
			// Initializers not allowed
			lookahead = src.peek();
			if (lookahead.matchesOperator(JSOperator.ASSIGNMENT))
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
		ExpressionTree initializer = this.parseInitializer(src, context);
		
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
		if (src.peek().matchesOperator(JSOperator.RIGHT_PARENTHESIS))
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
		ExpressionTree initializer = this.parseInitializer(src, context);
		
		Modifiers modifiers = Modifiers.NONE;
		if (optionalToken != null)
			modifiers = modifiers.combine(Modifiers.OPTIONAL);
		
		parameters.add(new ParameterTreeImpl(lastParam.getStart(), src.getPosition(), modifiers, (IdentifierTree) lastParam, false, type, initializer));
		
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
	 * <pre>
	 * GroupExpression[Yield, Await]:
	 * 	
	 * </pre>
	 * @return Either a ParenthesizedTree or a FunctionExpressionTree
	 */
	@SuppressWarnings("unchecked")
	protected ExpressionTree parseGroupExpression(JSLexer src, Context context) {
		Token leftParenToken = expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		
		//Check for easy upgrades to lambda expression
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.RIGHT_PARENTHESIS)) {
			//Is lambda w/ no args ("()=>???")
			dialect.require("js.function.lambda", leftParenToken.getRange());
			expectOperator(JSOperator.LAMBDA, src, context);
			return this.finishFunctionBody(leftParenToken.getStart(), Modifiers.NONE, null, null, Collections.emptyList(), null, true, src, context);
		} else if (src.peek().matchesOperator(JSOperator.SPREAD)) {
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
				if (src.peek().matchesOperator(JSOperator.SPREAD)) {
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
					if (lookahead.matchesOperator(JSOperator.QUESTION_MARK) || lookahead.matchesOperator(JSOperator.COLON))
						return upgradeGroupToLambdaFunction(leftParenToken.getStart(), (List<ExpressionTree>)(List<?>)expressions, expression, src, context);
					
					
					expressions.add(expression);
				}
			} while (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.COMMA));
			
			//Ensure that it exited the loop with a closing paren
			expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
			
			//Sequence, but not lambda
			return new ParenthesizedTreeImpl(leftParenToken.getStart(), src.getPosition(), new SequenceExpressionTreeImpl((List<ExpressionTree>)(List<?>)expressions));
		}
		//Only one expression
		expectOperator(JSOperator.RIGHT_PARENTHESIS, src, context);
		
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
		Token spreadToken = expectOperator(JSOperator.SPREAD, src, context);
		dialect.require("js.operator.spread", spreadToken.getRange());
		
		context.isolateCoverGrammar();
		final ExpressionTree expr = this.parseAssignment(src, context);
		context.inheritCoverGrammar();
		
		return new SpreadElementTreeImpl(spreadToken.getStart(), expr.getEnd(), expr);
	}
	
	/**
	 * <pre>
	 * NewExpression[Yield, Await]:
	 * 		MemberExpression[?Yield, ?Await]
	 * 		new NewExpression[?Yield, ?Await]
	 * MemberExpression[Yield, Await]:
	 * 		PrimaryExpression[?Yield, ?Await]
	 * 		MemberExpression[?Yield, ?Await] [ Expression[+In, ?Yield, ?Await] ]
	 * 		MemberExpression[?Yield, ?Await] . IdentifierName
	 * 		MemberExpression[?Yield, ?Await] TemplateLiteral[?Yield, ?Await, +Tagged]
	 * 		SuperProperty[?Yield, ?Await]
	 * 		MetaProperty
	 * 		new MemberExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
	 * SuperProperty[Yield, Await]:
	 * 		super [ Expression[+In, ?Yield, ?Await] ]
	 * 		super . IdentifierName
	 * MetaProperty:
	 * 		NewTarget
	 * NewTarget:
	 * 		new . target
	 * </pre>
	 */
	protected ExpressionTree parseNew(JSLexer src, Context context) {
		Token newKeywordToken = expectKeyword(JSKeyword.NEW, src, context);
		
		// Support 'new.target' metaproperty
		Token t;
		if ((t = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.PERIOD)) != null) {
			Token r = src.nextToken();
			if (context.inFunction() && r.matches(TokenKind.IDENTIFIER, "target")) {
				//See developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/new.target
				IdentifierTree idNew = new IdentifierTreeImpl(newKeywordToken.reinterpretAsIdentifier());
				IdentifierTree idTarget = new IdentifierTreeImpl(r);
				return new MemberExpressionTreeImpl(Tree.Kind.MEMBER_SELECT, idNew, idTarget);
			}
			
			throw new JSUnexpectedTokenException(t);
		}
		
		ExpressionTree callee = this.parseLeftSideExpression(false, src, context.coverGrammarIsolated());
		
		List<TypeTree> typeArgs = tryParse(this::parseTypeArgumentsMaybe, this::canFollowExpressionTypeArguments, src, context);
		
		//TODO: finish member expression
		//TODO: type arguments
		
		final List<ExpressionTree> args;
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_PARENTHESIS))
			args = this.parseArguments(src, context);
		else
			args = null;
		
		return new NewTreeImpl(newKeywordToken.getStart(), src.getPosition(), callee, typeArgs, args);
	}
	
	/**
	 * <pre>
	 * LeftHandSideExpression[Yield, Await]:
	 * 		NewExpression[?Yield, ?Await]
	 * 		CallExpression[?Yield, ?Await]
	 * </pre>
	 */
	protected ExpressionTree parseLeftSideExpression(boolean allowCall, JSLexer src, Context context) {
		context.push(); // +In
		
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
			if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.LEFT_BRACKET)) {
				//Computed member access expressions
				context.isBindingElement(false);
				context.isAssignmentTarget(true);
				ExpressionTree property = this.parseNextExpression(src, context.coverGrammarIsolated());
				expectOperator(JSOperator.RIGHT_BRACKET, src, context);
				expr = new MemberExpressionTreeImpl(expr.getStart(), src.getPosition(), Kind.ARRAY_ACCESS, expr, property);
			} else if (allowCall && lookahead.matchesOperator(JSOperator.LEFT_PARENTHESIS)) {
				//Function call
				context.isBindingElement(false);
				context.isAssignmentTarget(false);
				expr = this.finishParsingFunctionCall(expr, src, context);
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
		
		context.pop(~Context.FLAG_IN);
		return expr;
	}
	
	/**
	 * Parse a function call expression, in the form of {@code [expr]([expr],...,[expr])}.
	 * <pre>
	 * CallExpression[Yield, Await]:
	 * 		CallExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
	 * 		<TODO: finish>
	 * </pre>
	 * @param functionSelectExpression
	 *     Expression that contains the expression of the function that is being called
	 * @returns AST for function call expression
	 */
	protected FunctionCallTree finishParsingFunctionCall(ExpressionTree functionSelectExpression, JSLexer src, Context context) {
		//TODO: type arguments
		// Make sure that we have the token for the open paren of the function call
		expectOperator(JSOperator.LEFT_PARENTHESIS, src, context);
		//Read function arguments (also consumes closing token)
		List<? extends ExpressionTree> arguments = this.parseArguments(src, context);
		
		return new FunctionCallTreeImpl(functionSelectExpression.getStart(), src.getPosition(), functionSelectExpression, Collections.emptyList(), arguments);
	}
	
	protected boolean isIdentifier(Token identifierToken, Context context) {
		if (identifierToken.isIdentifier()) {
			String name = identifierToken.<String>getValue();
			return !(context.isStrict() && ("eval".equals(name) || "arguments".equals(name)));
		} else if (identifierToken.isKeyword()) {
			switch (identifierToken.<JSKeyword>getValue()) {
				case AWAIT:
					if (context.allowAwait())
						return false;
					return true;
				case YIELD:
					if (context.allowYield())
						return false;
					//Fallthrough intentional
				case AS:
					// Contextual keywords
					return true;
				case LET:
				case PUBLIC:
				case PRIVATE:
				case PROTECTED:
				case STATIC:
				case IMPLEMENTS:
				case INTERFACE:
				case PACKAGE:
					// Strict-mode disallowed keywords
					return !context.isStrict();
				default:
					return false;
			}
		}
		return false;
	}
	
	protected String asIdentifierName(Token identifierToken) throws JSSyntaxException {
		//'yield' can be an identifier if yield expressions aren't allowed in the current context
		if (!identifierToken.isIdentifier())
			identifierToken = identifierToken.reinterpretAsIdentifier();
		
		return identifierToken.getValue();
	}
	
	/**
	 * Precondition: identifierToken is a valid identifier in context
	 * @param identifierToken
	 * @return
	 */
	protected IdentifierTree asIdentifier(Token identifierToken) {
		String name = this.asIdentifierName(identifierToken);
		return new IdentifierTreeImpl(identifierToken.getStart(), identifierToken.getEnd(), name);
	}
	
	protected IdentifierTree parseIdentifierMaybe(JSLexer src, Context context) {
		//Keep reference to lookahead (if applicable) to skip over it at the end
		Token lookahead = src.peek();
		
		if (!this.isIdentifier(lookahead, context))
			return null;
		
		return this.parseIdentifier(src, context);
	}
	
	protected IdentifierTree parseIdentifier(JSLexer src, Context context) {
		Token id = src.nextToken();
		if (!this.isIdentifier(id, context))
			throw new JSSyntaxException(id + " not allowed as identifier", id.getRange());
		return this.asIdentifier(id);
	}
	
	protected ThisExpressionTree parseThis(JSLexer src, Context ctx) {
		return new ThisExpressionTreeImpl(expectKeyword(JSKeyword.THIS, src, ctx));
	}
	
	/**
	 * Parse super for use in LHS of:
	 * <pre>
	 * 
	 * </pre>
	 */
	protected SuperExpressionTree parseSuper(JSLexer src, Context context) {
		SuperExpressionTree result = new SuperExpressionTreeImpl(expectKeyword(JSKeyword.SUPER, src, context));
		Token tmp = src.peek();
		if (!(tmp.matchesOperator(JSOperator.LEFT_BRACKET) || tmp.matchesOperator(JSOperator.PERIOD)))
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
		if (src.peek().matchesOperator(JSOperator.LEFT_BRACE)) {
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
		SourcePosition startPos = src.peek().getStart();
		Token functionKeywordToken = src.nextToken();
		
		Modifiers modifiers = Modifiers.NONE;
		
		//The first token could be an async modifier
		if (functionKeywordToken.matches(TokenKind.IDENTIFIER, "async")) {
			dialect.require("js.function.async", functionKeywordToken.getRange());
			modifiers = modifiers.combine(Modifiers.ASYNC);
			functionKeywordToken = src.nextToken();
		}
		
		
		//functionKeywordToken should be `function`
		if (!functionKeywordToken.matches(TokenKind.KEYWORD, JSKeyword.FUNCTION))
			throw new JSUnexpectedTokenException(functionKeywordToken);
		
		Token asteriskToken = src.nextTokenIf(TokenKind.OPERATOR, JSOperator.ASTERISK);
		if (asteriskToken != null) {
			dialect.require("js.function.generator", asteriskToken.getRange());
			modifiers = modifiers.combine(Modifiers.GENERATOR);
		}
		
		
		//Parse function identifier
		IdentifierTree identifier = this.parseIdentifierMaybe(src, context);
		
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
	 * <pre>
	 * ArrayLiteralElement[Yield, Await]:
	 * 		SpreadExpression[+In, ?Yield, ?Await]
	 * 		AssignmentExpression[+In, ?Yield, ?Await]
	 * </pre>
	 */
	protected ExpressionTree parseArrayLiteralElement(JSLexer src, Context context) {
		Token lookahead = src.peek();
		
		if (lookahead.matchesOperator(JSOperator.COMMA))
			return null;
		
		if (lookahead.matchesOperator(JSOperator.SPREAD)) {
			ExpressionTree result = this.parseSpread(src, context.withIn());
			if (!src.peek().matchesOperator(JSOperator.RIGHT_BRACKET)) {
				//TODO: why not set always?
				context.isAssignmentTarget(false);
				context.isBindingElement(false);
			}
			return result;
		}
		
		return this.parseAssignment(src, context.coverGrammarIsolated().allowIn());
	}
	/**
	 * Parse array literal.
	 * <pre>
	 * ArrayLiteral[Yield, Await]:
	 * 		[ Elision? ]
	 * 		[ ElementList[?Yield, ?Await] ]
	 * 		[ ElementList[?Yield, ?Await] , Elision? ]
	 * </pre>
	 * @see #parseArrayLiteralElement(JSLexer, Context)
	 */
	protected ArrayLiteralTree parseArrayInitializer(JSLexer src, Context context) {
		Token startToken = expectOperator(JSOperator.LEFT_BRACKET, src, context);
		
		List<ExpressionTree> values = this.parseDelimitedList(this::parseArrayLiteralElement, this::parseCommaSeparator, TokenPredicate.match(TokenKind.OPERATOR, JSOperator.RIGHT_BRACKET), src, context);
		
		expectOperator(JSOperator.RIGHT_BRACKET, src, context);
		
		return new ArrayLiteralTreeImpl(startToken.getStart(), src.getPosition(), values);
	}
	
	/**
	 * <pre>
	 * PropertyName[Yield, Await]:
	 * 		LiteralPropertyName
	 * 		ComputedPropertyName[?Yield, ?Await]
	 * LiteralPropertyName:
	 * 		IdentifierName
	 * 		StringLiteral
	 * 		NumericLiteral
	 * ComputedPropertyName[Yield, Await]:
	 * 		[ AssignmentExpression[+In, ?Yield, ?Await] ]
	 * </pre>
	 */
	protected PropertyName parsePropertyName(JSLexer src, Context context) {
		Token lookahead = src.peek();
		SourcePosition start = lookahead.getStart();
		IdentifierTree id = this.parseIdentifierMaybe(src, context);
		if (id != null)
			return id;
		
		switch (lookahead.getKind()) {
			case NUMERIC_LITERAL:
			case STRING_LITERAL:
				return (PropertyName) this.parseLiteral(null, src, context);
			case BOOLEAN_LITERAL:
			case NULL_LITERAL:
				return new IdentifierTreeImpl(src.skip(lookahead).reinterpretAsIdentifier());
			case IDENTIFIER:
			case KEYWORD:
				return this.parseIdentifier(src, context);
			case OPERATOR:
				//Computed property
				if (lookahead.<JSOperator>getValue() == JSOperator.LEFT_BRACKET) {
					src.skip(lookahead);
					ExpressionTree expr = this.parseAssignment(src, context);//TODO: push +In
					expectOperator(JSOperator.RIGHT_BRACKET, src, context);
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
			case OPERATOR:
				return t.<JSOperator>getValue() == JSOperator.LEFT_BRACKET;
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
	
	/**
	 * <pre>
	 * PropertyDefinition[Yield, Await]:
	 * 		IdentifierReference[?Yield, ?Await]
	 * 		CoverInitializedName[?Yield, ?Await]
	 * 		PropertyName[?Yield, ?Await] : AssignmentExpression[+In, ?Yield, ?Await]
	 * 		MethodDefinition[?Yield, ?Await]
	 * 		SpreadExpression[+In, ?Yield, ?Await]
	 * </pre>
	 */
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
		
		if (src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASTERISK))
			modifiers = modifiers.combine(Modifiers.GENERATOR);
		
		PropertyName name = this.parsePropertyName(src, context);
		
		// Parse optional/definite postfix modifiers 
		modifiers = modifiers.combine(this.parseModifiers(Modifiers.MASK_POSTFIX, false, src, context));
		
		Token lookahead = src.peek();
		if (modifiers.isGenerator() || TokenPredicate.CALL_SIGNATURE_START.test(lookahead)) {
			// Method declaration
			return this.parseMethodDeclaration(start, decorators, modifiers, name, src, context);
		} else if (name.getKind() == Kind.IDENTIFIER
				&& (lookahead.matchesOperator(JSOperator.COMMA)
						|| lookahead.matchesOperator(JSOperator.ASSIGNMENT)
						|| lookahead.matchesOperator(JSOperator.RIGHT_BRACE))) {
			// Shorthand property assignment
			// <name> [= <initializer>]
			ExpressionTree initializer = this.parseInitializer(src, context);
			
			return new ShorthandAssignmentPropertyTreeImpl(start, src.getPosition(), modifiers, (IdentifierTree) name, initializer);
		} else {
			// Normal property assignment
			// Form <name>: <value>
			expectOperator(JSOperator.COLON, src, context);
			ExpressionTree value = this.parseAssignment(src, context.pushed().allowIn());
			return new AssignmentPropertyTreeImpl(start, src.getPosition(), modifiers, name, value);
		}
	}
	
	/**
	 * Parse object literal.
	 * <pre>
	 * ObjectLiteral[Yield, Await]:
	 * 		{ }
	 * 		{ PropertyDefinitionList }
	 * </pre>
	 * @see #parseObjectProperty(JSLexer, Context)
	 */
	protected ObjectLiteralTree parseObjectInitializer(JSLexer src, Context context) {
		Token startToken = expectOperator(JSOperator.LEFT_BRACE, src, context);
		
		List<ObjectLiteralElement> properties = this.parseDelimitedList(this::parseObjectProperty, this::parseCommaSeparator, TokenPredicate.RIGHT_BRACE, src, context);
		
		expectOperator(JSOperator.RIGHT_BRACE, src, context);
		
		return new ObjectLiteralTreeImpl(startToken.getStart(), src.getPosition(), properties);
	}
	
	//Unary ops
	
	/**
	 * Syntax:
	 * <pre>
	 * TypeAssertion[Yield, Await]:
	 * 		< Type > BinaryExpression[?Yield, ?Await]
	 * </pre>
	 * @param src
	 * @param context
	 * @return
	 */
	protected ExpressionTree parseTypeAssertion(JSLexer src, Context context) {
		//Angle bracket casting
		Token lhsBracket = expectOperator(JSOperator.LESS_THAN, src, context);
		TypeTree type = this.parseType(src, context);
		expectOperator(JSOperator.GREATER_THAN, src, context);
		ExpressionTree rhs = this.parseBinaryExpression(src, context);
		
		SourcePosition start = lhsBracket.getStart(), end = src.getPosition();
		dialect.require("ts.types.cast", new SourceRange(start, end));
		return new CastExpressionTreeImpl(start, end, rhs, type);
	}
	
	/**
	 * Map token kind/value to AST {@link Tree.Kind}.
	 * @param token
	 * @return AST kind, or {@code null} if no mapping present
	 */
	protected Tree.Kind mapTokenToUnaryKind(Token token) {
		switch (token.getKind()) {
			case KEYWORD:
				switch (token.<JSKeyword>getValue()) {
					case VOID:
						return Tree.Kind.VOID;
					case TYPEOF:
						return Tree.Kind.TYPEOF;
					case DELETE:
						return Tree.Kind.DELETE;
					default:
						break;
				}
				break;
			case OPERATOR:
				switch (token.<JSOperator>getValue()) {
					case PLUS:
						return Tree.Kind.UNARY_PLUS;
					case MINUS:
						return Tree.Kind.UNARY_MINUS;
					case BITWISE_NOT:
						return Tree.Kind.BITWISE_NOT;
					case LOGICAL_NOT:
						return Tree.Kind.LOGICAL_NOT;
					case INCREMENT:
						return Tree.Kind.PREFIX_INCREMENT;
					case DECREMENT:
						return Tree.Kind.PREFIX_DECREMENT;
					default:
						break;
				}
				break;
			default:
				break;
		}
		// No valid mapping
		return null;
	}
	
	/**
	 * Parse an unary expression (in form of {@code {OP} {EXPR}} or {@code {EXPR} {OP}}).
	 * <pre>
	 * UnaryExpression[Yield, Await]:
	 * 		UpdateExpression[?Yield, ?Await]
	 * 		delete UnaryExpression[?Yield, ?Await]
	 * 		void UnaryExpression[?Yield, ?Await]
	 * 		typeof UnaryExpression[?Yield, ?Await]
	 * 		+ UnaryExpression[?Yield, ?Await]
	 * 		- UnaryExpression[?Yield, ?Await]
	 * 		~ UnaryExpression[?Yield, ?Await]
	 * 		! UnaryExpression[?Yield, ?Await]
	 * 		[+Await] AwaitExpression[?Yield]
	 * </pre>
	 */
	protected ExpressionTree parseUnaryExpression(JSLexer src, Context context) {
		Token lookahead = src.peek();
		
		// Special cases
		if (context.allowYield() && lookahead.matches(TokenKind.KEYWORD, JSKeyword.YIELD))
			return this.parseYield(src, context);
		if (lookahead.matchesOperator(JSOperator.SPREAD))
			return this.parseSpread(src, context);
		if (context.allowAwait() && lookahead.matches(TokenKind.KEYWORD, JSKeyword.AWAIT))
			return this.parseAwait(src, context);
		if (dialect.supports("ts.types.cast") && lookahead.matchesOperator(JSOperator.LESS_THAN))
			return this.parseTypeAssertion(src, context);
		
		Tree.Kind kind = this.mapTokenToUnaryKind(lookahead);
		if (kind == null)
			return this.parseUnaryPostfix(src, context);
		
		final SourcePosition start = src.skip(lookahead).getStart();
		
		ExpressionTree expression;
		//TODO: better ASI check?
		if (kind == Kind.VOID && src.peek().getKind() == TokenKind.SPECIAL)
			expression = null;
		else
			expression = this.parseUnaryExpression(src, context.coverGrammarIsolated());
		
		final SourcePosition end = src.getPosition();
		
		// Some unary types require us to validate the expression
		//TODO: move to validation pass?
		switch (kind) {
			case PREFIX_INCREMENT:
			case PREFIX_DECREMENT:
				// Update expressions: Require RHS to be assignable
				// Check if the target can be modified
				// TODO: move to validation? (we can't check if we're modifying a `readonly` property here anyways)
				if (!Validator.canBeAssigned(expression, dialect))
					throw new JSSyntaxException("Invalid right-hand side expression in " + kind + " expression", start, end);
				break;
			case DELETE:
				// Delete expression: Target must be qualified
				if (context.isStrict() && expression.getKind() == Tree.Kind.IDENTIFIER) {
					//TODO: move to validation pass?
					String identName = ((IdentifierTree) expression).getName();
					throw new JSSyntaxException("Cannot delete unqualified identifier " + identName + " in strict mode", start, end);
				}
				break;
			default:
				// No other checks here
				break;
		}
		
		return new UnaryTreeImpl(start, end, expression, kind);
	}
	
	/**
	 * <pre>
	 * UpdateExpression[Yield, Await]:
	 * 		LeftHandSideExpression[?Yield, ?Await]
	 * 		LeftHandSideExpression[?Yield, ?Await] [no line terminator] ++
	 * 		LeftHandSideExpression[?Yield, ?Await] [no line terminator] --
	 * 		++ UnaryExpression[?Yield, ?Await]
	 * 		-- UnaryExpression[?Yield, ?Await]
	 * </pre>
	 * @param src
	 * @param context
	 * @return
	 */
	protected ExpressionTree parseUnaryPostfix(JSLexer src, Context context) {
		ExpressionTree expr = this.parseLeftSideExpression(true, src, context.pushed());
		
		Token operatorToken = src.nextTokenIf(TokenPredicate.UPDATE_OPERATOR);
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
		Token yieldKeywordToken = expectKeyword(JSKeyword.YIELD, src, context);
		
		dialect.require("js.yield", yieldKeywordToken.getRange());
		
		//Check if it's a 'yield*'
		boolean delegates = src.nextTokenIs(TokenKind.OPERATOR, JSOperator.ASTERISK);
		
		//Parse RHS of expression
		Token lookahead = src.peek();
		ExpressionTree argument = delegates
				|| !(lookahead.matches(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON)
				|| lookahead.matchesOperator(JSOperator.RIGHT_PARENTHESIS)
				|| lookahead.matchesOperator(JSOperator.RIGHT_BRACE)) ? this.parseAssignment(src, context) : null;
		
		return new UnaryTreeImpl(yieldKeywordToken.getStart(), src.getPosition(), argument, delegates ? Kind.YIELD_GENERATOR : Kind.YIELD);
	}
	
	public static class Context {
		// Context flags (propagate down only)
		static final int FLAG_IN                = (1 <<  0);
		static final int FLAG_YIELD             = (1 <<  1);
		static final int FLAG_AWAIT             = (1 <<  2);
		static final int FLAG_RETURN            = (1 <<  3);
		static final int FLAG_BREAK             = (1 <<  4);
		static final int FLAG_CONTINUE          = (1 <<  5);
		// SS flags (propagate across)
		static final int FLAG_ASSIGNMENT_TARGET = (1 <<  6);
		static final int FLAG_NAMED_BLOCK       = (1 <<  7);
		static final int FLAG_BINDING_ELEMENT   = (1 <<  8);
		static final int FLAG_STRICT            = (1 <<  9);
		static final int FLAG_DIRECTIVE_TARGET  = (1 << 10);
		static final int FLAG_MAYBE_PARAMETER   = (1 << 11);
		static final int FLAG_AMBIENT           = (1 << 12);
		// Scope flags
		static final int FLAG_FUNCTION          = (1 << 13);
		static final int FLAG_GENERATOR         = (1 << 14);
		static final int FLAG_SWITCH            = (1 << 15);
		static final int FLAG_LOOP              = (1 << 16);
		// Type context flags
		static final int FLAG_DEFINITE_TYPE     = (1 << 17);
		static final int FLAG_OPTIONAL_TYPE     = (1 << 18);
		
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
			int mask = Context.FLAG_BINDING_ELEMENT | Context.FLAG_ASSIGNMENT_TARGET;
			int masked = this.data.flags & mask;
			this.pop();
			this.data.flags &= masked | ~mask;
			return this;
		}
		
		public Context inheritingCoverGrammar() {
			int rcMask = Context.FLAG_BINDING_ELEMENT | Context.FLAG_ASSIGNMENT_TARGET;
			int rsMask = ~rcMask;
			return new Context(new ContextData(this.data, rsMask, rcMask));
		}
		
		public Context pop() {
			data = data.parent;
			return this;
		}
		
		public Context pop(int inheritFlags) {
			int iFlags = data.flags & inheritFlags;
			this.pop();
			data.flags = (data.flags & ~inheritFlags) | iFlags;
			return this;
		}
		
		public boolean isAmbient() {
			return data.hasFlags(Context.FLAG_AMBIENT);
		}
		
		public boolean isStrict() {
			return data.hasFlags(Context.FLAG_STRICT);
		}
		
		public boolean isDirectiveTarget() {
			return data.hasFlags(Context.FLAG_DIRECTIVE_TARGET);
		}
		
		public boolean allowReturn() {
			return data.hasFlags(Context.FLAG_RETURN);
		}
		
		public boolean inAllowed() {
			return data.hasFlags(Context.FLAG_IN);
		}

		public boolean allowBreak() {
			return data.hasFlags(Context.FLAG_BREAK);
		}
		
		public boolean allowContinue() {
			return data.hasFlags(Context.FLAG_CONTINUE);
		}
		
		public boolean allowYield() {
			return data.hasFlags(Context.FLAG_YIELD);
		}
		
		public Context allowOptionalTypes(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_OPTIONAL_TYPE);
			else
				data.clearFlags(Context.FLAG_OPTIONAL_TYPE);
			return this;
		}
		
		public boolean allowDefiniteTypes() {
			return data.hasFlags(Context.FLAG_DEFINITE_TYPE);
		}
		
		public Context allowDefiniteTypes(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_DEFINITE_TYPE);
			else
				data.clearFlags(Context.FLAG_DEFINITE_TYPE);
			return this;
		}
		
		public boolean allowOptionalTypes() {
			return data.hasFlags(Context.FLAG_OPTIONAL_TYPE);
		}
		
		public boolean inBinding() {
			return data.hasFlags(Context.FLAG_BINDING_ELEMENT);
		}

		public boolean inFunction() {
			return data.hasFlags(Context.FLAG_FUNCTION);
		}
		
		public Context disallowIn() {
			data.clearFlags(Context.FLAG_IN);
			return this;
		}
		
		public Context withIn() {
			return this.pushed().allowIn();
		}

		public Context allowIn() {
			data.setFlags(Context.FLAG_IN);
			return this;
		}
		
		public Context allowAwait(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_AWAIT);
			else
				data.clearFlags(Context.FLAG_AWAIT);
			return this;
		}
		
		public boolean allowAwait() {
			return data.hasFlags(Context.FLAG_AWAIT);
		}

		public boolean isAssignmentTarget() {
			return data.hasFlags(Context.FLAG_ASSIGNMENT_TARGET);
		}
		
		public boolean isMaybeParam() {
			return data.hasFlags(Context.FLAG_MAYBE_PARAMETER);
		}

		/**
		 * Marks this level of the context as being in a function body. Allows
		 * the use of {@code return} statements.
		 * 
		 * @return self
		 */
		public Context pushFunction() {
			this.data = new ContextData(this.data, false);
			this.data.setFlags(Context.FLAG_RETURN | Context.FLAG_FUNCTION);
			return this;
		}

		/**
		 * Marks this level of the context as being in a switch statement.
		 * Allows the use of {@code break}.
		 * 
		 * @return self
		 */
		public Context enterSwitch() {
			data.setFlags(Context.FLAG_BREAK | Context.FLAG_SWITCH);
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
			data.setFlags(Context.FLAG_RETURN | Context.FLAG_YIELD | Context.FLAG_GENERATOR);
			return this;
		}
		
		public Context enterDeclare() {
			data.setFlags(Context.FLAG_AMBIENT);
			return this;
		}

		/**
		 * Marks this level of the context as being in a loop. Allows
		 * {@code break} and {@code continue} statements.
		 * 
		 * @return self
		 */
		public Context enterLoop() {
			data.setFlags(Context.FLAG_CONTINUE | Context.FLAG_BREAK);
			return this;
		}

		public Context enterStrict() {
			data.setFlags(Context.FLAG_STRICT);
			return this;
		}
		
		public Context setDirectiveTarget(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_DIRECTIVE_TARGET);
			else
				data.clearFlags(Context.FLAG_DIRECTIVE_TARGET);
			return this;
		}

		public Context isBindingElement(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_BINDING_ELEMENT);
			else
				data.clearFlags(Context.FLAG_BINDING_ELEMENT);
			return this;
		}

		public Context isAssignmentTarget(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_ASSIGNMENT_TARGET);
			else
				data.clearFlags(Context.FLAG_ASSIGNMENT_TARGET);
			return this;
		}
		
		public Context isMaybeParam(boolean value) {
			if (value)
				data.setFlags(Context.FLAG_MAYBE_PARAMETER);
			else
				data.clearFlags(Context.FLAG_MAYBE_PARAMETER);
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
			int flags = Context.FLAG_IN;
			int rsMask = 0;
			int rcMask = 0;
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

			public ContextData(ContextData parent, int rsMask, int rcMask) {
				this.parent = parent;
				this.inheritFrom(parent);
				this.rsMask = rsMask & ~parent.flags;
				this.rcMask = rcMask & parent.flags;
			}
			
			protected void setFlags(int flags) {
				this.flags |= flags;
				int raised = flags & rsMask;
				if (raised != 0)
					this.parent.setFlags(raised);
			}
			
			protected void clearFlags(int flags) {
				this.flags &= ~flags;
				int raised = flags & rcMask;
				if (raised != 0)
					this.parent.clearFlags(raised);
			}
			
			protected boolean hasFlags(int flags) {
				return (this.flags & flags) == flags;
			}
			
			private void inheritFrom(ContextData source) {
				this.flags = source.flags;
			}
		}
	}
}