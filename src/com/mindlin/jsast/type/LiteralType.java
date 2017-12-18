package com.mindlin.jsast.type;

import java.util.Objects;

public class LiteralType<T> implements Type {
	
	private static final LiteralType<Boolean> BOOLEAN_TRUE = new LiteralType<>(Type.Kind.BOOLEAN_LITERAL, true);
	private static final LiteralType<Boolean> BOOLEAN_FALSE = new LiteralType<>(Type.Kind.BOOLEAN_LITERAL, false);
	
	public static LiteralType<Boolean> of(boolean value) {
		return value ? BOOLEAN_TRUE : BOOLEAN_FALSE;
	}
	
	//TODO: cache LiteralType's
	public static LiteralType<Number> of(Number value) {
		return new LiteralType<>(Type.Kind.NUMBER_LITERAL, value);
	}
	
	public static LiteralType<String> of(String value) {
		return new LiteralType<>(Type.Kind.STRING_LITERAL, value);
	}
	
	protected Type.Kind kind;
	protected T value;
	
	public LiteralType(Type.Kind kind, T value) {
		Objects.nonNull(kind);
		Objects.nonNull(value);
		this.kind = kind;
		this.value = value;
	}
	
	public T getValue() {
		return this.value;
	}

	@Override
	public Kind getKind() {
		return this.kind;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode() ^ kind.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof LiteralType) || this.getKind() != ((Type) other).getKind())
			return false;
		
		//TODO: check this predicate
		if (this.getKind() == Kind.NUMBER_LITERAL && Double.isNaN((double) this.getValue()) && Double.isNaN((double) ((LiteralType<?>) other).getValue()))
			return true;
		
		return Objects.equals(this.getValue(), ((LiteralType<?>) other).getValue());
	}
	
	@Override
	public String toString() {
		return super.toString() + "{" + this.getValue() + "}";
	}
}
