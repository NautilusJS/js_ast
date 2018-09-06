package com.mindlin.jsast.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.Modifiers.AccessModifier;

/**
 * Repre
 * @author mailmindlin
 *
 * @param <T>
 */
public interface ClassPropertyTree<T extends ExpressionTree> extends TypedPropertyTree {
	/**
	 * Get the value of the property
	 * @return
	 */
	T getValue();

	/**
	 * Whether the property is static or not
	 * @return
	 */
	@Deprecated
	default boolean isStatic() {
		return this.getModifiers().isStatic();
	}
	
	/**
	 * Get the access modifier on this property. Not null.
	 * @return
	 */
	@Deprecated
	default AccessModifier getAccess() {
		return this.getModifiers().getAccess();
	}
	
	/**
	 * Get the type of this property. Not null.
	 * @return
	 */
	PropertyDeclarationType getDeclarationType();
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || !(other instanceof ClassPropertyTree) || this.hashCode() != other.hashCode())
			return false;
		
		
		ClassPropertyTree<?> o = (ClassPropertyTree<?>) other;
		
		return this.getKind() == o.getKind()
				&& Objects.equals(this.getModifiers(), o.getModifiers())
				&& this.getDeclarationType() == o.getDeclarationType()
				&& Tree.equivalentTo(this.getKey(), o.getKey())
				&& Tree.equivalentTo(this.getType(), o.getType())
				&& Tree.equivalentTo(this.getValue(), o.getValue());
	}
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CLASS_PROPERTY;
	}

	public static enum PropertyDeclarationType {
		GETTER,
		SETTER,
		METHOD,
		ASYNC_METHOD,
		GENERATOR,
		CONSTRUCTOR,
		FIELD;
	}
}
