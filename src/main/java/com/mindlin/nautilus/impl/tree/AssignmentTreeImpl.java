package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.AssignmentTree;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.PatternTree;
import com.mindlin.nautilus.tree.Tree;

public class AssignmentTreeImpl extends AbstractTree implements AssignmentTree {
	protected final Tree.Kind kind;
	protected final PatternTree variable;
	protected final ExpressionTree value;
	
	public AssignmentTreeImpl(Kind kind, PatternTree variable, ExpressionTree value) {
		this(variable.getStart(), value.getEnd(), kind, variable, value);
	}
	
	public AssignmentTreeImpl(SourcePosition start, SourcePosition end, Kind kind, PatternTree variable, ExpressionTree value) {
		super(start, end);
		this.kind = kind;
		this.variable = variable;
		this.value = value;
	}
	
	@Override
	public Tree.Kind getKind() {
		return this.kind;
	}

	@Override
	public PatternTree getVariable() {
		return this.variable;
	}

	@Override
	public ExpressionTree getValue() {
		return this.value;
	}
}
