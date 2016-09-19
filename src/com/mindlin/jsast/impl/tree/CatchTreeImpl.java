package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public class CatchTreeImpl extends AbstractTree implements CatchTree {
	protected final BlockTree block;
	protected final IdentifierTree param;
	protected final TypeTree type;
	public CatchTreeImpl(long start, long end, BlockTree block, IdentifierTree param, TypeTree type) {
		super(start, end);
		this.block = block;
		this.param = param;
		this.type = type;
	}

	@Override
	public BlockTree getBlock() {
		return block;
	}

	@Override
	public IdentifierTree getParameter() {
		return param;
	}

	@Override
	public TypeTree getType() {
		return type;
	}

}
