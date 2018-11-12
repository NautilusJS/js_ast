package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class ParameterTreeImpl extends AbstractTree implements ParameterTree {
	protected final Modifiers access;
	protected final boolean rest;
	protected final boolean optional;
	protected final TypeTree type;
	protected final ExpressionTree initializer;
	protected final PatternTree identifier;
	
	/**
	 * Simple pattern -> ParameterTree
	 * @param identifier
	 */
	public ParameterTreeImpl(PatternTree identifier) {
		this(identifier.getStart(), identifier.getEnd(), identifier);
	}
	
	/**
	 * Simple pattern -> ParameterTree
	 * @param start
	 * @param end
	 * @param identifier
	 */
	public ParameterTreeImpl(SourcePosition start, SourcePosition end, PatternTree identifier) {
		this(start, end, identifier, false, false, null, null);
	}
	
	/**
	 * Constructor for if you don't want any access modifiers
	 * @param start
	 * @param end
	 * @param identifier
	 * @param rest
	 * @param optional
	 * @param type
	 * @param initializer
	 */
	public ParameterTreeImpl(SourcePosition start, SourcePosition end, PatternTree identifier, boolean rest, boolean optional, TypeTree type, ExpressionTree initializer) {
		this(start, end, Modifiers.NONE, identifier, rest, optional, type, initializer);
	}
	
	public ParameterTreeImpl(SourcePosition start, SourcePosition end, Modifiers access, PatternTree identifier, boolean rest, boolean optional, TypeTree type, ExpressionTree initializer) {
		super(start, end);
		this.access = access;
		this.rest = rest;
		this.optional = optional;
		this.identifier = identifier;
		this.type = type;
		this.initializer = initializer;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.access;
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
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getIdentifier(), isRest(), isOptional(), getType(), getInitializer());
	}
}
