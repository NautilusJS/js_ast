package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ImportDeclarationTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.StringLiteralTree;

public class ImportDeclarationTreeImpl extends AbstractTree implements ImportDeclarationTree {
	protected final List<ImportSpecifierTree> specifiers;
	protected final StringLiteralTree source;

	public ImportDeclarationTreeImpl(SourcePosition start, SourcePosition end, List<ImportSpecifierTree> specifiers, StringLiteralTree source) {
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
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getSpecifiers(), getSource());
	}
}
