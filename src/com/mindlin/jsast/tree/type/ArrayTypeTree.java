package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

/**
 * Type representing an array of something. For example, <code>Foo[]</code>.
 * Not that <code>[Foo, Bar, Baz]</code> is not an array type, but a {@link TupleTypeTree}.
 * @author mailmindlin
 */
public interface ArrayTypeTree extends TypeTree {
	TypeTree getBaseType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_TYPE;
	}
}
