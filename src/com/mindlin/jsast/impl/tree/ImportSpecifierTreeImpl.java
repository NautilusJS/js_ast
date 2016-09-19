package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;

public class ImportSpecifierTreeImpl extends AbstractTree implements ImportSpecifierTree {
	protected final IdentifierTree imported;
	protected final IdentifierTree alias;
	protected final boolean isDefault;

	public ImportSpecifierTreeImpl(IdentifierTree imported) {
		this(imported.getStart(), imported.getEnd(), imported, imported, true);
	}

	public ImportSpecifierTreeImpl(long start, long end, IdentifierTree imported, IdentifierTree alias,
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

}
