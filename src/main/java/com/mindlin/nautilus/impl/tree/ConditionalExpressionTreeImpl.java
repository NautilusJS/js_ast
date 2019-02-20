package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;

public class ConditionalExpressionTreeImpl extends AbstractTree implements ConditionalExpressionTree {
	protected final ExpressionTree condition, trueExpr, falseExpr;

	public ConditionalExpressionTreeImpl(ExpressionTree condition, ExpressionTree concequent,
			ExpressionTree alternate) {
		this(condition.getStart(), alternate.getEnd(), condition, concequent, alternate);
	}

	public ConditionalExpressionTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree condition, ExpressionTree concequent,
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
	public ExpressionTree getTrueExpression() {
		return this.trueExpr;
	}

	@Override
	public ExpressionTree getFalseExpression() {
		return this.falseExpr;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getCondition(), getTrueExpression(), getFalseExpression());
	}

}
