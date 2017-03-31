package com.mindlin.jsast.tree;

import java.util.List;

public interface FunctionExpressionTree extends ExpressionTree {
	StatementTree getBody();

	IdentifierTree getName();

	List<ParameterTree> getParameters();
	
	TypeTree getReturnType();

	/**
	 * Return if this function is marked as strict
	 * @return
	 */
	boolean isStrict();
	
	/**
	 * Return if this is an arrow function
	 * @return
	 */
	boolean isArrow();
	
	boolean isGenerator();
	
	boolean isAsync();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_EXPRESSION;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionExpression(this, data);
	}
}
