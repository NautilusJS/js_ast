package com.mindlin.jsast.type;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.property.AccessModifier;

public class ParameterInfo {
	protected AccessModifier access;
	protected PatternTree identifier;
	protected boolean rest;
	protected boolean optional;
	protected Type declaredType;
	protected ExpressionTree initializer;
	
	public ParameterInfo(AccessModifier access, PatternTree identifier, boolean rest, boolean optional, Type declaredType, ExpressionTree initializer) {
		this.access = access;
		this.identifier = identifier;
		this.rest = rest;
		this.optional = optional;
		this.declaredType = declaredType;
		this.initializer = initializer;
	}
	
	public AccessModifier getAccessModifier() {
		return this.access;
	}
	
	public boolean isRest() {
		return this.rest;
	}
	
	public boolean isOptional() {
		return this.optional;
	}
	
	public Type getDeclaredType() {
		return this.declaredType;
	}
	
	public ExpressionTree getInitializer() {
		return this.initializer;
	}
	
	public PatternTree getIdentifier() {
		return this.identifier;
	}
}
