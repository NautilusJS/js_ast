package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;
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

	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		if (kind == Kind.TYPE_UNION)
			return UnionTypeTree.super.accept(visitor, data);
		else if (kind == Kind.TYPE_INTERSECTION)
			return IntersectionTypeTree.super.accept(visitor, data);
		throw new UnsupportedOperationException();
	}
}
