package com.mindlin.jsast.tree;

import java.util.Iterator;
import java.util.List;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree.CallSignatureTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree.ConstructSignatureTree;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;
import com.mindlin.jsast.tree.comment.CommentNode;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.ConstructorTypeTree;
import com.mindlin.jsast.tree.type.EnumDeclarationTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.InferTypeTree;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.LiteralTypeTree;
import com.mindlin.jsast.tree.type.MappedTypeTree;
import com.mindlin.jsast.tree.type.MemberTypeTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
import com.mindlin.jsast.tree.type.ParenthesizedTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeAliasTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.ConditionalTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.UnaryTypeTree;

//see http://download.java.net/java/jdk9/docs/jdk/api/nashorn/jdk/nashorn/api/tree/package-summary.html
//http://docs.oracle.com/javase/8/docs/jdk/api/javac/tree/index.html
//https://github.com/javaparser/javaparser
public interface Tree {
	public static enum Kind {
		// ===== Data structures =====
		// Program
		COMPILATION_BUNDLE,
		COMPILATION_UNIT(CompilationUnitTree.class),
		
		// Comments
		COMMENT(CommentNode.class),
		JSDOC_DIRECTIVE,
		JSDOC_ANNOTATION,
		
		// Misc.
		DECORATOR(DecoratorTree.class),
		PARAMETER(ParameterTree.class),
		ERROR(ErroneousTree.class),
		OTHER,//TODO: remove?

		// ===== Statements =====
		EXPRESSION_STATEMENT(ExpressionStatementTree.class),
		EMPTY_STATEMENT(EmptyStatementTree.class),
		DIRECTIVE(DirectiveTree.class),
		
		// Control flow structures
		BLOCK(BlockTree.class),
		DEBUGGER(DebuggerTree.class),
		WITH(WithTree.class),
		
		// Control flow statements
		LABELED_STATEMENT(LabeledStatementTree.class),
		BREAK(BreakTree.class),
		CONTINUE(ContinueTree.class),
		RETURN(ReturnTree.class),
		AWAIT(AwaitTree.class),
		YIELD(UnaryTree.class),//TODO: custom inheritance?
		YIELD_GENERATOR(UnaryTree.class),//TODO: merge with yield?
		
		// Choice
		IF(IfTree.class),
		SWITCH(SwitchTree.class),
		CASE(SwitchCaseTree.class),
		
		// Exceptions
		THROW(ThrowTree.class),
		TRY(TryTree.class),
		CATCH(CatchTree.class),
		
		// Loops
		WHILE_LOOP(WhileLoopTree.class),
		DO_WHILE_LOOP(DoWhileLoopTree.class),
		FOR_LOOP(ForLoopTree.class),
		FOR_IN_LOOP(ForEachLoopTree.class),
		FOR_OF_LOOP(ForEachLoopTree.class),
		
		// ===== Declarations =====
		FUNCTION_DECLARATION(FunctionDeclarationTree.class),
		VARIABLE_DECLARATION(VariableDeclarationTree.class),
		VARIABLE_DECLARATOR(VariableDeclaratorTree.class),
		CLASS_DECLARATION(ClassDeclarationTree.class),
		
		// TS declarations
		ENUM_DECLARATION(EnumDeclarationTree.class),
		MODULE_DECLARATION,
		NAMESPACE_DECLARATION,
		TYPE_ALIAS(TypeAliasTree.class),
		INTERFACE_DECLARATION(InterfaceDeclarationTree.class),
		
		// ===== Expressions =====
		
		// Prototype-based
		THIS_EXPRESSION(ThisExpressionTree.class),
		SUPER_EXPRESSION(SuperExpressionTree.class),
		
		//TODO: categorize
		FUNCTION_EXPRESSION(FunctionExpressionTree.class),
		CLASS_EXPRESSION(ClassExpressionTree.class),

		// Literals
		BOOLEAN_LITERAL(BooleanLiteralTree.class),
		NULL_LITERAL(NullLiteralTree.class),
		NUMERIC_LITERAL(NumericLiteralTree.class),
		REGEXP_LITERAL(RegExpLiteralTree.class),
		STRING_LITERAL(StringLiteralTree.class),
		
		// Literal-like (but more expression-y)
		ARRAY_LITERAL(ArrayLiteralTree.class),
		OBJECT_LITERAL(ObjectLiteralTree.class),
		OBJECT_LITERAL_PROPERTY(ObjectLiteralElement.class),
		
		// Templates
		TAGGED_TEMPLATE,//TODO: fix
		TEMPLATE_LITERAL(TemplateLiteralTree.class),
		TEMPLATE_ELEMENT(TemplateElementTree.class),
		
		// Unary operators
		UNARY_PLUS(UnaryTree.class),
		UNARY_MINUS(UnaryTree.class),
		UNARY_NONNULL(UnaryTree.class),
		TYPEOF(UnaryTree.class),
		VOID(UnaryTree.class),
		DELETE(UnaryTree.class),
		
		// Update expressions
		POSTFIX_DECREMENT(UnaryTree.class),
		POSTFIX_INCREMENT(UnaryTree.class),
		PREFIX_DECREMENT(UnaryTree.class),
		PREFIX_INCREMENT(UnaryTree.class),

		// Binary operators
		IN(BinaryExpressionTree.class),
		INSTANCEOF(BinaryExpressionTree.class),
		
		// Binary math
		ADDITION(BinaryExpressionTree.class),
		SUBTRACTION(BinaryExpressionTree.class),
		MULTIPLICATION(BinaryExpressionTree.class),
		DIVISION(BinaryExpressionTree.class),
		REMAINDER(BinaryExpressionTree.class),
		EXPONENTIATION(BinaryExpressionTree.class),

		// Bitwise operators
		BITWISE_AND(BinaryExpressionTree.class),
		BITWISE_OR(BinaryExpressionTree.class),
		BITWISE_XOR(BinaryExpressionTree.class),
		BITWISE_NOT(UnaryTree.class),
		LEFT_SHIFT(BinaryExpressionTree.class),
		RIGHT_SHIFT(BinaryExpressionTree.class),
		UNSIGNED_RIGHT_SHIFT(BinaryExpressionTree.class),

		// Logical operators
		LOGICAL_AND(BinaryExpressionTree.class),
		LOGICAL_OR(BinaryExpressionTree.class),
		LOGICAL_NOT(UnaryTree.class),

		// Comparison operators
		EQUAL(BinaryExpressionTree.class),
		NOT_EQUAL(BinaryExpressionTree.class),
		STRICT_EQUAL(BinaryExpressionTree.class),
		STRICT_NOT_EQUAL(BinaryExpressionTree.class),
		GREATER_THAN(BinaryExpressionTree.class),
		LESS_THAN(BinaryExpressionTree.class),
		GREATER_THAN_EQUAL(BinaryExpressionTree.class),
		LESS_THAN_EQUAL(BinaryExpressionTree.class),

		// Assignment operators
		ASSIGNMENT(AssignmentTree.class),
		ADDITION_ASSIGNMENT(AssignmentTree.class),
		SUBTRACTION_ASSIGNMENT(AssignmentTree.class),
		MULTIPLICATION_ASSIGNMENT(AssignmentTree.class),
		DIVISION_ASSIGNMENT(AssignmentTree.class),
		REMAINDER_ASSIGNMENT(AssignmentTree.class),

		// Assignment bitwise operators
		BITWISE_AND_ASSIGNMENT(AssignmentTree.class),
		BITWISE_OR_ASSIGNMENT(AssignmentTree.class),
		BITWISE_XOR_ASSIGNMENT(AssignmentTree.class),
		EXPONENTIATION_ASSIGNMENT(AssignmentTree.class),
		LEFT_SHIFT_ASSIGNMENT(AssignmentTree.class),
		RIGHT_SHIFT_ASSIGNMENT(AssignmentTree.class),
		UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(AssignmentTree.class),

		// Misc. operators
		SEQUENCE(SequenceExpressionTree.class),
		PARENTHESIZED(ParenthesizedTree.class),

		// Member access
		ARRAY_ACCESS(BinaryExpressionTree.class),
		MEMBER_SELECT(MemberExpressionTree.class),

		// Control flow modifiers
		CONDITIONAL(ConditionalExpressionTree.class),

		// Module stuff
		IMPORT(ImportDeclarationTree.class),
		IMPORT_SPECIFIER(ImportSpecifierTree.class),
		EXPORT(ExportTree.class),

		// Array stuff
		SPREAD(SpreadElementTree.class),//TODO: make custom inheritance

		// Method invocation
		NEW(NewTree.class),
		FUNCTION_INVOCATION(FunctionCallTree.class),

		// Variable stuff
		IDENTIFIER(IdentifierTree.class),
		COMPUTED_PROPERTY_KEY(ComputedPropertyKeyTree.class),
		
		// ===== Properties =====
		// These are used for object literals, classes, interfaces, etc.
		
		// Signatures
		PROPERTY_SIGNATURE,
		METHOD_SIGNATURE(MethodSignatureTree.class),
		INDEX_SIGNATURE(IndexSignatureTree.class),
		CALL_SIGNATURE(CallSignatureTree.class),
		CONSTRUCT_SIGNATURE(ConstructSignatureTree.class),
		
		// Inheritance
		EXTENDS_CLAUSE(HeritageClauseTree.class),
		IMPLEMENTS_CLAUSE(HeritageClauseTree.class),
		HERITAGE_EXPRESSION(HeritageExpressionTree.class),
		
		// Declarations
		ASSIGNMENT_PROPERTY(AssignmentPropertyTree.class),
		SHORTHAND_ASSIGNMENT_PROPERTY(ShorthandAssignmentPropertyTree.class),
		PROPERTY_DECLARATION(PropertyDeclarationTree.class),
		METHOD_DECLARATION(MethodDeclarationTree.class),
		CONSTRUCTOR_DECLARATION(ConstructorDeclarationTree.class),
		GET_ACCESSOR_DECLARATION(GetAccessorDeclarationTree.class),
		SET_ACCESSOR_DECLARATION(SetAccessorDeclarationTree.class),
		
		// ===== Patterns =====
		OBJECT_PATTERN(ObjectPatternTree.class),
		ARRAY_PATTERN(ArrayPatternTree.class),
		ASSIGNMENT_PATTERN(AssignmentPatternTree.class),
		SHORTHAND_ASSIGNMENT_PATTERN(ShorthandAssignmentPatternTree.class),
		REST_PATTERN(RestPatternElementTree.class),
		
		// ===== Typing ======
		
		// Type-modified expressions
		CAST(CastExpressionTree.class),
		
		// Built-in types
		SPECIAL_TYPE(SpecialTypeTree.class),
		LITERAL_TYPE(LiteralTypeTree.class),
		
		// Generics
		TYPE_PARAMETER_DECLARATION(TypeParameterDeclarationTree.class),
		IDENTIFIER_TYPE(IdentifierTypeTree.class),
		
		// Type literals
		FUNCTION_TYPE(FunctionTypeTree.class),
		CONSTRUCTOR_TYPE(ConstructorTypeTree.class),
		TUPLE_TYPE(TupleTypeTree.class),
		OBJECT_TYPE(ObjectTypeTree.class),
		
		// Unary type expressions
		ARRAY_TYPE(ArrayTypeTree.class),
		KEYOF_TYPE(UnaryTypeTree.class),
		UNIQUE_TYPE(UnaryTypeTree.class),
		OPTIONAL_TYPE(UnaryTypeTree.class),
		DEFINITE_TYPE(UnaryTypeTree.class),
		PARENTHESIZED_TYPE(ParenthesizedTypeTree.class),
		INFER_TYPE(InferTypeTree.class),
		REST_TYPE,
		
		// Type expressions
		MEMBER_TYPE(MemberTypeTree.class),
		TYPE_UNION(CompositeTypeTree.class),
		TYPE_INTERSECTION(CompositeTypeTree.class),
		TYPE_PREDICATE,
		
		// Complex type expressions
		MAPPED_TYPE(MappedTypeTree.class),
		CONDITIONAL_TYPE(ConditionalTypeTree.class),
		TYPE_QUERY,
		;
		
		private final Class<? extends Tree> iface;
		private final boolean expr, litr, stmt, typ;

		Kind() {
			this(Tree.class);// TODO fix
		}

		Kind(Class<? extends Tree> clazz) {
			this.iface = clazz;
			this.litr = LiteralTree.class.isAssignableFrom(clazz);
			this.expr = litr || ExpressionTree.class.isAssignableFrom(clazz);//literals are expressions
			this.stmt = StatementTree.class.isAssignableFrom(clazz);
			this.typ = TypeTree.class.isAssignableFrom(clazz);
		}
		
		Kind(Class<? extends Tree> clazz, boolean litr, boolean expr, boolean stmt, boolean typ) {
			this.iface = clazz;
			this.litr = litr;
			this.expr = expr;
			this.stmt = stmt;
			this.typ = typ;
		}

		public Class<? extends Tree> asInterface() {
			return iface;
		}

		public boolean isExpression() {
			return this.expr;
		}

		public boolean isLiteral() {
			return this.litr;
		}

		public boolean isStatement() {
			return this.stmt;
		}
		
		public boolean isType() {
			return this.typ;
		}
	}
	
	public static boolean equivalentTo(Tree a, Tree b) {
		if (a == b)
			return true;
		
		if (a == null || b == null)
			return false;
		
		return a.equivalentTo(b);
	}
	
	public static <T extends Tree> boolean equivalentTo(List<? extends T> a, List<? extends T> b) {
		if (a == b)
			return true;
		if (a == null || b == null || a.size() != b.size())
			return false;
		
		for (Iterator<? extends T> i = a.iterator(), j = b.iterator(); i.hasNext();)
			if (!Tree.equivalentTo(i.next(), j.next()))
				return false;
		return true;
	}
	
	/**
	 * Get the kind of this tree. Useful for switch statements.
	 * @return the kind of this tree.
	 */
	Kind getKind();
	
	SourceRange getRange();

	/**
	 * Get the start position of this tree.
	 * @return the start position of this element, else -1 if not available
	 * @see #getEnd()
	 */
	default SourcePosition getStart() {
		return this.getRange().getStart();
	}

	/**
	 * Get the end position of this tree.
	 * 
	 * @return the end position of this element, else -1 if not available
	 * @see #getStart()
	 */
	default SourcePosition getEnd() {
		return this.getRange().getEnd();
	}

	<R, D> R accept(TreeVisitor<R, D> visitor, D data);
	
	/**
	 * Indicates whether another tree is 'equivalent to' this one.
	 * <p>
	 * This method is less strict than {@link #equals(Object)}, as it does not require all
	 * fields to be the same. Rather, it is to test a looser 'is pretty much the same' equivalence that ignores
	 * certain attributes.
	 * For example, the values of {@link #getStart()}, {@link #getEnd()}, and {@link #isMutable()} should
	 * be ignored.
	 * </p>
	 * <p>
	 * Implementations of this method should satisfy the general contract provided by {@link #equals(Object)}. Specifically,
	 * <ul>
	 * <li>For any {@code x} and {@code y}, {@code x.equals(y)} implies {@code x.equivalentTo(y)}.</li>
	 * <li>It is <i>reflexive</i>: For any non-null {@code x}, {@code x.equivalentTo(x) == true}.</li>
	 * <li>It is <i>symmetric</i>: For any non-null {@code x} and {@code b}, {@code a.equivalentTo(b) == b.equivalentTo(a)}</li>
	 * <li>It is <i>transitive</i>: For any non-null  {@code x}, {@code y}, and {@code z},
	 * 		if {@code x.equivalentTo(y)} returns {@code true} and {@code y.equivalentTo(z)} returns {@code true},
	 * 		then {@code x.equivalentTo(z)} should return {@code true}.</li>
	 * <li>It is <i>consistent</i>: For any non-null {@code x} and {@code y}, multiple calls of </li>
	 * <li>Like {@link #equals(Object)}, {@code a.equivalentTo(null)} should return {@code false}.</li>
	 * </p>
	 * @param other
	 * @return equivalency
	 */
	default boolean equivalentTo(Tree other) {
		return this == other;
	}
}