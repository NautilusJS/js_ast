package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.ClassPropertyTree.AccessModifier;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class ParameterTreeImpl extends AbstractTree implements ParameterTree {
	protected final AccessModifier access;
	protected final boolean rest;
	protected final boolean optional;
	protected final TypeTree type;
	protected final ExpressionTree initializer;
	protected final PatternTree identifier;
	
	public ParameterTreeImpl(PatternTree identifier) {
		this(identifier.getStart(), identifier.getEnd(), identifier);
	}
	
	public ParameterTreeImpl(long start, long end, PatternTree identifier) {
		this(start, end, null, identifier, false, false, null, null);
	}
	
	public ParameterTreeImpl(long start, long end, AccessModifier access, PatternTree identifier, boolean rest, boolean optional, TypeTree type, ExpressionTree initializer) {
		super(start, end);
		this.access = access;
		this.rest = rest;
		this.optional = optional;
		this.identifier = identifier;
		this.type = type;
		this.initializer = initializer;
	}
	
	@Override
	public boolean isRest() {
		return rest;
	}
	
	@Override
	public boolean isOptional() {
		return optional;
	}
	
	@Override
	public TypeTree getType() {
		return type;
	}
	
	@Override
	public ExpressionTree getInitializer() {
		return initializer;
	}
	
	@Override
	public PatternTree getIdentifier() {
		return identifier;
	}
	
	@Override
	public AccessModifier getAccessModifier() {
		return access;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getAccessModifier(), getIdentifier(), isRest(), isOptional(), getType(), getInitializer());
	}
}
