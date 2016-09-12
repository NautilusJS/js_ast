package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.TypeTree;

public class ParameterTreeImpl extends IdentifierTreeImpl implements ParameterTree {
	protected final boolean rest;
	protected final boolean optional;
	protected final TypeTree type;
	protected final ExpressionTree initializer;

	public ParameterTreeImpl(IdentifierTree identifier) {
		this(identifier.getStart(), identifier.getEnd(), identifier.getName());
	}

	public ParameterTreeImpl(long start, long end, String name) {
		super(start, end, name);
		this.rest = false;
		this.optional = false;
		this.type = null;
		this.initializer = null;
	}

	public ParameterTreeImpl(long start, long end, String name, boolean rest, boolean optional, TypeTree type, ExpressionTree initializer) {
		super(start, end, name);
		this.rest = rest;
		this.optional = optional;
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

}
