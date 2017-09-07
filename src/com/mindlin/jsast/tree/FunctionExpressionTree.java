package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.TypeTree;

public interface FunctionExpressionTree extends ExpressionTree {
	StatementTree getBody();

	IdentifierTree getName();

	List<ParameterTree> getParameters();
	
	TypeTree getReturnType();

	/**
	 * Return if this function is marked as strict
	 * @return if strict
	 */
	boolean isStrict();
	
	/**
	 * Return if this is an arrow function
	 * @return if arrow function
	 */
	boolean isArrow();
	
	boolean isGenerator();
	
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
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionExpression(this, data);
	}
}
