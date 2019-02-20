package com.mindlin.jsast.type;

public interface TypeVariable extends Type {
	/**
	 * Get constraint for variable
	 * @return constraint, or null if not present
	 */
	Type getConstraint();
	
	@Override
	default Type.Kind getKind() {
		return Type.Kind.VARIABLE;
	}
}
