package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.ClassElementTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.UnvisitableTree;

/**
 * Index signature: properties in form of {@code [key: T]: R}.
 * 
 * @author mailmindlin
 *
 */
public interface IndexSignatureTree extends ClassElementTree, TypeElementTree, UnvisitableTree {
	
	Modifiers getModifiers();
	
	/**
	 * Get type being used to index.
	 */
	TypeParameterDeclarationTree getIndexType();
	
	/**
	 * Get type returned from index
	 */
	TypeTree getReturnType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INDEX_SIGNATURE;
	}
}
