package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.TemplateElementTree;

public class TemplateElementTreeImpl extends AbstractTree implements TemplateElementTree {
	protected final String raw;
	protected final String cooked;
	
	public TemplateElementTreeImpl(SourcePosition start, SourcePosition end, String raw, String cooked) {
		super(start, end);
		this.raw = raw;
		this.cooked = cooked;
	}
	
	@Override
	public String getRaw() {
		return this.raw;
	}
	
	@Override
	public String getCooked() {
		return this.cooked;
	}
}
