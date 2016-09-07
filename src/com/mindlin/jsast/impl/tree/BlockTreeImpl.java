package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.StatementTree;

public class BlockTreeImpl extends AbstractTree implements BlockTree {
	protected final List<? extends StatementTree> statements;
	protected final boolean scoped;
	public BlockTreeImpl(long start, long end, List<? extends StatementTree> statements) {
		this(start, end, statements, true);
	}
	public BlockTreeImpl(long start, long end, List<? extends StatementTree> statements, boolean scoped) {
		super(start, end);
		this.statements = statements;
		this.scoped = scoped;
	}

	@Override
	public List<? extends StatementTree> getStatements() {
		return statements;
	}

	@Override
	public boolean isScoped() {
		return scoped;
	}
	
}
