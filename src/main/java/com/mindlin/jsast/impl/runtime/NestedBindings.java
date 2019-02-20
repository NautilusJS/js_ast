package com.mindlin.jsast.impl.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptContext;

public class NestedBindings implements Bindings {
	protected final NestedBindings parent;
	private final Map<String, Object> local;
	private final Set<String> constants = new HashSet<>();
	protected final int scope;

	public NestedBindings(int scope) {
		this(null, new HashMap<>(), scope);
	}

	public NestedBindings(Map<String, Object> map, int scope) {
		this(null, map, scope);
	}

	public NestedBindings(NestedBindings parent) {
		this(parent, new HashMap<>(), parent.getScope() + 1);
	}

	public NestedBindings(NestedBindings parent, Map<String, Object> map, int scope) {
		this.parent = parent;
		this.local = map;
		this.scope = scope;
	}

	public int getScope() {
		return this.scope;
	}
	
	public NestedBindings getParent() {
		return this.parent;
	}
	
	public int localSize() {
		return local.size();
	}

	@Override
	public int size() {
		return local.size() + (parent != null ? parent.size() : 0);
	}

	@Override
	public boolean isEmpty() {
		return local.isEmpty() && (parent != null && parent.isEmpty());
	}

	public boolean localContainsValue(Object value) {
		return local.containsKey(value);
	}

	@Override
	public boolean containsValue(Object value) {
		return local.containsValue(value) || (parent != null && parent.containsValue(value));
	}

	public void clearLocal() {
		local.clear();
	}

	@Override
	public void clear() {
		clearLocal();
		if (parent != null)
			parent.clear();
	}

	public Set<String> localKeySet() {
		return local.keySet();
	}

	@Override
	public Set<String> keySet() {
		Set<String> result = new HashSet<>();
		NestedBindings c = this;
		do {
			result.addAll(c.localKeySet());
		} while ((c = c.parent) != null);
		return result;
	}

	public Collection<Object> localValues() {
		return local.values();
	}

	@Override
	public Collection<Object> values() {
		List<Object> result = new ArrayList<>();
		NestedBindings c = this;
		do {
			result.addAll(c.localValues());
		} while ((c = c.parent) != null);
		return result;
	}

	public Set<Map.Entry<String, Object>> localEntrySet() {
		return local.entrySet();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> entries = new HashSet<>();
		NestedBindings c = this;
		do {
			entries.addAll(c.localEntrySet());
		} while ((c = c.parent) != null);
		return entries;
	}

	@Override
	public Object put(String key, Object value) {
		if (localContainsKey(key) || parent == null || (this.getScope() <= ScriptContext.GLOBAL_SCOPE && !parent.containsKey(key))) {
			if (constants.contains(key))
				throw new IllegalArgumentException("Cannot set a constant");
			return local.put(key, value);
		}
		return parent.put(key, value);
	}
	
	public Object put(String key, Object value, int scope) {
		NestedBindings c = this;
		while (c.getScope() < scope)
			c = c.parent;
		return c.put(key, value);
	}

	public Object putLocal(String name, Object value) {
		if (constants.contains(name))
			throw new IllegalArgumentException("Cannot set a constant");
		return local.put(name, value);
	}
	
	public Object putConst(String name, Object value) {
		Object r = putLocal(name, value);
		constants.add(name);
		return r;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		for (Map.Entry<? extends String, ? extends Object> e : toMerge.entrySet())
			put(e.getKey(), e.getValue());
	}

	protected boolean localContainsKey(Object key) {
		return local.containsKey(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return local.containsKey(key) || (parent != null && parent.containsKey(key));
	}

	public Object getLocal(Object key) {
		return local.get(key);
	}

	@Override
	public Object get(Object key) {
		if (localContainsKey(key) || parent == null)
			return local.get(key);
		return parent.get(key);
	}

	@Override
	public Object remove(Object key) {
		if (localContainsKey(key) || parent == null) {
			if (constants.contains(key))
				throw new IllegalArgumentException("Cannot remove a constant");
			return local.remove(key);
		}
		return parent.remove(key);
	}

}
