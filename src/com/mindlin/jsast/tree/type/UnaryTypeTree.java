package com.mindlin.jsast.tree.type;

/**
 * Type representing a type expression in the form of {@code keyof X} or {@code unique X}
 * @author mailmindlin
 */
public interface UnaryTypeTree extends TypeTree {

	/**
	 * Get the type that this is a key of ({@code X} in {@code keyof X}).
	 * @return base type
	 */
	TypeTree getBaseType();
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitUnaryType(this, data);
	}
}
