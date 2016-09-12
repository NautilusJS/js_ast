package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.UnaryTree.VoidTree;

//see http://download.java.net/java/jdk9/docs/jdk/api/nashorn/jdk/nashorn/api/tree/package-summary.html
//http://docs.oracle.com/javase/8/docs/jdk/api/javac/tree/index.html
//https://github.com/javaparser/javaparser
public interface Tree {
	public static enum Kind {
		// Data structures
		COMPILATION_UNIT(CompilationUnitTree.class),
		FUNCTION,
		FUNCTION_EXPRESSION(FunctionExpressionTree.class),

		// Loops
		FOR_IN_LOOP(ForEachLoopTree.class),
		FOR_OF_LOOP(ForEachLoopTree.class), // Special
		FOR_LOOP(ForLoopTree.class),
		DO_WHILE_LOOP(DoWhileLoopTree.class),
		WHILE_LOOP(WhileLoopTree.class),

		// Unary operators
		POSTFIX_DECREMENT,
		POSTFIX_INCREMENT,
		PREFIX_DECREMENT,
		PREFIX_INCREMENT,
		UNARY_PLUS,
		UNARY_MINUS,
		DELETE,

		// Binary operators
		ADDITION,
		SUBTRACTION,
		MULTIPLICATION,
		DIVISION,
		REMAINDER,
		EXPONENTIATION,
		LEFT_SHIFT,
		RIGHT_SHIFT,
		UNSIGNED_RIGHT_SHIFT,

		// Bitwise operators
		BITWISE_AND,
		BITWISE_OR,
		BITWISE_XOR,
		BITWISE_NOT,

		// Assignment operators
		ASSIGNMENT(AssignmentTree.class),
		ADDITION_ASSIGNMENT,
		SUBTRACTION_ASSIGNMENT,
		MULTIPLICATION_ASSIGNMENT,
		DIVISION_ASSIGNMENT,
		REMAINDER_ASSIGNMENT,
		EXPONENTIATION_ASSIGNMENT,
		LEFT_SHIFT_ASSIGNMENT,
		RIGHT_SHIFT_ASSIGNMENT,
		UNSIGNED_RIGHT_SHIFT_ASSIGNMENT,

		// Assignment bitwise operators
		BITWISE_AND_ASSIGNMENT,
		BITWISE_OR_ASSIGNMENT,
		BITWISE_XOR_ASSIGNMENT,

		// Comparison operators
		EQUAL,
		NOT_EQUAL,
		STRICT_EQUAL,
		STRICT_NOT_EQUAL,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_EQUAL,
		LESS_THAN_EQUAL,

		// Logical operators
		LOGICAL_AND,
		LOGICAL_OR,
		LOGICAL_NOT,

		// Literals
		BOOLEAN_LITERAL(BooleanLiteralTree.class),
		ARRAY_LITERAL(ArrayLiteralTree.class),
		NULL_LITERAL(NullLiteralTree.class),
		NUMERIC_LITERAL(NumericLiteralTree.class),
		OBJECT_LITERAL(ObjectLiteralTree.class),
		REGEXP_LITERAL,
		STRING_LITERAL(StringLiteralTree.class),
		TEMPLATE_LITERAL,

		// Misc. operators
		SEQUENCE(SequenceTree.class),
		PARENTHESIZED(ParenthesizedTree.class),

		// Member access
		ARRAY_ACCESS(ArrayAccessTree.class),
		MEMBER_SELECT(MemberSelectTree.class),

		// Control structures
		BLOCK(BlockTree.class),
		CATCH(CatchTree.class),
		EMPTY_STATEMENT(EmptyStatementTree.class),
		SWITCH(SwitchTree.class),
		TRY(TryTree.class),

		// Control flow modifiers
		CASE(CaseTree.class),
		RETURN(ReturnTree.class),
		THROW(ThrowTree.class),
		IF(IfTree.class),
		WITH(WithTree.class),
		CONDITIONAL(ConditionalExpressionTree.class),

		// GOTO stuff
		LABELED_STATEMENT,
		CONTINUE(ContinueTree.class),
		BREAK(BreakTree.class),
		DEBUGGER(DebuggerTree.class),

		// Module stuff
		IMPORT(ImportTree.class),
		EXPORT(ExportTree.class),

		// Class stuff
		CLASS_EXPRESSION,
		CLASS_DECLARATION,
		INTERFACE_DECLARATION, // Support some typescript
		PROPERTY(PropertyTree.class),

		// Type stuff
		ENUM,
		UNION,
		GENERIC_PARAM,

		// Array stuff
		IN,
		OF,
		SPREAD(SpreadTree.class),

		// Method invocation
		NEW(NewTree.class),
		FUNCTION_INVOCATION(FunctionCallTree.class),
		PARAMETER(ParameterTree.class),

		// Variable stuff
		VARIABLE(VariableTree.class),
		SCOPED_FUNCTION,//Lambda
		IDENTIFIER(IdentifierTree.class),
		THIS_EXPRESSION(ThisExpressionTree.class),
		SUPER_EXPRESSION(SuperExpressionTree.class),

		// Prototype stuff
		TYPEOF,
		INSTANCE_OF(InstanceOfTree.class),

		ERROR(ErroneousTree.class),
		EXPRESSION_STATEMENT(ExpressionStatementTree.class),
		OTHER,
		VOID(VoidTree.class),

		// Comments
		COMMENT(CommentNode.class);
		private final Class<? extends Tree> iface;
		private final boolean expr, litr, stmt;

		Kind() {
			this(Tree.class);// TODO fix
		}

		Kind(Class<? extends Tree> clazz) {
			this.iface = clazz;
			this.litr = LiteralTree.class.isAssignableFrom(clazz);
			this.expr = litr || ExpressionTree.class.isAssignableFrom(clazz);//literals are expressions
			this.stmt = StatementTree.class.isAssignableFrom(clazz);
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

		@Override
		public String toString() {
			return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
		}
	}

	Kind getKind();

	long getStart();

	long getEnd();

	<R, D> R accept(TreeVisitor<R, D> visitor, D data);
}