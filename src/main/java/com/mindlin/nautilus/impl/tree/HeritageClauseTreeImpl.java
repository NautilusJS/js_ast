package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.HeritageExpressionTree;
import com.mindlin.jsast.tree.Tree;

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
