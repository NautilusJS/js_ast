package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;

/**
 * Index signature: properties in form of {@code [key: T]: R}.
 * 
 * @author mailmindlin
 *
 */
public interface IndexSignatureTree extends TypeTree {
	/**
	 * Get type being used to index. Should be either {@code string} or {@code number}.
	 * 
	 * @return
	 */
	TypeTree getIndexType();
	
	/**
	 * Get type returned from index
	 * 
	 * @return
	 */
	TypeTree getReturnType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INDEX_TYPE;
	}
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitIndexType(this, data);
	}
}
