package com.mindlin.jsast.transform;

import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.CastExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ConditionalExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.MemberExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.UnaryTreeImpl;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.CastExpressionTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionStatementTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressionTreeVisitor;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.NullLiteralTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Expression trees (esp. binary expressions) may be in a form that violates
 * precedence. This transformation adds parentheses to fix it.
 * 
 * @author mailmindlin
 */
public class ExpressionFixerTf implements TreeTransformation<Void> {
	protected int precedence(Tree.Kind kind) {
		switch (kind) {
			case IDENTIFIER:
				return 21;
			case PARENTHESIZED:
				return 20;
			case MEMBER_SELECT:
			case ARRAY_ACCESS:
			case NEW:// Assuming argument list
				return 19;
			case FUNCTION_INVOCATION:
				return 18;
			case POSTFIX_INCREMENT:
			case POSTFIX_DECREMENT:
				return 17;
			case LOGICAL_NOT:
			case BITWISE_NOT:
			case UNARY_PLUS:
			case UNARY_MINUS:
			case PREFIX_INCREMENT:
			case PREFIX_DECREMENT:
			case TYPEOF:
			case VOID:
			case DELETE:
				return 16;
			case EXPONENTIATION:
			case CAST:
				return 15;
			case MULTIPLICATION:
			case DIVISION:
			case REMAINDER:
				return 14;
			case ADDITION:
			case SUBTRACTION:
				return 13;
			case LEFT_SHIFT:
			case RIGHT_SHIFT:
			case UNSIGNED_RIGHT_SHIFT:
				return 12;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
			case IN:
			case INSTANCEOF:
				return 11;
			case EQUAL:
			case NOT_EQUAL:
			case STRICT_EQUAL:
			case STRICT_NOT_EQUAL:
				return 10;
			case BITWISE_AND:
				return 9;
			case BITWISE_XOR:
				return 8;
			case BITWISE_OR:
				return 7;
			case LOGICAL_AND:
				return 6;
			case LOGICAL_OR:
				return 5;
			case CONDITIONAL:
				return 4;
			case ASSIGNMENT:
			case ADDITION_ASSIGNMENT:
			case SUBTRACTION_ASSIGNMENT:
			case EXPONENTIATION_ASSIGNMENT:
			case MULTIPLICATION_ASSIGNMENT:
			case DIVISION_ASSIGNMENT:
			case REMAINDER_ASSIGNMENT:
			case LEFT_SHIFT_ASSIGNMENT:
			case RIGHT_SHIFT_ASSIGNMENT:
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
			case BITWISE_AND_ASSIGNMENT:
			case BITWISE_XOR_ASSIGNMENT:
			case BITWISE_OR_ASSIGNMENT:
				return 3;
			case YIELD:
			case YIELD_GENERATOR:
				return 2;
			case SPREAD:
				return 1;
			case SEQUENCE:
				return 0;
			default:
				return 99;
		}
	}
	
	@Override
	public ExpressionTree visitAssignment(AssignmentTree node, Void d) {
		Tree.Kind kind = node.getKind();
		int precedence = precedence(kind);
		PatternTree lhs = node.getVariable(), oldLhs = lhs;
		ExpressionTree rhs = node.getValue(), oldRhs = rhs;
		
		//LHS options are always greater precedence than assignment ops
		
		if (precedence(rhs.getKind()) < precedence)
			rhs = new ParenthesizedTreeImpl(rhs.getStart(), rhs.getEnd(), rhs);
		
		if (lhs != oldLhs || rhs != oldRhs)
			node = new AssignmentTreeImpl(kind, lhs, rhs);
		
		return node;
	}
	
	protected ExpressionTree wrap(ExpressionTree expr) {
		return new ParenthesizedTreeImpl(expr.getStart(), expr.getEnd(), expr);
	}
	
	@Override
	public ExpressionTree visitBinary(BinaryExpressionTree node, Void d) {
		Tree.Kind kind = node.getKind();
		int precedence = precedence(kind);
		ExpressionTree lhs = node.getLeftOperand(), rhs = node.getRightOperand(), oldLhs = lhs, oldRhs = rhs;
		
		if (precedence(lhs.getKind()) < precedence)
			lhs = wrap(lhs);
		if (precedence(rhs.getKind()) < precedence)
			rhs = wrap(rhs);
		
		if (lhs != oldLhs || rhs != oldRhs) {
			if (kind == Kind.MEMBER_SELECT || kind == Kind.ARRAY_ACCESS)
				node = new MemberExpressionTreeImpl(kind, lhs, rhs);
			else
				node = new BinaryTreeImpl(kind, lhs, rhs);
		}
		
		return node;
	}
	
	@Override
	public ExpressionTree visitCast(CastExpressionTree node, Void d) {
		ExpressionTree expr = node.getExpression();
		if (precedence(node.getKind()) > precedence(expr.getKind())) {
			expr = new ParenthesizedTreeImpl(expr.getStart(), expr.getEnd(), expr);
			TypeTree type = node.getType();
			node = new CastExpressionTreeImpl(expr.getStart(), type.getEnd(), expr, type);
		}
		return node;
	}
	
	@Override
	public ExpressionTree visitConditionalExpression(ConditionalExpressionTree node, Void d) {
		int basePrecedence = precedence(node.getKind());
		
		ExpressionTree condition = node.getCondition();
		if (basePrecedence > precedence(condition.getKind()))
			condition = wrap(condition);
		ExpressionTree trueExpr = node.getTrueExpression();
		if (basePrecedence > precedence(trueExpr.getKind()))
			trueExpr = wrap(trueExpr);
		ExpressionTree falseExpr = node.getFalseExpression();
		if (basePrecedence > precedence(falseExpr.getKind()))
			falseExpr = wrap(trueExpr);
		
		if (condition != node.getCondition() || trueExpr != node.getTrueExpression() || falseExpr != node.getFalseExpression())
			return new ConditionalExpressionTreeImpl(condition, trueExpr, falseExpr);
		
		return node;
	}
	
	@Override
	public StatementTree visitExpressionStatement(ExpressionStatementTree node, Void d) {
		//Fix expressions starting with empty object literals, as they're misinterperted as empty blocks.
		ExpressionTree left = node.getExpression().accept(new LeftmostThingFinder(), null);
		if (left == null)
			return node;
		//Wrap in parens
		if (left.getKind() == Kind.OBJECT_LITERAL && ((ObjectLiteralTree) left).getProperties().isEmpty())
			return new ExpressionStatementTreeImpl(node.getStart(), node.getEnd(), new ParenthesizedTreeImpl(node.getStart(), node.getEnd(), node.getExpression()));
		return node;
	}

	@Override
	public ExpressionTree visitNew(NewTree node, Void d) {
		// TODO Auto-generated method stub
		return TreeTransformation.super.visitNew(node, d);
	}
	
	@Override
	public ExpressionTree visitUnary(UnaryTree node, Void d) {
		ExpressionTree expr = node.getExpression();
		//TODO: handle expr == null (possible if node.getKind() == VOID)
		
		if (precedence(expr.getKind()) < precedence(node.getKind())) {
			expr = new ParenthesizedTreeImpl(expr.getStart(), expr.getEnd(), expr);
			
			node = new UnaryTreeImpl(node.getStart(), node.getEnd(), expr, node.getKind());
		}
		
		return node;
	}
	
	/**
	 * Finds the expression that is, lexically, the most left.
	 * For *why* this is a thing, see {@link ExpressionFixerTf#visitExpressionStatement(ExpressionStatementTree, Void)}.
	 * @author mailmindlin
	 */
	private class LeftmostThingFinder implements ExpressionTreeVisitor<ExpressionTree, Void> {

		@Override
		public ExpressionTree visitArrayLiteral(ArrayLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitAssignment(AssignmentTree node, Void d) {
			//TODO: fix
			//return node.getLeftOperand().accept(this, d);
			throw new UnsupportedOperationException();
		}

		@Override
		public ExpressionTree visitAwait(AwaitTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitBinary(BinaryExpressionTree node, Void d) {
			return node.getLeftOperand().accept(this, d);
		}

		@Override
		public ExpressionTree visitBooleanLiteral(BooleanLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitCast(CastExpressionTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitClassDeclaration(ClassDeclarationTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitConditionalExpression(ConditionalExpressionTree node, Void d) {
			return node.getCondition().accept(this, d);
		}

		@Override
		public ExpressionTree visitFunctionCall(FunctionCallTree node, Void d) {
			return node.getCallee().accept(this, d);
		}

		@Override
		public ExpressionTree visitFunctionExpression(FunctionExpressionTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitIdentifier(IdentifierTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitNew(NewTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitNull(NullLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitNumericLiteral(NumericLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitObjectLiteral(ObjectLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitParentheses(ParenthesizedTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitRegExpLiteral(RegExpLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitSequence(SequenceExpressionTree node, Void d) {
			return node.getElements().isEmpty() ? null : node.getElements().get(0);
		}

		@Override
		public ExpressionTree visitStringLiteral(StringLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitSuper(SuperExpressionTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitTemplateLiteral(TemplateLiteralTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitThis(ThisExpressionTree node, Void d) {
			return node;
		}

		@Override
		public ExpressionTree visitUnary(UnaryTree node, Void d) {
			if (node.getKind() == Kind.POSTFIX_DECREMENT || node.getKind() == Kind.POSTFIX_INCREMENT)
				return node.getExpression().accept(this, d);
			return node;
		}
	}
}
