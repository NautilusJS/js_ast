package com.mindlin.jsast.harness;

public class CompilerOption<T> {
	private final String name;
	private final T defaultValue;
	
	CompilerOption(String name) {
		this(name, null);
	}
	
	CompilerOption(String name, T defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}
	
	public String name() {
		return this.name;
	}
	
	public T defaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
