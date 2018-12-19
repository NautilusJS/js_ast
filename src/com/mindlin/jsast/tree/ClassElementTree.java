package com.mindlin.jsast.tree;

/**
 * Marker interface for valid elements of a {@link ClassTreeBase class declaration/expression}.
 * 
 * @author mailmindlin
 */
public interface ClassElementTree extends DeclarationTree {
	<R, D> R accept(ClassElementVisitor<R, D> visitor, D data);
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((ClassElementVisitor<R, D>) visitor, data);
	}
}
