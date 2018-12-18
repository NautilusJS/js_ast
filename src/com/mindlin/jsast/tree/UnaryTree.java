package com.mindlin.jsast.tree;

public interface UnaryTree extends ExpressionTree {
	
	ExpressionTree getExpression();
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitUnary(this, data);
	}
	
	public interface AwaitTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.AWAIT;
		}
		
		@Override
		default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
			return visitor.visitAwait(this, data);
		}
	}
}