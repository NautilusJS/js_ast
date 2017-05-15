package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.TypeTree;

public class ClassPropertyTreeImpl<T extends Tree> extends AbstractTypedPropertyTree implements ClassPropertyTree<T> {
	protected final T value;
	protected final boolean isStatic;
	protected final AccessModifier access;
	protected final PropertyDeclarationType declaration;
	
	public ClassPropertyTreeImpl(long start, long end, AccessModifier access, boolean readonly, boolean isStatic, PropertyDeclarationType declaration, ObjectPropertyKeyTree key, TypeTree type, T value) {
		super(start, end, readonly, key, type);
		this.access = access;
		this.isStatic = isStatic;
		this.declaration = declaration;
		this.value = value;
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public boolean isStatic() {
		return isStatic;
	}
	
	@Override
	public AccessModifier getAccess() {
		return access;
	}
	
	@Override
	public PropertyDeclarationType getDeclarationType() {
		return declaration;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getAccess(), isReadonly(), isStatic(), getDeclarationType(), getKey(), getType(), getValue());
	}
	
}
