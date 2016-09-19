package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.UnaryTree;

public class UnaryTreeImpl extends AbstractExpressiveExpressionTree implements UnaryTree {
	protected final Kind kind;
	
	public UnaryTreeImpl(long start, long end, ExpressionTree expression, Kind kind) {
		super(start, end, expression);
		this.kind = kind;
	}
	
	@Override
	public Kind getKind() {
		return this.kind;
	}
	
	public static class VoidTreeImpl extends UnaryTreeImpl implements VoidTree {
		public VoidTreeImpl(ExpressionTree expr) {
			this(expr.getStart(), expr.getEnd(), expr);
		}
		
		public VoidTreeImpl(Token voidToken, ExpressionTree expr) {
			this(voidToken.getStart(), expr.getEnd(), expr);
		}
		
		public VoidTreeImpl(long start, long end, ExpressionTree expression) {
			super(start, end, expression, Kind.VOID);
		}
	}
}
