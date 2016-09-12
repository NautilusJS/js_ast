package com.mindlin.jsast.impl.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.RandomAccess;

public class LongStack implements RandomAccess, Cloneable, Serializable {
	private static final long serialVersionUID = -2309377622807194378L;
	protected static final int DEFAULT_CAPACITY = 16;
	protected long[] elements;
	protected int size = 0;
	protected final boolean autoShrink;
	protected int shrinkSize = 0;

	public LongStack() {
		this(DEFAULT_CAPACITY);
	}

	public LongStack(int initialCapacity) {
		this(initialCapacity, true);
	}

	public LongStack(LongStack src) {
		this.elements = new long[src.elements.length];
		this.autoShrink = src.autoShrink;
		synchronized (src) {
			System.arraycopy(src.elements, 0, this.elements, 0, src.getSize());
			this.size = src.getSize();
		}
	}

	public LongStack(int initialCapacity, boolean autoShrink) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
		this.elements = new long[initialCapacity];
		this.autoShrink = autoShrink;
		this.shrinkSize = initialCapacity >> 16;
	}

	public int getSize() {
		return size;
	}

	public void ensureSpace(int space) {
		ensureCapacity(size + space);
	}

	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words
	 * in an array. Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit (From
	 * {@link java.util.ArrayList#MAX_ARRAY_SIZE})
	 */
	protected static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	public void ensureCapacity(int capacity) {
		if (capacity < 0)
			throw new IllegalArgumentException("Illegal capacity: " + capacity);
		final int oldCapacity = elements.length;
		if (oldCapacity >= capacity)
			return;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - capacity < 0)
			// Integer overflow
			newCapacity = capacity;
		if (newCapacity - MAX_ARRAY_SIZE < 0)
			newCapacity = MAX_ARRAY_SIZE;
		elements = Arrays.copyOf(elements, newCapacity);
		// Trigger shrink at 1/2 of the current capacity
		// However, shrinks won't be triggered when under 32 elements (Mb<Mc)
		shrinkSize = (autoShrink && newCapacity >> 5 != 0) ? newCapacity >> 1 : -1;
	}

	public void shrinkABit() {
		final int oldCapacity = elements.length;
		if (oldCapacity - 22 > 0)
			return;
		// Target 3/4 of the current capacity
		int newCapacity = oldCapacity - oldCapacity >> 2;
		if (newCapacity < DEFAULT_CAPACITY)
			newCapacity = DEFAULT_CAPACITY;
		if (newCapacity - size < 0)
			newCapacity = size;
		if (newCapacity - oldCapacity > 0 || newCapacity - MAX_ARRAY_SIZE < 0 || newCapacity < 0)
			// Something clearly has gone very wrong
			return;
		elements = Arrays.copyOf(elements, newCapacity);
		// Trigger shrink at 1/2 of the current capacity
		// However, shrinks won't be triggered when under 32 elements (Mb<Mc)
		shrinkSize = (autoShrink && newCapacity >> 5 != 0) ? newCapacity >> 1 : -1;
	}

	public void push(long value) {
		ensureSpace(1);
		elements[size++] = value;
	}

	public long peek() {
		return elements[size];
	}

	public long pop() {
		long result = elements[--size];
		if (size < shrinkSize)
			shrinkABit();
		return result;
	}

	public long[] toArray() {
		return Arrays.copyOf(elements, size);
	}
}