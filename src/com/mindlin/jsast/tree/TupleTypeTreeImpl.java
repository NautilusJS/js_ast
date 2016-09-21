package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.impl.tree.AbstractTypeTree;
import com.mindlin.jsast.tree.type.TupleTypeTree;

public class TupleTypeTreeImpl extends AbstractTypeTree implements TupleTypeTree {
	protected final List<TypeTree> slots;
	
	public TupleTypeTreeImpl(long start, long end, boolean implicit, List<TypeTree> slots) {
		super(start, end, implicit);
		this.slots = slots;
	}
	
	@Override
	public List<TypeTree> getSlotTypes() {
		return this.slots;
	}
	
}
