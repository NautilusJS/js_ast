package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.Tree;

public class RegExpLiteralTreeImpl extends AbstractTree implements RegExpLiteralTree {
	protected final String body;
	protected final String flags;
	
	public RegExpLiteralTreeImpl(SourcePosition start, SourcePosition end, String body, String flags) {
		super(start, end);
		this.body = body;
		this.flags = flags;
	}
	
	@Override
	public String getBody() {
		return this.body;
	}
	
	@Override
	public String getFlags() {
		return this.flags;
	}
	
	@Override
	public boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		if (getKind() != other.getKind() || this.hashCode() != other.hashCode())
			return false;
		
		RegExpLiteralTree r = (RegExpLiteralTree) other;
		return Objects.equals(this.body, r.getBody()) && Objects.equals(this.flags, r.getFlags());
	}
	
	@Override
	public String[] getValue() {
		return new String[] { body, flags };
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), this.body, this.flags);
	}
	
}
