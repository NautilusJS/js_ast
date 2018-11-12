package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class ClassPropertyTreeImpl<T extends ExpressionTree> extends AbstractTypedPropertyTree implements ClassPropertyTree<T> {
	protected final T value;
	protected final PropertyDeclarationType declaration;
	
	public ClassPropertyTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyDeclarationType declaration, ObjectPropertyKeyTree key, TypeTree type, T value) {
		super(start, end, modifiers, key, type);
		this.declaration = declaration;
		this.value = value;
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public PropertyDeclarationType getDeclarationType() {
		return declaration;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getModifiers(), getDeclarationType(), getKey(), getType(), getValue());
	}
	
}
