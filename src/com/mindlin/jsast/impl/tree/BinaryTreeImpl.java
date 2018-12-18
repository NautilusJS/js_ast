package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree;

public class BinaryTreeImpl extends AbstractTree implements BinaryExpressionTree {
	protected final ExpressionTree left, right;
	protected final Tree.Kind kind;
	
	public BinaryTreeImpl(SourcePosition start, SourcePosition end, Tree.Kind kind, ExpressionTree left, ExpressionTree right) {
		super(start, end);
		this.kind = kind;
		this.left = left;
		this.right = right;
	}
	
	public BinaryTreeImpl(Tree.Kind kind, ExpressionTree left, ExpressionTree right) {
		this(left.getStart(), right.getEnd(), kind, left, right);
	}
	
	protected String getOperand() {
		// TODO finish
		// TODO remove?
		switch (kind) {
			case ADDITION:
				return "+";
			case ADDITION_ASSIGNMENT:
				return "+=";
			case ARRAY_ACCESS:
				return "[";
			case MEMBER_SELECT:
				return ".";
			case ASSIGNMENT:
				return "=";
			case BITWISE_AND:
				return "&";
			case BITWISE_AND_ASSIGNMENT:
				return "&=";
			case BITWISE_OR:
				return "|";
			case BITWISE_OR_ASSIGNMENT:
				return "|=";
			case BITWISE_XOR:
				return "^";
			case BITWISE_XOR_ASSIGNMENT:
				return "^=";
			case DIVISION:
				return "/";
			case DIVISION_ASSIGNMENT:
				return "/=";
			case EQUAL:
				return "==";
			case EXPONENTIATION:
				return "**";
			case EXPONENTIATION_ASSIGNMENT:
				return "**=";
			case GREATER_THAN:
				return ">";
			case GREATER_THAN_EQUAL:
				return "<";
			case IN:
				return " in ";
			case INSTANCEOF:
				return " instanceof ";
			case LEFT_SHIFT:
				return "<<";
			case LEFT_SHIFT_ASSIGNMENT:
				return "<<=";
			case LESS_THAN:
				return "<";
			case LESS_THAN_EQUAL:
				return "=<";
			case LOGICAL_AND:
				return "&";
			case LOGICAL_OR:
				return "!";
			case MULTIPLICATION:
				return "*";
			case MULTIPLICATION_ASSIGNMENT:
				return "*=";
			case NOT_EQUAL:
				return "!=";
			case REMAINDER:
				return "%";
			case REMAINDER_ASSIGNMENT:
				return "%=";
			case RIGHT_SHIFT:
				return ">>";
			case RIGHT_SHIFT_ASSIGNMENT:
				return ">>=";
			case STRICT_EQUAL:
				return "===";
			case STRICT_NOT_EQUAL:
				return "!==";
			case SUBTRACTION:
				return "-";
			case SUBTRACTION_ASSIGNMENT:
				return "-=";
			case UNSIGNED_RIGHT_SHIFT:
				return ">>>";
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
				return ">>>=";
			default:
				break;
		}
		throw new IllegalArgumentException(kind.toString() + " is not a binary operator");
	}
	
	@Override
	public ExpressionTree getLeftOperand() {
		return left;
	}
	
	@Override
	public ExpressionTree getRightOperand() {
		return right;
	}
	
	@Override
	public Kind getKind() {
		return kind;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getLeftOperand(), getRightOperand());
	}
}
