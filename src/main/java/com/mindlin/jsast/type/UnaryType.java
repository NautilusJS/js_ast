package com.mindlin.jsast.type;

public class UnaryType implements Type {
	protected final Type.Kind kind;
	protected final Type base;
	
	public UnaryType(Type.Kind kind, Type base) {
		this.kind = kind;
		this.base = base;
	}
	
	public Type getBaseType() {
		return this.base;
	}

	@Override
	public Kind getKind() {
		return this.kind;
	}
}
