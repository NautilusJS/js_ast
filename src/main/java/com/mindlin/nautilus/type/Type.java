package com.mindlin.jsast.type;

/**
 * Base class for types
 * @author mailmindlin
 *
 */
public interface Type {
	Kind getKind();
	
	public static enum Kind {
		INTRINSIC,
		
		//Literals
		STRING_LITERAL,
		NUMBER_LITERAL,
		BOOLEAN_LITERAL,
		
		//Unary types
		ARRAY,
		KEYOF,
		
		//Composite types
		INTERSECTION,
		UNION,
		
		//Complex types
		OBJECT,
		TUPLE,
		ENUM,
		
		/**
		 * Type variable.
		 * @see TypeVariable
		 */
		VARIABLE,
		/**
		 * Type reference.
		 * @see TypeReference
		 */
		REFERENCE,
		;
	}
}
