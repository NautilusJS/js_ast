package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.Tree;

public class CompilationUnitTreeImpl extends AbstractTree implements CompilationUnitTree {
	protected final String unitName;
	protected final LineMap lineMap;
	protected final List<? extends Tree> sourceElements;
	protected final boolean isStrict;
	public CompilationUnitTreeImpl(long start, long end, String unitName, LineMap lineMap, List<? extends Tree> sourceElements, boolean isStrict) {
		super(start, end);
		this.unitName = unitName;
		this.lineMap = lineMap;
		this.sourceElements = sourceElements;
		this.isStrict = isStrict;
	}
	@Override
	public LineMap getLineMap() {
		return this.lineMap;
	}
	@Override
	public List<? extends Tree> getSourceElements() {
		return this.sourceElements;
	}
	@Override
	public String getSourceName() {
		return this.unitName;
	}
	@Override
	public boolean isStrict() {
		return this.isStrict;
	}
	@Override
	public String toString() {
		return new StringBuilder()
				.append("CompilationUnitTree{name: \"").append(unitName)
				.append("\", strict: ").append(isStrict)
				.append(",lineMap:").append(lineMap)
				.append(",sourceElements:").append(sourceElements)
				.append("}")
				.toString();
	}
}
