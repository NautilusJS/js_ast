package com.mindlin.jsast.tree.type;

import java.util.Optional;

import com.mindlin.jsast.tree.IdentifierTree;

/**
 * Reference to {@link GenericParameterTree}.
 * @author mailmindlin
 */
public interface GenericRefTypeTree extends TypeTree {
	/**
	 * Get name of ref
	 * @return
	 */
	IdentifierTree getName();
	
	/**
	 * Get GenericTypeTree referenced.
	 * @return referenced value
	 */
	Optional<GenericParameterTree> getReferenced();
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitGenericRefType(this, data);
	}
}
