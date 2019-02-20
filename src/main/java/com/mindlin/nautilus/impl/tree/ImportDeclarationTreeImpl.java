package com.mindlin.nautilus.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.ImportDeclarationTree;
import com.mindlin.nautilus.tree.ImportSpecifierTree;
import com.mindlin.nautilus.tree.StringLiteralTree;

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
