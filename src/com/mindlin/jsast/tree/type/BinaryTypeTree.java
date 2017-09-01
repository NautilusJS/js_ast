package com.mindlin.jsast.tree.type;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

/**
 * Shared interface for both {@link Kind#TYPE_UNION} and {@link Kind#TYPE_INTERSECTION}.
 * @author mailmindlin
 */
public interface BinaryTypeTree extends TypeTree {
	/**
	 * Get the left-hand type for this union/intersection
	 * @return left-hand-side type
	 */
	TypeTree getLeftType();
	
	/**
	 * Get the right-hand type for this union/intersection
	 * @return right-hand-side type
	 */
	TypeTree getRightType();
	
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
	default Set<TypeTree> values() {
		final Set<TypeTree> result = new HashSet<>();
		final Tree.Kind kind = this.getKind();
		
		/*
		 * We use a stack here instead of recursing, because Java doesn't support
		 * TCO (ATM), so 1) we don't get any nice optimizations, and 2) we could fill up the stack.
		 */
		
		final Stack<BinaryTypeTree> queue = new Stack<>();
		queue.push(this);
		
		while (!queue.isEmpty()) {
			BinaryTypeTree current = queue.pop();
			
			TypeTree left = current.getLeftType();
			if (left.getKind() == kind)
				queue.push((BinaryTypeTree) left);
			else
				result.add(left);
			
			TypeTree right = current.getRightType();
			if (right.getKind() == kind)
				queue.push((BinaryTypeTree) right);
			else
				result.add(right);
		}
		
		return result;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		if (this.getKind() == Kind.TYPE_UNION)
			return visitor.visitUnionType(this, data);
		return visitor.visitIntersectionType(this, data);
	}
}
