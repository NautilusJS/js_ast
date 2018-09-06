package com.mindlin.jsast.type;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.Modifiers.AccessModifier;

public class TypeMember {
	Modifiers modifiers;
	boolean required = true;
	Type key;
	Type value;
	
	public TypeMember(boolean required, boolean readonly, Type key, Type value) {
		this(required, readonly ? Modifiers.READONLY : Modifiers.NONE, key, value);
	}
	
	public TypeMember(boolean required, Modifiers modifiers, Type key, Type value) {
		this.required = required;
		this.modifiers = modifiers;
		this.key = key;
		this.value = value;
	}
	
	public boolean isRequired() {
		return this.required;
	}
	
	public boolean isReadonly() {
		return this.modifiers.isReadonly();
	}
	
	public Type getName() {
		return this.key;
	}
	
	public Type getType() {
		return this.value;
	}
	
	public AccessModifier getAccess() {
		return this.modifiers.getAccess();
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
