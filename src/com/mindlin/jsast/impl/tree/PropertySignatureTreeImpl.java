package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.PropertyName;
import com.mindlin.jsast.tree.PropertySignatureTree;
import com.mindlin.jsast.tree.type.TypeTree;

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
