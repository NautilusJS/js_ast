package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.PropertyDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public abstract class AbstractTypedPropertyTree extends AbstractTree implements PropertyDeclarationTree {
	protected final Modifiers modifiers;
	protected final ObjectPropertyKeyTree key;
	protected final TypeTree type;
	
	public AbstractTypedPropertyTree(long start, long end, Modifiers modifiers, ObjectPropertyKeyTree key, TypeTree type) {
		super(start, end);
		this.modifiers = modifiers;
		this.key = key;
		this.type = type;
	}
	
	@Override
	public ObjectPropertyKeyTree getKey() {
		return key;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	public TypeTree getType() {
		return type;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getKey(), getType());
	}
	
}
