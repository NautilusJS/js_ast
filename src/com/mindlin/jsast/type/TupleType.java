package com.mindlin.jsast.type;

import java.util.List;
import java.util.Objects;

public class TupleType implements Type {
	protected final List<Type> slots;
	
	public TupleType(List<Type> slots) {
		this.slots = slots;
	}
	
	public List<Type> getSlots() {
		return this.slots;
	}
	
	@Override
	public Kind getKind() {
		return Type.Kind.TUPLE;
	}
	
	@Override
	public int hashCode() {
		return slots.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || !(other instanceof TupleType))
			return false;
		
		return Objects.equals(this.getSlots(), ((TupleType) other).getSlots());
	}
	
}
