package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.GenericTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface FunctionExpressionTree extends ExpressionTree {
	/**
	 * Get function identifier.
	 * Null if arrow function (see {@link #isArrow()})
	 * @return name
	 */
	IdentifierTree getName();
	
	/**
	 * Get generic parameters.
	 * @return generics (may return null if empty)
	 */
	List<GenericTypeTree> getGenericParameters();

	/**
	 * Get function parameters.
	 * @return parameters
	 */
	List<ParameterTree> getParameters();
	
	/**
	 * Get function return type.
	 * <p>
	 * Note: the type provided by this method is <strong>wrong</strong> if this function is:
	 * <ul>
	 * <li>a generator (in which case, the *actual* return type is
	 * <code>Generator&lt;getReturnType()&gt;</code>); see {@link #isGenerator()}.</li>
	 * <li>async (in which case, the *actual* return type is <code>Promise&lt;getReturnType()&gt;</code>);
	 * see {@link #isAsync()}.</li>
	 * </ul>
	 * </p>
	 * @return declared return type
	 */
	TypeTree getReturnType();
	
	/**
	 * Get function body.
	 * @return function body
	 */
	StatementTree getBody();

	/**
	 * Return if this function is marked as strict.
	 * @return if strict
	 */
	boolean isStrict();
	
	/**
	 * Return if this is an arrow function.
	 * @return if arrow function
	 */
	boolean isArrow();
	
	/**
	 * Get if this is a generator. Mutually exclusive with {@link #isArrow()}.
	 * <p>
	 * Note that if this method returns true, {@link #getReturnType()} will be
	 * <strong>wrong</strong> (see {@link #getReturnType()}).
	 * </p>
	 * 
	 * @return if generator
	 */
	boolean isGenerator();
	
	/**
	 * Get if this is an async function.
	 * <p>
	 * Note that if this method returns true, {@link #getReturnType()} will be
	 * <strong>wrong</strong> (see {@link #getReturnType()}).
	 * </p>
	 * @return if async
	 */
	boolean isAsync();
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || this.getKind() != other.getKind() || !(other instanceof FunctionExpressionTree) || this.hashCode() != other.hashCode())
			return false;
		
		FunctionExpressionTree o = (FunctionExpressionTree) other;
		
		return this.isStrict() == o.isStrict()
				&& this.isArrow() == o.isArrow()
				&& this.isAsync() == o.isAsync()
				&& Tree.equivalentTo(this.getName(), o.getName())
				&& Tree.equivalentTo(this.getReturnType(), o.getReturnType())
				&& Tree.equivalentTo(this.getParameters(), o.getParameters())
				&& Tree.equivalentTo(this.getBody(), o.getBody());
	}
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_EXPRESSION;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionExpression(this, data);
	}
}
