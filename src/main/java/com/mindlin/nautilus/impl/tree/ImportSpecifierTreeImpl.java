package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.ImportSpecifierTree;

public class ImportSpecifierTreeImpl extends AbstractTree implements ImportSpecifierTree {
	protected final IdentifierTree imported;
	protected final IdentifierTree alias;
	protected final boolean isDefault;

	public ImportSpecifierTreeImpl(IdentifierTree imported) {
		this(imported.getStart(), imported.getEnd(), imported, imported, true);
	}

	public ImportSpecifierTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree imported, IdentifierTree alias,
			boolean isDefault) {
		super(start, end);
		this.imported = imported;
		this.alias = alias;
		this.isDefault = isDefault;
	}

	@Override
	public IdentifierTree getAlias() {
		return alias;
	}

	@Override
	public IdentifierTree getImported() {
		return imported;
	}

	@Override
	public boolean isDefault() {
		return isDefault;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getAlias(), getImported(), isDefault());
	}

}
