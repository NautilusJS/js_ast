package com.mindlin.jsast.impl.tree;

import java.util.List;

import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.StringLiteralTree;

public class ImportTreeImpl extends AbstractTree implements ImportTree {
	protected final List<ImportSpecifierTree> specifiers;
	protected final StringLiteralTree source;

	public ImportTreeImpl(long start, long end, List<ImportSpecifierTree> specifiers, StringLiteralTree source) {
		super(start, end);
		this.specifiers = specifiers;
		this.source = source;
	}

	@Override
	public List<ImportSpecifierTree> getSpecifiers() {
		return specifiers;
	}

	@Override
	public StringLiteralTree getSource() {
		return source;
	}
}
