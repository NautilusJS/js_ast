package com.mindlin.jsast.tree;

public interface UnaryTree extends ExpressiveExpressionTree, ExpressionTree {
	public static interface PrefixIncrementNode extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.PREFIX_INCREMENT;
		}
	}
	
	public static interface PostfixIncrementTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.POSTFIX_INCREMENT;
		}
	}
	
	public static interface PrefixDecrementNode extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.PREFIX_DECREMENT;
		}
	}
	
	public static interface PostfixDecrementTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.POSTFIX_DECREMENT;
		}
	}
	public static interface SpreadOperatorTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.SPREAD;
		}
	}
	public static interface UnaryPlusTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.UNARY_PLUS;
		}
	}
	public static interface UnaryMinusTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.UNARY_MINUS;
		}
	}
	public static interface DeleteTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.DELETE;
		}
	}
	public static interface VoidTree extends UnaryTree {
		@Override
		default Tree.Kind getKind() {
			return Tree.Kind.VOID;
		}
	}
}