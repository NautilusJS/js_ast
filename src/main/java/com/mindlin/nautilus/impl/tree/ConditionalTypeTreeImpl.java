package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.type.ConditionalTypeTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class ConditionalTypeTreeImpl extends AbstractTree implements ConditionalTypeTree {
	protected final TypeTree check;
	protected final TypeTree limit;
	protected final TypeTree concequent;
	protected final TypeTree alternate;
	
	public ConditionalTypeTreeImpl(SourcePosition start, SourcePosition end, TypeTree check, TypeTree limit, TypeTree concequent, TypeTree alternate) {
		super(start, end);
		this.check = check;
		this.limit = limit;
		this.concequent = concequent;
		this.alternate = alternate;
	}
	
	@Override
	public TypeTree getCheckType() {
		return check;
	}
	
	@Override
	public TypeTree getLimitType() {
		return limit;
	}
	
	@Override
	public TypeTree getConecquent() {
		return concequent;
	}
	
	@Override
	public TypeTree getAlternate() {
		return alternate;
	}
	
}
