package com.mindlin.jsast.impl.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RecursiveMap<K, V> implements Map<K, V> {
	protected final Map<K, V> parent;
	transient int modCount;
	
	Map<K, V> backing;
	
	public RecursiveMap() {
		this(null, new HashMap<>());
	}
	
	public RecursiveMap(Map<K, V> parent) {
		this(parent, new HashMap<>());
	}
	
	public RecursiveMap(Map<K, V> parent, Map<K, V> backing) {
		this.parent = parent;
		this.backing = backing;
	}
	
	@Override
	public int size() {
		return backing.size() + (parent == null ? 0 : parent.size());
	}
	
	@Override
	public boolean isEmpty() {
		return this.isEmpty() && (parent == null || parent.isEmpty());
	}
	
	@Override
	public boolean containsKey(Object key) {
		return backing.containsKey(key) || (parent != null && parent.containsKey(key));
	}
	
	@Override
	public boolean containsValue(Object value) {
		return backing.containsKey(value) || (parent != null && parent.containsKey(value));
	}
	
	@Override
	public V get(Object key) {
		if (backing.containsKey(key))
			return backing.get(key);
		if (parent != null)
			return parent.get(key);
		return null;
	}
	
	protected Optional<V> putIfIn(K key, V value) {
		if (backing.containsKey(key)) {
			V result = backing.put(key, value);
			if (result != value)
				this.modCount++;
			return Optional.of(result);
		}
		if (parent != null) {
			if (parent instanceof RecursiveMap) {
				// Optimal
				Optional<V> result = ((RecursiveMap<K, V>) parent).putIfIn(key, value);
				if (result.isPresent() && result.get() != value)
					this.modCount++;
				//TODO: we can reduce recursion here
				return result;
			} else {
				if (parent.containsKey(key))
					return Optional.of(parent.put(key, value));
			}
		}
		return Optional.empty();
	}
	
	@Override
	public V put(K key, V value) {
		Optional<V> r = this.putIfIn(key, value);
		if (r.isPresent())
			return r.get();
		this.modCount++;
		return backing.put(key, value);
	}
	
	public V putLocal(K key, V value) {
		V result = this.backing.put(key, value);
		if (result != value)
			this.modCount++;
		return result;
	}
	
	public void putAllLocal(Map<? extends K, ? extends V> m) {
		this.backing.putAll(m);
	}
	
	@Override
	public V remove(Object key) {
		if (this.containsKey(key))
			return this.remove(key);
		if (parent != null)
			return parent.remove(key);
		return null;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.forEach(this::put);
	}
	
	@Override
	public void clear() {
		this.modCount++;
		backing.clear();
		if (parent != null)
			parent.clear();
	}
	
	@Override
	public Set<K> keySet() {
		Set<K> result = new HashSet<>();
		result.addAll(backing.keySet());
		if (parent != null)
			result.addAll(parent.keySet());
		return result;
	}
	
	@Override
	public Collection<V> values() {
		//Not super useful for our purposes
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		// Not super useful for our purposes
		throw new UnsupportedOperationException();
	}
	
}
