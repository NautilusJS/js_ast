package com.mindlin.jsast.impl.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.RandomAccess;

public class LongStack implements RandomAccess, Cloneable, Serializable {
	private static final long serialVersionUID = -2309377622807194378L;
	protected static final int DEFAULT_CAPACITY = 16;
	protected long[] elements;
	protected int size = 0;
	public LongStack() {
		this(DEFAULT_CAPACITY);
	}
	public LongStack(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
		this.elements = new long[initialCapacity];
	}
	public LongStack(LongStack src) {
		this(src.elements.length);
		synchronized (src) {
			System.arraycopy(src.elements, 0, this.elements, 0, src.getSize());
			this.size = src.getSize();
		}
	}
	public int getSize() {
		return size;
	}
	public void ensureSpace(int space) {
		ensureCapacity(size + 1);
	}
	public void ensureCapacity(int capacity) {
		if (capacity < 0)
			throw new IllegalArgumentException("Illegal capacity: " + capacity);
		final int oldCapacity = elements.length;
		if (oldCapacity >= capacity)
			return;
		int newCapacity = oldCapacity * 2;
		if (newCapacity - capacity < 0)
			//Integer overflow
			newCapacity = capacity;
		
		elements = Arrays.copyOf(elements, newCapacity);
	}
	public void push(long value) {
		ensureSpace(1);
		elements[size++] = value;
	}
	public long peek() {
		return elements[size];
	}
	public long pop() {
		return elements[size--];
	}
}