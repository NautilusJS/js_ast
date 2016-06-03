package com.mindlin.jsast.tree;
//see http://download.java.net/java/jdk9/docs/jdk/api/nashorn/jdk/nashorn/api/tree/package-summary.html
//http://docs.oracle.com/javase/8/docs/jdk/api/javac/tree/index.html
//https://github.com/javaparser/javaparser
public interface Tree {
	public static enum Kind {
		//Data structures
		COMPILATION_UNIT(CompilationUnitTree.class),
		FUNCTION,
		FUNCTION_EXPRESSION(FunctionExpressionTree.class),
		
		//Loops
		FOR_IN_LOOP(ForEachLoopTree.class),
		FOR_OF_LOOP(ForEachLoopTree.class),//Special
		FOR_LOOP(ForLoopTree.class),
		DO_WHILE_LOOP(DoWhileLoopTree.class),
		WHILE_LOOP(WhileLoopTree.class),
		
		//Unary operators
		POSTFIX_DECREMENT,
		POSTFIX_INCREMENT,
		PREFIX_DECREMENT,
		PREFIX_INCREMENT,
		SPREAD_OPERATOR,
		UNARY_PLUS,
		UNARY_MINUS,
		DELETE,
		
		//Binary operators
		ADDITION,
		SUBTRACTION,
		MULTIPLICATION,
		DIVISION,
		REMAINDER,
		EXPONENTIATION,
		LEFT_SHIFT,
		RIGHT_SHIFT,
		UNSIGNED_RIGHT_SHIFT,
		
		//Bitwise operators
		BITWISE_AND,
		BITWISE_OR,
		BITWISE_XOR,
		BITWISE_NOT,
		
		//Assignment operators
		ASSIGNMENT,
		ADDITION_ASSIGNMENT,
		SUBTRACTION_ASSIGNMENT,
		MULTIPLICATION_ASSIGNMENT,
		DIVISION_ASSIGNMENT,
		REMAINDER_ASSIGNMENT,
		EXPONENTIATION_ASSIGNMENT,
		LEFT_SHIFT_ASSIGNMENT,
		RIGHT_SHIFT_ASSIGNMENT,
		UNSIGNED_RIGHT_SHIFT_ASSIGNMENT,
		
		//Assignment bitwise operators
		BITWISE_AND_ASSIGNMENT,
		BITWISE_OR_ASSIGNMENT,
		BITWISE_XOR_ASSIGNMENT,
		
		//Comparison operators
		EQUAL,
		NOT_EQUAL,
		STRICT_EQUAL,
		STRICT_NOT_EQUAL,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_EQUAL,
		LESS_THAN_EQUAL,
		
		//Logical operators
		LOGICAL_AND,
		LOGICAL_OR,
		LOGICAL_NOT,
		
		//Literals
		BOOLEAN_LITERAL,
		ARRAY_LITERAL,
		NULL_LITERAL,
		NUMBER_LITERAL,
		OBJECT_LITERAL,
		REGEXP_LITERAL,
		STRING_LITERAL,
		STRING_INTERPOLATED_LITERAL,
		
		//Misc. operators
		COMMA,
		PARENTHESIZED,
		
		//Member access
		ARRAY_ACCESS,
		MEMBER_SELECT,
		
		//Control structures
		BLOCK,
		CATCH,
		EMPTY_STATEMENT,
		SWITCH,
		TRY,
		FINALLY,
		
		//Control flow modifiers
		CASE,
		RETURN,
		THROW,
		IF,
		WITH,
		
		//GOTO stuff
		LABELED_STATEMENT,
		CONTINUE,
		BREAK,
		DEBUGGER,
		
		//Module stuff
		IMPORT,
		EXPORT,
		
		//Class stuff
		CLASS_EXPRESSION,
		CLASS_DECLARATION,
		INTERFACE_DECLARATION,//Support some typescript
		
		//Array stuff
		IN,
		OF,
		SPREAD,
		
		//Method invocation
		NEW,
		FUNCTION_INVOCATION,
		PARAMETER,
		
		//Variable stuff
		VARIABLE,
		SCOPED_FUNCTION,
		IDENTIFIER,
		
		//Prototype stuff
		TYPEOF,
		INSTANCE_OF,
		
		ERROR,
		EXPRESSION_STATEMENT,
		OTHER,
		PROPERTY,
		VOID,
		;
		private final Class<? extends Tree> iface;
		private final boolean expr, litr, stmt;
		Kind() {
			this(Tree.class);//TODO fix
		}
		Kind(Class<? extends Tree> clazz) {
			this.iface = clazz;
			this.expr = ExpressionTree.class.isAssignableFrom(clazz);
			this.litr = LiteralTree.class.isAssignableFrom(clazz);
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
			return this.name().substring(0,1) + this.name().substring(1).toLowerCase();
		}
	}
	
	Kind getKind();
	long getStart();
	long getEnd();
	<R,D> R accept(TreeVisitor<R, D> visitor, D data);
}