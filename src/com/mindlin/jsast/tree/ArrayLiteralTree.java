package com.mindlin.jsast.tree;

import java.util.List;

/**
 * A tree representing an array literal
 * @author mailmindlin
 */
public interface ArrayLiteralTree extends ExpressionTree {
	/**
	 * Get list of elements in literal, in order
	 * @return elements
	 */
	List<? extends ExpressionTree> getElements();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_LITERAL;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitArrayLiteral(this, data);
	}
}
