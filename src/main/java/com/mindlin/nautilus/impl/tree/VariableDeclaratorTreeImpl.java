package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.PatternTree;
import com.mindlin.nautilus.tree.VariableDeclaratorTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class VariableDeclaratorTreeImpl extends AbstractTree implements VariableDeclaratorTree {
	protected final PatternTree identifier;
	protected final ExpressionTree initializer;
	protected final TypeTree type;
	
	public VariableDeclaratorTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), t.getValue(), null, null);
	}
	
	public VariableDeclaratorTreeImpl(SourcePosition start, SourcePosition end, PatternTree identifier, TypeTree type,
			ExpressionTree initializer) {
		super(start, end);
		this.identifier = identifier;
		this.type = type;
		this.initializer = initializer;
	}
	
	@Override
	public ExpressionTree getInitializer() {
		return this.initializer;
	}
	
	@Override
	public TypeTree getType() {
		return this.type;
	}
	
	@Override
	public PatternTree getName() {
		return identifier;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getType(), getInitializer());
	}
	
}
