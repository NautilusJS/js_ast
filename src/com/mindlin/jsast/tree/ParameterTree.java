package com.mindlin.jsast.tree;

/**
 * Represents a parameter declaration (this is used when defining a function, not when
 * calling one). Supports most of ES6/TypeScript functionality, including being a rest parameter,
 * type annotations, default values, and optional qualities.
 * 
 * TODO Does not yet support destructuring.
 * @author mailmindlin
 */
public interface ParameterTree extends Tree {
	boolean isRest();

	boolean isOptional();
	
	TypeTree getType();
	
	ExpressionTree getInitializer();
	
	IdentifierTree getIdentifier();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PARAMETER;
	}
}