package com.mindlin.nautilus.tree;

public interface AssignmentTree extends ExpressionTree {
	PatternTree getVariable();
	
	ExpressionTree getValue();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ASSIGNMENT;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitAssignment(this, data);
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		if (getKind() != other.getKind() || this.hashCode() != other.hashCode())
			return false;
		
		AssignmentTree b = (AssignmentTree) other;
		return this.getVariable().equivalentTo(b.getVariable()) && this.getValue().equivalentTo(b.getValue());
	}
}
