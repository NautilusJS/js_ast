package com.mindlin.jsast.impl.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.RandomAccess;

public class BooleanStack implements RandomAccess, Cloneable, Serializable {
	private static final long serialVersionUID = -2309377622807194378L;
	protected static final int DEFAULT_CAPACITY = 16;
	protected boolean[] elements;
	protected int size = 0;
	protected final boolean autoShrink;
	protected int shrinkSize = 0;
	
	public BooleanStack() {
		this(DEFAULT_CAPACITY);
	}
	
	public BooleanStack(int initialCapacity) {
		this(initialCapacity, true);
	}
	
	public BooleanStack(BooleanStack src) {
		this.autoShrink = src.autoShrink;
		synchronized (src) {
			this.size = src.getSize();
			this.shrinkSize = src.shrinkSize;
			this.elements = new boolean[src.elements.length];
			System.arraycopy(src.elements, 0, this.elements, 0, this.size);
		}
	}
	
	public BooleanStack(int initialCapacity, boolean autoShrink) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
		this.elements = new boolean[initialCapacity];
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
	
	/**
	 * Add an element to the top of the stack
	 * @param value
	 */
	public void push(boolean value) {
		ensureSpace(1);
		elements[size++] = value;
	}
	
	/**
	 * Return the element at the top of the stack
	 * @return
	 */
	public boolean peek() {
		return elements[size - 1];
	}
	
	/**
	 * Remove & return the value at the top of the stack
	 * @return
	 */
	public boolean pop() {
		boolean result = elements[--size];
		if (size < shrinkSize)
			shrinkABit();
		return result;
	}
	
	/**
	 * Get an array of the values on this stack. The returned array should be a
	 * copy, such that any changes to it aren't reflected in this object.
	 * 
	 * @return
	 */
	public boolean[] toArray() {
		return Arrays.copyOf(elements, size);
	}
	
	@Override
	public int hashCode() {
		//TODO cache?
		boolean[] elements = this.elements;
		int len = this.size;
		//Mostly for JIT assertion stuff
		//Should always be false.
		if (len > elements.length)
			len = elements.length;
		
		long result = len;
		long tmp = 0;
		for (int i = 0; i < len; i++) {
			tmp <<= 1;
			if (elements[i])
				tmp |= 1;
			if (i % 63 == 0) {
				result = result * 31 + tmp;
				tmp = 0;
			}
		}
		result = result * 51 + tmp;
		return (int) (result ^ (result >>> 32));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof BooleanStack))
			return false;
		
		BooleanStack other = (BooleanStack) o;
		if(this.size != other.size)
			return false;
		
		
		final int len = this.size;
		boolean[] e1 = this.elements, e2 = other.elements;
		
		if (e1.length < len || e2.length < len)
			return false;//Bail here, rather than throwing an error
		
		for (int i = 0; i < len; i++)
			if (e1[i] != e2[i])
				return false;
		return true;
	}
}
