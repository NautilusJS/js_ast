package com.mindlin.jsast.impl.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.type.TypeAliasTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class TypeAliasTreeImpl extends AbstractTree implements TypeAliasTree {
	protected final IdentifierTree name;
	protected final List<TypeParameterDeclarationTree> genericParams;
	protected final TypeTree value;
	
	public TypeAliasTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree alias, List<TypeParameterDeclarationTree> params, TypeTree value) {
		super(start, end);
		this.name = alias;
		this.genericParams = params;
		this.value = value;
	}
	
	@Override
	public IdentifierTree getName() {
		return this.name;
	}

	@Override
	public List<TypeParameterDeclarationTree> getTypeParameters() {
		return this.genericParams;
	}

	@Override
	public TypeTree getValue() {
		return this.value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getTypeParameters(), getValue());
	}
	
}
