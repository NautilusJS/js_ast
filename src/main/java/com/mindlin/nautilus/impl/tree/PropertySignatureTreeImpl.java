package com.mindlin.nautilus.impl.tree;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.Modifiers;
import com.mindlin.nautilus.tree.PropertyName;
import com.mindlin.nautilus.tree.PropertySignatureTree;
import com.mindlin.nautilus.tree.type.TypeTree;

public class PropertySignatureTreeImpl extends AbstractTree implements PropertySignatureTree {
	protected final Modifiers modifiers;
	protected final PropertyName name;
	protected final TypeTree type;
	
	public PropertySignatureTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyName name, TypeTree type) {
		super(start, end);
		this.modifiers = modifiers;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Modifiers getModifiers() {
		return modifiers;
	}
	
	@Override
	public PropertyName getName() {
		return name;
	}

	@Override
	public TypeTree getType() {
		return type;
	}
}
