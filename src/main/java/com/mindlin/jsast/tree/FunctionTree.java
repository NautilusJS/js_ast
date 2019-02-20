package com.mindlin.jsast.tree;

import java.util.Objects;

/**
 * Contains shared methods on {@link FunctionDeclarationTree} and {@link FunctionExpressionTree}
 * @author mailmindlin
 */
public interface FunctionTree extends SignatureDeclarationTree {
	/**
	 * @return modifiers (not null)
	 */
	Modifiers getModifiers();
	/**
	 * Get function identifier.
	 * Null if expression
	 * @return name
	 */
	@Override
	PropertyName getName();
	
	/**
	 * Get function body.
	 * @return function body
	 */
	StatementTree getBody();
	
	/**
	 * Return if this is an arrow function.
	 * @return if arrow function
	 */
	boolean isArrow();
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || this.getKind() != other.getKind() || !(other instanceof FunctionTree) || this.hashCode() != other.hashCode())
			return false;
		
		FunctionTree o = (FunctionTree) other;
		
		return this.isArrow() == o.isArrow()
				&& Objects.equals(this.getModifiers(), o.getModifiers())
				&& Tree.equivalentTo(this.getName(), o.getName())
				&& Tree.equivalentTo(this.getReturnType(), o.getReturnType())
				&& Tree.equivalentTo(this.getParameters(), o.getParameters())
				&& Tree.equivalentTo(this.getBody(), o.getBody());
	}
}
