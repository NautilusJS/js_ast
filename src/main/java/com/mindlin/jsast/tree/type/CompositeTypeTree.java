package com.mindlin.jsast.tree.type;

import java.util.List;

/**
 * Shared interface for both {@link Kind#TYPE_UNION} and {@link Kind#TYPE_INTERSECTION}.
 * @author mailmindlin
 */
public interface CompositeTypeTree extends TypeTree {
	/**
	 * Get a set of types that are either all the alternatives (if this is a type union tree) or
	 * all the boundaries (if this is a type intersection).
	 * <p>
	 * This method will traverse all children recursively. For example, the type expression
	 * <code> (A | B | (C & D)) => Union[A, Union[B, Intersection[C, D]]] => {A, B, Intersection[C, D]}</code>.
	 * </p>
	 * <p>
	 * Note that this method will not recurse through binary type trees that have different kinds
	 * than the current one (e.g., <code> (A | (B & (C | A))) => {A, B & (C | A)}</code>).
	 * </p>
	 * @return set of all types encompassed by this union/intersection
	 */
	/**
	 * Get a list of constituent types.
	 * Returned list should have size of >= 2
	 * @return constituent types
	 */
	List<TypeTree> getConstituents();
	
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		if (this.getKind() == Kind.TYPE_UNION)
			return visitor.visitUnionType(this, data);
		return visitor.visitIntersectionType(this, data);
	}
}
