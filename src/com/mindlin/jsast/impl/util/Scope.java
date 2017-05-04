package com.mindlin.jsast.impl.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class Scope {
	protected final CompilationUnitTree root;
	protected final int depth;
	protected final Scope parent;
	protected final Scope hoistScope;
	protected Map<String, String> variables = new LinkedHashMap<>();
	protected final ScopeType type;
	
	public Scope(Scope parent, ScopeType type) {
		this.parent = parent;
		this.depth = parent.depth + 1;
		this.root = parent.root;
		this.type = type;
		
		if (type == ScopeType.MODULE || type == ScopeType.FUNCTION || type == ScopeType.FUNCTION_BLOCK)
			this.hoistScope = this;
		else
			this.hoistScope = parent.hoistScope;
	}
	
	public Scope(CompilationUnitTree root) {
		this.parent = null;
		this.depth = 0;
		this.root = root;
		this.type = ScopeType.MODULE;
		this.hoistScope = this;
	}
	
	public void declareVariable(String name) {
		
	}
	
	public void undeclareVariable(String name) {
		
	}
	
	public void lookupVariable(String name) {
		
	}
	
	public void defineType(String name, TypeTree type) {
		
	}
	
	public boolean undefineType(String name) {
		return false;
	}
	
	public TypeTree lookupType(String name) {
		return null;
	}
	
	public boolean isChildOf(Scope maybeParent) {
		if (parent.depth >= this.depth)
			return false;
		Scope candidate = this;
		for (int i = 0; i < this.depth - parent.depth; i++)
			candidate = candidate.parent;
		return candidate == maybeParent;
	}
	
	public static enum ScopeType {
		MODULE,
		FUNCTION,
		FUNCTION_BLOCK,
		BLOCK,
		CATCH
		;
	}
}
