package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;

/**
 * Type representing an array of something. For example, {@code Foo[]} or {@code Array<Foo>}.
 * Not that types in the form of {@code [Foo, Bar, Baz]} is not an array type, but a {@link TupleTypeTree}, however
 * empty arrays ({@code []}) <i>are</i> the same as {@code any[]}.
 * @author mailmindlin
 */
public interface ArrayTypeTree extends TypeTree {
	/**
	 * Get base type of array.
	 * @return
	 */
	TypeTree getBaseType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_TYPE;
	}
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitArrayType(this, data);
	}
}
