package com.mindlin.jsast.impl.analysis;

import java.util.Objects;

import com.mindlin.jsast.type.Type;

public abstract class AbstractVariableInfo implements VariableInfo {
	protected final long id;
	protected final Type restrictionType;
	private final boolean assignable;
	protected Type currentType;
	
	public AbstractVariableInfo(long id, boolean assignable, Type restrictionType) {
		this.id = id;
		Objects.nonNull(restrictionType);
		this.restrictionType = restrictionType;
		this.assignable = assignable;
	}
	
	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public Type getRestrictionType() {
		return this.restrictionType;
	}
	
	@Override
	public Type getCurrentType() {
		return this.currentType == null ? this.restrictionType : this.currentType;
	}
	
	@Override
	public boolean isAssignable() {
		return this.assignable;
	}
}
