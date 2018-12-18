package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

public class CatchTreeImpl extends AbstractTree implements CatchTree {
	protected final BlockTree block;
	protected final VariableDeclaratorTree param;
	public CatchTreeImpl(SourcePosition start, SourcePosition end, BlockTree block, VariableDeclaratorTree param) {
		super(start, end);
		this.block = block;
		this.param = param;
	}

	@Override
	public BlockTree getBlock() {
		return block;
	}

	@Override
	public VariableDeclaratorTree getParameter() {
		return param;
	}

	@Override
	protected int hash() {
		return Objects.hash(getKind(), getParameter(), getBlock());
	}
}
