package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.BlockTree;
import com.mindlin.nautilus.tree.StatementTree;

public class BlockTreeImpl extends AbstractTree implements BlockTree {
	protected final List<? extends StatementTree> statements;
	protected final boolean scoped;
	
	public BlockTreeImpl(SourcePosition start, SourcePosition end, List<? extends StatementTree> statements) {
		this(start, end, statements, true);
	}
	
	public BlockTreeImpl(SourcePosition start, SourcePosition end, List<? extends StatementTree> statements, boolean scoped) {
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
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getStatements(), isScoped());
	}
}
