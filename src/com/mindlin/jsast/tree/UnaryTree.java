package com.mindlin.jsast.tree;

public interface UnaryTree extends ExpressiveExpressionTree, ExpressionTree {
	/**
	 * VOID is treated as both an expression and a statement, which allows us to convert
	 * epressions to statements (<code>VOID([expression])</code>).
	 * 
	 * TODO implement
	 * @author mailmindlin
	 */
	public interface VoidTree extends UnaryTree, StatementTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.VOID;
		}
	}
}