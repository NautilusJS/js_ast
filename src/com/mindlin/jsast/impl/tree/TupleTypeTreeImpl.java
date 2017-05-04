package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

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
