package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Represents a TypeScript cast operation. Cast operations are written in the
 * form of {@code <TYPE>EXPR} or {@code EXPR as TYPE} where
 * {@code EXPR} is the expression being cast, and {@code TYPE} is the
 * type it is being cast as.
 * 
 * @author mailmindlin
 */
public interface CastExpressionTree extends ExpressionTree {
	
	ExpressionTree getExpression();
	
	/**
	 * Get the type that the expression was cast to
	 * @return type casted to
	 */
	TypeTree getType();
	
	@Override
	default Kind getKind() {
		return Kind.CAST;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitCast(this, data);
	}
	
}
