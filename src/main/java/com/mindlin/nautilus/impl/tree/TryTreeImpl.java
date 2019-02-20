package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.BlockTree;
import com.mindlin.nautilus.tree.CatchTree;
import com.mindlin.nautilus.tree.TryTree;

public class TryTreeImpl extends AbstractTree implements TryTree {
	protected final BlockTree tryBlock;
	protected final List<? extends CatchTree> catches;
	protected final BlockTree finallyBlock;
	
	public TryTreeImpl(SourcePosition start, SourcePosition end, BlockTree tryBlock, List<? extends CatchTree> catches, BlockTree finallyBlock) {
		super(start, end);
		this.tryBlock = tryBlock;
		this.catches = catches;
		this.finallyBlock = finallyBlock;
	}
	
	@Override
	public BlockTree getBlock() {
		return tryBlock;
	}
	
	@Override
	public List<? extends CatchTree> getCatches() {
		return catches;
	}
	
	@Override
	public BlockTree getFinallyBlock() {
		return finallyBlock;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getBlock(), getCatches(), getFinallyBlock());
	}
	
}
