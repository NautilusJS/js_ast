package com.mindlin.jsast.tree;

import java.util.Objects;

public interface LiteralTree<T> extends ExpressionTree {
	T getValue();
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		if (other == null || this.getKind() != other.getKind() || this.hashCode() != other.hashCode())
			return false;
		return Objects.equals(getValue(), ((LiteralTree<?>) other).getValue());
	}
}
