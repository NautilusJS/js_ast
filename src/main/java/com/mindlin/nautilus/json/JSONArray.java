package com.mindlin.jsast.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.mindlin.jsast.json.api.JSONArrayInput;
import com.mindlin.jsast.json.api.JSONArrayOutput;
import com.mindlin.jsast.json.api.JSONExternalizable;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.jsast.json.api.JSONOutput;

public class JSONArray implements List<Object>, JSONExternalizable {
	protected final List<Object> backingList;

	public JSONArray() {
		this(new ArrayList<>());
	}

	public JSONArray(List<Object> list) {
		this.backingList = list;
	}

	@Override
	public int size() {
		return backingList.size();
	}

	@Override
	public boolean isEmpty() {
		return backingList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return backingList.contains(o);
	}

	@Override
	public Iterator<Object> iterator() {
		return backingList.iterator();
	}

	@Override
	public Object[] toArray() {
		return backingList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return backingList.toArray(a);
	}

	@Override
	public boolean add(Object e) {
		return backingList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return backingList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backingList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		return backingList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		return backingList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return backingList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return backingList.retainAll(c);
	}

	@Override
	public void clear() {
		backingList.clear();
	}

	@Override
	public Object get(int index) {
		return backingList.get(index);
	}

	@Override
	public Object set(int index, Object element) {
		return backingList.set(index, element);
	}

	@Override
	public void add(int index, Object element) {
		backingList.add(index, element);
	}

	@Override
	public Object remove(int index) {
		return backingList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return backingList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return backingList.lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return backingList.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return backingList.listIterator(index);
	}

	@Override
	public JSONArray subList(int fromIndex, int toIndex) {
		return new JSONArray(backingList.subList(fromIndex, toIndex));
	}
	
	public JSONArray getJSONArray(int index) {
		return (JSONArray) get(index);
	}
	
	public JSONObject getJSONObject(int index) {
		return (JSONObject) get(index);
	}
	
	@Override
	public void readJSON(JSONInput in) {
		try (JSONArrayInput arrIn = in.readArray()) {
			arrIn.forEachRemaining(this::add);
		}
	}

	@Override
	public void writeJSON(JSONOutput out) {
		try (JSONArrayOutput arrOut = out.makeArray()) {
			arrOut.write(backingList);
		}
	}

}
