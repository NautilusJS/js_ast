package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.StatementTree;

public class BlockTreeImpl extends AbstractTree implements BlockTree {
	protected final List<? extends StatementTree> statements;
	public BlockTreeImpl(long start, long end, List<? extends StatementTree> statements) {
		super(start, end);
		this.statements = statements;
	}

	@Override
	public List<? extends StatementTree> getStatements() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
