package com.mindlin.nautilus.impl.tree;

import java.util.List;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.HeritageClauseTree;
import com.mindlin.nautilus.tree.HeritageExpressionTree;
import com.mindlin.nautilus.tree.Tree;

public class HeritageClauseTreeImpl extends AbstractTree implements HeritageClauseTree {
	protected final Tree.Kind kind;
	protected final List<HeritageExpressionTree> types;
	
	public HeritageClauseTreeImpl(SourcePosition start, SourcePosition end, Tree.Kind kind, List<HeritageExpressionTree> types) {
		super(start, end);
		this.kind = kind;
		this.types = types;
	}

	@Override
	public Kind getKind() {
		return this.kind;
	}
	
	@Override
	public List<HeritageExpressionTree> getTypes() {
		return this.types;
	}
}
