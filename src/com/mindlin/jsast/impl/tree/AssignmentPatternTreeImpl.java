package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.AssignmentPatternTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.PropertyName;

public class AssignmentPatternTreeImpl extends AbstractTree implements AssignmentPatternTree {
	protected final PropertyName name;
	protected final PatternTree value;
	protected final ExpressionTree initializer;
	
	public AssignmentPatternTreeImpl(SourcePosition start, SourcePosition end, PropertyName name, PatternTree value, ExpressionTree initializer) {
		super(start, end);
		this.name = name;
		this.value = value;
		this.initializer = initializer;
	}

	@Override
	public PropertyName getName() {
		return this.name;
	}
	
	@Override
	public PatternTree getValue() {
		return this.value;
	}
	
	@Override
	public ExpressionTree getInitializer() {
		return initializer;
	}

	@Override
	public Modifiers getModifiers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getValue(), getInitializer());
	}
}
