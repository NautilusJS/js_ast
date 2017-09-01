package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.BinaryTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Class to implement union and intersection types.
 * 
 * @author mailmindlin
 */
public class BinaryTypeTreeImpl extends AbstractTypeTree implements BinaryTypeTree {
	protected final TypeTree left;
	protected final Kind kind;
	protected final TypeTree right;
	
	public BinaryTypeTreeImpl(long start, long end, boolean implicit, TypeTree left, Tree.Kind kind, TypeTree right) {
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
	protected int hash() {
		return Objects.hash(getKind(), getLeftType(), getRightType());
	}
}
