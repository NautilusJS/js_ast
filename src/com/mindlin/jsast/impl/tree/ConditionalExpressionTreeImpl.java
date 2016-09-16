package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ConditionalExpressionTreeImpl extends AbstractTree implements ConditionalExpressionTree {
	protected final ExpressionTree condition, trueExpr, falseExpr;

	public ConditionalExpressionTreeImpl(ExpressionTree condition, ExpressionTree concequent,
			ExpressionTree alternate) {
		this(condition.getStart(), alternate.getEnd(), condition, concequent, alternate);
	}

	public ConditionalExpressionTreeImpl(long start, long end, ExpressionTree condition, ExpressionTree concequent,
			ExpressionTree alternate) {
		super(start, end);
		this.condition = condition;
		this.trueExpr = concequent;
		this.falseExpr = alternate;
	}

	@Override
	public ExpressionTree getCondition() {
		return this.condition;
	}

	@Override
	public ExpressionTree getTrueExcpression() {
		return this.trueExpr;
	}

	@Override
	public ExpressionTree getFalseExpression() {
		return this.falseExpr;
	}

}
