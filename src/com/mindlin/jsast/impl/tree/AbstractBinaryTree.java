package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;

public abstract class AbstractBinaryTree extends AbstractTree implements BinaryTree {
	public static AbstractBinaryTree make(String operand, long start, long end, ExpressionTree left, ExpressionTree right) {
		switch (operand) {
			case "+":
				return new AdditionTreeImpl(start, end, left, right);
			case "-":
				return new SubtractionTreeImpl(start, end, left, right);
			case "*":
				return new MultiplicationTreeImpl(start, end, left, right);
			case "/":
				return new DivisionTreeImpl(start, end, left, right);
			case "%":
				return new RemainderTreeImpl(start, end, left, right);
			case "**":
				return new ExponentiationTreeImpl(start, end, left, right);
		}
		throw new IllegalArgumentException("Unkown operator " + operand);
	}
	protected final ExpressionTree left, right;
	protected AbstractBinaryTree(long start, long end, ExpressionTree left, ExpressionTree right) {
		super(start, end);
		this.left = left;
		this.right = right;
	}
	protected abstract String getOperand();
	@Override
	public ExpressionTree getLeftOperand() {
		return left;
	}
	@Override
	public ExpressionTree getRightOperand() {
		return right;
	}
	public static class AdditionTreeImpl extends AbstractBinaryTree implements BinaryTree.AdditionTree {
		public AdditionTreeImpl(long start, long end, ExpressionTree left, ExpressionTree right) {
			super(start, end, left, right);
		}
		@Override
		protected String getOperand() {
			return "+";
		}
	}
	public static class SubtractionTreeImpl extends AbstractBinaryTree implements BinaryTree.SubtractionTree {
		public SubtractionTreeImpl(long start, long end, ExpressionTree left, ExpressionTree right) {
			super(start, end, left, right);
		}
		@Override
		protected String getOperand() {
			return "-";
		}
	}
	public static class MultiplicationTreeImpl extends AbstractBinaryTree implements BinaryTree.MultiplicationTree {
		public MultiplicationTreeImpl(long start, long end, ExpressionTree left, ExpressionTree right) {
			super(start, end, left, right);
		}
		@Override
		protected String getOperand() {
			return "*";
		}
	}
	public static class DivisionTreeImpl extends AbstractBinaryTree implements BinaryTree.DivisionTree {
		public DivisionTreeImpl(long start, long end, ExpressionTree left, ExpressionTree right) {
			super(start, end, left, right);
		}
		@Override
		protected String getOperand() {
			return "/";
		}
	}
	public static class RemainderTreeImpl extends AbstractBinaryTree implements BinaryTree.RemainderTree {
		public RemainderTreeImpl(long start, long end, ExpressionTree left, ExpressionTree right) {
			super(start, end, left, right);
		}
		@Override
		protected String getOperand() {
			return "%";
		}
	}
	public static class ExponentiationTreeImpl extends AbstractBinaryTree implements BinaryTree.ExponentiationTree {
		public ExponentiationTreeImpl(long start, long end, ExpressionTree left, ExpressionTree right) {
			super(start, end, left, right);
		}
		@Override
		protected String getOperand() {
			return "**";
		}
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append(getLeftOperand())
			.append(' ')
			.append(getOperand())
			.append(' ')
			.append(getRightOperand())
			.toString();
	}
}
