package com.mindlin.jsast.tree;

public interface ClassPropertyTree<T> extends TypedPropertyTree {
	/**
	 * Get the value of the property
	 * @return
	 */
	T getValue();

	/**
	 * Whether the property is static or not
	 * @return
	 */
	boolean isStatic();
	
	/**
	 * Get the access modifier on this property. Not null.
	 * @return
	 */
	AccessModifier getAccess();

	/**
	 * Get the type of this property. Not null.
	 * @return
	 */
	PropertyDeclarationType getDeclarationType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CLASS_PROPERTY;
	}

	public static enum PropertyDeclarationType {
		GETTER,
		SETTER,
		METHOD,
		CONSTRUCTOR,
		FIELD;
	}

	public static enum AccessModifier {
		PUBLIC,
		PROTECTED,
		PRIVATE,
	}
}
