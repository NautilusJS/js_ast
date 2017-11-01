package com.mindlin.jsast.tree.type;

import java.util.Optional;

import com.mindlin.jsast.tree.IdentifierTree;

/**
 * Reference to {@link GenericTypeTree}.
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
	Optional<GenericTypeTree> getReferenced();
}
