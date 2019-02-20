package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class TupleTypeTreeImpl extends AbstractTypeTree implements TupleTypeTree {
	protected final List<TypeTree> slots;
	
	public TupleTypeTreeImpl(SourcePosition start, SourcePosition end, List<TypeTree> slots) {
		super(start, end);
		this.slots = slots;
	}
	
	@Override
	public List<TypeTree> getSlotTypes() {
		return this.slots;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getSlotTypes());
	}
	
}
