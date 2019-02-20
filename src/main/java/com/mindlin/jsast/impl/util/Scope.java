package com.mindlin.jsast.impl.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class Scope {
	protected final CompilationUnitTree root;
	/**
	 * Distance from root scope (root = 0)
	 */
	protected final int depth;
	/**
	 * Parent scope
	 */
	protected final Scope parent;
	/**
	 * Reference to scope that variables are hoisted to; may be self
	 */
	protected final Scope hoistScope;
	/**
	 * Map of defined variables
	 */
	protected Map<String, Object> variables = new LinkedHashMap<>();
	/**
	 * Type of scope
	 */
	protected final ScopeType type;
	
	protected final boolean conditional;
	
	public Scope(Scope parent, ScopeType type) {
		this.conditional = false;//TODO fix
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
		this.conditional = false;
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
	
	public Bindings asBindings() {
		return new ReflectiveBindings();
	}
	
	public class ReflectiveBindings implements Bindings {

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Set<String> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Object> values() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object put(String name, Object value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> toMerge) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object get(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}
		
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
