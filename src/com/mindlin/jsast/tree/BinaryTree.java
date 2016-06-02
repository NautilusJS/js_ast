package com.mindlin.jsast.tree;

public interface BinaryTree extends ExpressionTree {
	ExpressionTree getLeftOperand();
	ExpressionTree getRightOperand();
	public static interface AdditionTree extends BinaryTree {
		default Tree.Kind getKind() {
			return Tree.Kind.ADDITION;
		}
	}
	public static interface SubtractionTree extends BinaryTree {
		default Tree.Kind getKind() {
			return Tree.Kind.SUBTRACTION;
		}
	}
	public static interface MultiplicationTree extends BinaryTree {
		default Tree.Kind getKind() {
			return Tree.Kind.MULTIPLICATION;
		}
	}
	public static interface DivisionTree extends BinaryTree {
		default Tree.Kind getKind() {
			return Tree.Kind.DIVISION;
		}
	}
	public static interface RemainderTree extends BinaryTree {
		default Tree.Kind getKind() {
			return Tree.Kind.REMAINDER;
		}
	}
	public static interface ExponentiationTree extends BinaryTree {
		default Tree.Kind getKind() {
			return Tree.Kind.EXPONENTIATION;
		}
	}
}
