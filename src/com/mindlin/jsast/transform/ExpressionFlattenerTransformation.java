package com.mindlin.jsast.transform;

import com.mindlin.jsast.impl.tree.NumericLiteralTreeImpl;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.NumericLiteralTree;

public class ExpressionFlattenerTransformation implements ASTTransformation<ExpressionTree, ASTTransformerContext> {
	@Override
	public ExpressionTree visitNumericLiteral(NumericLiteralTree node, ASTTransformerContext ctx) {
		//For testing
		return new NumericLiteralTreeImpl(node.getStart(), node.getEnd(), node.getValue().doubleValue() * 2);
	}

	/*@Override
	public ExpressionTree visitBinary(BinaryTree node, ASTTransformerContext ctx) {
		ExpressionTree lhs = node.getLeftOperand().accept(this, ctx);
		ExpressionTree rhs = node.getRightOperand().accept(this, ctx);
		
		
		return node;
	}
	
	@Override
	public ExpressionTree visitParentheses(ParenthesizedTree node, ASTTransformerContext d) {
		
		return ASTTransformation.super.visitParentheses(node, d);
	}*/
	
}
