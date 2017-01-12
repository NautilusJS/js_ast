package com.mindlin.jsast.tree;

public interface UnaryTree extends ExpressiveExpressionTree {
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitUnary(this, data);
	}
	
	/**
	 * VOID is treated as both an expression and a statement, which allows us to
	 * convert expressions to statements (via <code>void([expression])</code>).
	 * 
	 * @author mailmindlin
	 */
	public interface VoidTree extends UnaryTree, StatementTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.VOID;
		}
	}
}