package com.mindlin.jsast.type;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.PatternTree;

public class ParameterInfo {
	protected Modifiers modifiers;
	protected PatternTree identifier;
	protected boolean rest;
	protected Type declaredType;
	protected ExpressionTree initializer;
	
	public ParameterInfo(Modifiers modifiers, PatternTree identifier, boolean rest, Type declaredType, ExpressionTree initializer) {
		this.modifiers = modifiers;
		this.identifier = identifier;
		this.rest = rest;
		this.declaredType = declaredType;
		this.initializer = initializer;
	}
	
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	public boolean isRest() {
		return this.rest;
	}
	
	public boolean isOptional() {
		return getModifiers().isOptional();
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
