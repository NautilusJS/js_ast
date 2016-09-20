package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;

/**
 * Class to implement union and intersection types.
 * 
 * @author mailmindlin
 */
public class BinaryTypeTree extends AbstractTypeTree implements UnionTypeTree, IntersectionTypeTree {
	protected final TypeTree left;
	protected final Kind kind;
	protected final TypeTree right;
	
	public BinaryTypeTree(long start, long end, boolean implicit, TypeTree left, Tree.Kind kind, TypeTree right) {
		super(start, end, implicit);
		this.left = left;
		this.kind = kind;
		this.right = right;
	}
	
	@Override
	public TypeTree getLeftType() {
		return left;
	}
	
	@Override
	public TypeTree getRightType() {
		return right;
	}
	
	@Override
	public Kind getKind() {
		return kind;
	}
	
}
