package com.mindlin.jsast.impl.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.TypeTree;

public class Scope {
	protected final CompilationUnitTree root;
	protected final Scope parent;
	protected final Scope hoistScope;
	protected Map<String, String> variables = new LinkedHashMap<>();
	protected final ScopeType type;
	
	public Scope(Scope parent, ScopeType type) {
		this.parent = parent;
		this.root = parent.root;
		this.type = type;
		
		if (type == ScopeType.MODULE || type == ScopeType.FUNCTION || type == ScopeType.FUNCTION_BLOCK)
			this.hoistScope = this;
		else
			this.hoistScope = parent.hoistScope;
	}
	
	public Scope(CompilationUnitTree root) {
		this.parent = null;
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
	
	public static enum ScopeType {
		MODULE,
		FUNCTION,
		FUNCTION_BLOCK,
		BLOCK,
		CATCH
		;
	}
}
