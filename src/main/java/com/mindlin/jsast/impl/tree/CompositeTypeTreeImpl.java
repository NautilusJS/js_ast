package com.mindlin.jsast.impl.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Class to implement union and intersection types.
 * 
 * @author mailmindlin
 */
public class CompositeTypeTreeImpl extends AbstractTypeTree implements CompositeTypeTree {
	protected final Kind kind;
	protected final List<TypeTree> constituents;
	
	public CompositeTypeTreeImpl(SourcePosition start, SourcePosition end, Tree.Kind kind, TypeTree... constituents) {
		this(start, end, kind, Arrays.asList(constituents));
	}
	
	public CompositeTypeTreeImpl(SourcePosition start, SourcePosition end, Tree.Kind kind, List<TypeTree> constituents) {
		super(start, end);
		this.kind = kind;
		this.constituents = constituents;
	}
	
	@Override
	public List<TypeTree> getConstituents() {
		return this.constituents;
	}
	
	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	protected int hash() {
		return Objects.hash(getKind(), getConstituents());
	}
}
