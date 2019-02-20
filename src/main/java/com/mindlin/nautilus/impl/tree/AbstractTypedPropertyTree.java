package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.Modifiers;
import com.mindlin.nautilus.tree.PropertyName;
import com.mindlin.nautilus.tree.PropertyTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public abstract class AbstractTypedPropertyTree extends AbstractTree implements PropertyTree {
	protected final Modifiers modifiers;
	protected final PropertyName name;
	protected final TypeTree type;
	
	public AbstractTypedPropertyTree(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyName name, TypeTree type) {
		super(start, end);
		this.modifiers = modifiers;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}
	
	@Override
	public PropertyName getName() {
		return this.name;
	}
	
	protected TypeTree getType() {
		return type;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getName(), getType());
	}
	
}
