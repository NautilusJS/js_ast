package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.UnaryTree;

public class UnaryTreeImpl extends AbstractTree implements UnaryTree {
	protected final ExpressionTree expression;
	protected final Kind kind;
	
	public UnaryTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression, Kind kind) {
		super(start, end);
		this.expression = expression;
		this.kind = kind;
	}
	
	@Override
	public Kind getKind() {
		return this.kind;
	}

	@Override
	public ExpressionTree getExpression() {
		return this.expression;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getExpression());
	}
	
	public static class AwaitTreeImpl extends UnaryTreeImpl implements AwaitTree {

		public AwaitTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree expression) {
			super(start, end, expression, Kind.AWAIT);
		}
		
	}
}
