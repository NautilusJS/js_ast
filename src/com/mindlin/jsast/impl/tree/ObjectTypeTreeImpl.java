package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
import com.mindlin.jsast.tree.type.TypeElementTree;

public class ObjectTypeTreeImpl extends AbstractTypeTree implements ObjectTypeTree {
	protected final List<TypeElementTree> properties;
	
	public ObjectTypeTreeImpl(SourcePosition start, SourcePosition end, List<TypeElementTree> properties) {
		super(start, end);
		this.properties = properties;
	}
	
	@Override
	public List<TypeElementTree> getDeclaredMembers() {
		return this.properties;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getDeclaredMembers());
	}

}
