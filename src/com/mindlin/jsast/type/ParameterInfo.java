package com.mindlin.jsast.type;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.PatternTree;

public class ParameterInfo {
	protected Modifiers access;
	protected PatternTree identifier;
	protected boolean rest;
	protected boolean optional;
	protected Type declaredType;
	protected ExpressionTree initializer;
	
	public ParameterInfo(Modifiers access, PatternTree identifier, boolean rest, boolean optional, Type declaredType, ExpressionTree initializer) {
		this.access = access;
		this.identifier = identifier;
		this.rest = rest;
		this.optional = optional;
		this.declaredType = declaredType;
		this.initializer = initializer;
	}
	
	public Modifiers getModifiers() {
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
