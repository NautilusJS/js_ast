package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree;

public class BinaryTreeImpl extends AbstractTree implements BinaryTree {
	protected final ExpressionTree left, right;
	protected Tree.Kind kind;
	public BinaryTreeImpl(long start, long end, Tree.Kind kind, ExpressionTree left, ExpressionTree right) {
		super(start, end);
		this.kind = kind;
		this.left = left;
		this.right = right;
	}
	public BinaryTreeImpl(Tree.Kind kind, ExpressionTree left, ExpressionTree right) {
		this(left.getStart(), right.getEnd(), kind, left, right);
	}
	protected String getOperand() {
		//TODO finish
		switch(kind) {
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
			case INSTANCE_OF:
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
			case OF:
				//TODO should this be valid?
				break;
			case PROPERTY:
				break;
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
			case DELETE:
				break;
			case EXPORT:
				break;
			case FOR_IN_LOOP:
				break;
			case FOR_LOOP:
				break;
			case FOR_OF_LOOP:
				break;
			case FUNCTION:
				break;
			case FUNCTION_EXPRESSION:
				break;
			case FUNCTION_INVOCATION:
				break;
			case IDENTIFIER:
				break;
			case IMPORT:
				break;
			case LOGICAL_NOT:
				break;
			case PARENTHESIZED:
				break;
			case RETURN:
				break;
			case SCOPED_FUNCTION:
				break;
			case TYPEOF:
				break;
			case VARIABLE:
				break;
			case VOID:
				break;
			default:
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
	public String toString() {
		return new StringBuilder()
			.append(getLeftOperand())
			.append(' ')
			.append(getOperand())
			.append(' ')
			.append(getRightOperand())
			.toString();
	}
	@Override
	public Kind getKind() {
		return kind;
	}
}
