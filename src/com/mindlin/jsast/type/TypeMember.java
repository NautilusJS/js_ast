package com.mindlin.jsast.type;

import com.mindlin.jsast.tree.ClassPropertyTree.AccessModifier;

public class TypeMember {
	AccessModifier access;
	boolean required = true;
	boolean readonly = false;
	Type key;
	Type value;
	
	public TypeMember(boolean required, boolean readonly, Type key, Type value) {
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
	
	public Type getName() {
		return this.key;
	}
	
	public Type getType() {
		return this.value;
	}
	
	public AccessModifier getAccess() {
		return this.access;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append('{');
		
		if (this.isReadonly())
			sb.append("readonly ");
		
		sb.append(this.getName());
		
		if (!this.isRequired())
			sb.append('?');
		
		if (this.getType() != null)
			sb.append(": ").append(this.getType());
		
		sb.append('}');
		return sb.toString();
	}
}
