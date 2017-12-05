package com.mindlin.jsast.type;

public class TypeMember {
	boolean required = true;
	boolean readonly = false;
	String key;
	Object value;
	
	public TypeMember(boolean required, boolean readonly, String key, Type value) {
		this.required = required;
		this.readonly = readonly;
		this.key = key;
		this.value = value;
	}
	
	public boolean isRequired() {
		return this.required;
	}
	
	public boolean isReadonly() {
		return this.readonly;
	}
	
	public String getName() {
		return this.key;
	}
	
	public Type getType() {
		return null;
	}
}
