package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.TypeTree;

public interface FunctionCallTree extends ExpressionTree {
	ExpressionTree getCallee();
	
	List<? extends ExpressionTree> getArguments();
	
	List<TypeTree> getTypeArguments();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_INVOCATION;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionCall(this, data);
	}
}
