package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

/**
 * 
 * @author Liam
 *
 */
public interface TypedPropertyTree extends ObjectPropertyTree {
	/**
	 * If this property has the <code>readonly</code> modifier on it.
	 * @return readonly presence
	 */
	boolean isReadonly();
	
	/**
	 * Get the declared type of this property.
	 * @return declared type, else null if not available
	 */
	TypeTree getType();
}
