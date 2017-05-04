package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.TypedPropertyTree;
import com.mindlin.jsast.tree.type.TypeTree;

public abstract class AbstractTypedPropertyTree extends AbstractTree implements TypedPropertyTree {
	protected final boolean readonly;
	protected final ObjectPropertyKeyTree key;
	protected final TypeTree type;
	
	public AbstractTypedPropertyTree(long start, long end, boolean readonly, ObjectPropertyKeyTree key, TypeTree type) {
		super(start, end);
		this.readonly = readonly;
		this.key = key;
		this.type = type;
	}
	
	@Override
	public ObjectPropertyKeyTree getKey() {
		return key;
	}
	
	@Override
	public boolean isReadonly() {
		return readonly;
	}
	
	@Override
	public TypeTree getType() {
		return type;
	}
	
}
