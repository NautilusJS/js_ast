package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.StatementTree;

public class CompilationUnitTreeImpl extends AbstractTree implements CompilationUnitTree {
	protected final SourceFile source;
	protected final LineMap lineMap;
	protected final List<StatementTree> sourceElements;
	protected final boolean isStrict;
	
	public CompilationUnitTreeImpl(SourcePosition start, SourcePosition end, SourceFile source, LineMap lineMap, List<StatementTree> sourceElements, boolean isStrict) {
		super(start, end);
		this.source = source;
		this.lineMap = lineMap;
		this.sourceElements = sourceElements;
		this.isStrict = isStrict;
	}
	
	@Override
	public LineMap getLineMap() {
		return this.lineMap;
	}
	
	@Override
	public List<StatementTree> getSourceElements() {
		return this.sourceElements;
	}
	
	@Override
	public SourceFile getSourceFile() {
		return this.source;
	}
	
	@Override
	public boolean isStrict() {
		return this.isStrict;
	}
	
	@Override
	protected int hash() {
		//TODO include sourceFile()?
		return Objects.hash(getKind(), getSourceElements());
	}
	
}
