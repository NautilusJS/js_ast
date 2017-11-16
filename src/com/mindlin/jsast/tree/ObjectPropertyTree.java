package com.mindlin.jsast.tree;

/**
 * A property on an object literal or something.
 * <p>
 * Honestly, the inheritance is kinda messed up for the properties of object
 * literals, interfaces, classes, etc., but I don't see any better way.
 * </p>
 * 
 * @author mailmindlin
 *
 */
public interface ObjectPropertyTree extends Tree {
	/**
	 * Get the key to this property. Note that this isn't always an identifier.
	 * @return key
	 */
	ObjectPropertyKeyTree getKey();
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
