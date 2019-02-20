package com.mindlin.jsast.type;

import java.util.Objects;

/**
 * Information about an index into an object type.
 * @author mailmindlin
 *
 */
public class IndexInfo {
	protected final boolean readonly;
	protected final Type keyType;
	protected final Type valueType;
	
	public IndexInfo(boolean readonly, Type key, Type value) {
		this.readonly = readonly;
		this.keyType = key;
		this.valueType = value;
	}
	
	public boolean isReadonly() {
		return this.readonly;
	}
	
	public Type getKeyType() {
		return this.keyType;
	}
	
	public Type getValueType() {
		return this.valueType;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || !(other instanceof IndexInfo))
			return false;
		
		IndexInfo o = (IndexInfo) other;
		return this.isReadonly() == o.isReadonly()
				&& Objects.equals(this.getKeyType(), o.getKeyType())
				&& Objects.equals(this.getValueType(), o.getValueType()); 
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(readonly, keyType, valueType);
	}
}
