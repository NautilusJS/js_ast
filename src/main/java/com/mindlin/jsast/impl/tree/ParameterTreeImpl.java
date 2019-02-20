package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.DecoratorTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class ParameterTreeImpl extends AbstractTree implements ParameterTree {
	protected final Modifiers modifiers;
	protected final boolean rest;
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
		this(start, end, identifier, null, null);
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
	public ParameterTreeImpl(SourcePosition start, SourcePosition end, PatternTree identifier, TypeTree type, ExpressionTree initializer) {
		this(start, end, Modifiers.NONE, identifier, false, type, initializer);
	}
	
	public ParameterTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PatternTree identifier, boolean rest, TypeTree type, ExpressionTree initializer) {
		super(start, end);
		this.modifiers = modifiers;
		this.identifier = identifier;
		this.rest = rest;
		this.type = type;
		this.initializer = initializer;
	}

	@Override
	public List<DecoratorTree> getDecorators() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	public boolean isRest() {
		return rest;
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
	public PatternTree getName() {
		return identifier;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getDecorators(), getModifiers(), getName(), isRest(), getType(), getInitializer());
	}
}
