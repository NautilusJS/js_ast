package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.ClassElementTree;
import com.mindlin.jsast.tree.ClassElementVisitor;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeElementVisitor;

/**
 * Index signature: properties in form of {@code [key: T]: R}.
 * 
 * @author mailmindlin
 *
 */
public interface IndexSignatureTree extends ClassElementTree, TypeElementTree {
	
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
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitIndexSignature(this, data);
	}
	
	@Override
	default <R, D> R accept(ClassElementVisitor<R, D> visitor, D data) {
		return visitor.visitIndexSignature(this, data);
	}
	
	@Override
	default <R, D> R accept(TypeElementVisitor<R, D> visitor, D data) {
		return visitor.visitIndexSignature(this, data);
	}
}
