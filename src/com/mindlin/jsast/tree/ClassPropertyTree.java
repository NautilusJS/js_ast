package com.mindlin.jsast.tree;

public interface ClassPropertyTree<T> extends TypedPropertyTree {
	T getValue();

	boolean isStatic();
	
	AccessModifier getAccess();

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
