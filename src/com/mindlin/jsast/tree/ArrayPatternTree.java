package com.mindlin.jsast.tree;

import java.util.List;

/**
 * A tree for ES6 array destructuring operators.
 * Example:
 * <code>
 * const array = [1, 2, 3];
 * const [i, j, k] = array;
 * //i = 1, j = 2, k = 3
 * </code>
 * @author mailmindlin
 */
public interface ArrayPatternTree extends PatternTree {
	/**
	 * Get the elements of the array destructuring in order
	 * @return elements
	 */
	List<PatternTree> getElements();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_PATTERN;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitArrayPattern(this, data);
	}
}
