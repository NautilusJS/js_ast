package com.mindlin.jsast.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.mindlin.jsast.json.api.JSONExternalizable;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.jsast.json.api.JSONObjectInput;
import com.mindlin.jsast.json.api.JSONObjectOutput;
import com.mindlin.jsast.json.api.JSONOutput;

public class JSONObject implements ConcurrentMap<String, Object>, JSONExternalizable {
	protected final Map<String, Object> backingMap;

	public JSONObject() {
		this(new HashMap<>());
	}
	public JSONObject(Map<String, Object> map) {
		this.backingMap = map;
	}

	@Override
	public int size() {
		return backingMap.size();
	}

	@Override
	public boolean isEmpty() {
		return backingMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return backingMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return backingMap.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return backingMap.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return backingMap.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return backingMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		backingMap.putAll(m);
	}

	@Override
	public void clear() {
		backingMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return backingMap.keySet();
	}

	@Override
	public Collection<Object> values() {
		return backingMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return backingMap.entrySet();
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		return backingMap.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return backingMap.remove(key, value);
	}

	@Override
	public boolean replace(String key, Object oldValue, Object newValue) {
		return backingMap.replace(key, oldValue, newValue);
	}

	@Override
	public Object replace(String key, Object value) {
		return backingMap.replace(key, value);
	}

	@Override
	public void readJSON(JSONInput in) {
		try (JSONObjectInput objIn = in.readObject()) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void writeJSON(JSONOutput out) {
		try (JSONObjectOutput objOut = out.makeObject()) {
			objOut.write(backingMap);
		}
	}

	
}
