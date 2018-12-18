package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.HeritageExpressionTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class HeritageExpressionTreeImpl extends AbstractTree implements HeritageExpressionTree {
	protected final ExpressionTree expression;
	protected final List<TypeTree> typeArguments;
	
	public HeritageExpressionTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, List<TypeTree> typeArguments) {
		super(start, end);
		this.expression = expression;
		this.typeArguments = typeArguments;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	public List<TypeTree> getTypeAguments() {
		return this.typeArguments;
	}
	
}
