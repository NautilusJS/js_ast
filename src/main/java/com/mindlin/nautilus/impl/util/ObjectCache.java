package com.mindlin.jsast.impl.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Supports caching immutable objects.
 * @author mailmindlin
 *
 * @param <E>
 */
public class ObjectCache<E> {
	public static final int MIN_CAPACITY = 16;
	public static final int MAX_CAPACITY = 1 << 30;
	public static final float DEFAULT_LF = 0.75f;
	
	protected Entry<E>[] table;
	
	protected int size = 0;
	
	protected int threshold = 0;
	
	protected final float loadFactor;
	
	protected ReferenceQueue<E> queue = new ReferenceQueue<>();
	
	public ObjectCache() {
		this(DEFAULT_LF);
	}
	
	@SuppressWarnings("unchecked")
	public ObjectCache(float loadFactor) {
		this.loadFactor = loadFactor;
		this.table = new Entry[MIN_CAPACITY];
	}
	
	protected synchronized void resize(int newCap) {
		Entry<E>[] oldTable = this.table;
		int oldCap = oldTable.length;
		
		if (oldCap == MAX_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}
		
		@SuppressWarnings("unchecked")
		Entry<E>[] newTable = (Entry<E>[]) new Entry<?>[newCap];
		
		this.transfer(oldTable, newTable);
		
		this.table = newTable;
		
		if (size > threshold / 2) {
			threshold = (int) (newCap * loadFactor);
		} else {
			this.clearOldEntries();
			this.transfer(newTable, oldTable);
			this.table = oldTable;
		}
	}
	
	protected void transfer(Entry<E>[] from, Entry<E>[] to) {
		//Transfer from old table to new table
		for (int i = 0; i < from.length; i++) {
			Entry<E> node = from[i];
			from[i] = null;
			while (node != null) {
				Entry<E> next = node.next;
				
				E key = node.get();
				if (key == null) {
					//Dead reference
					node.next = null;
					this.size--;
				} else {
					int idx = node.hash & (to.length - 1);
					node.next = to[idx];
					to[idx] = node;
				}
				
				node = next;
			}
		}
	}
	
	/**
	 * Remove entries that have been enqueued
	 */
	protected void clearOldEntries() {
		for(Object x; (x = queue.poll()) != null; ) {
			synchronized (queue) {
				@SuppressWarnings("unchecked")
				Entry<E> e = (Entry<E>) x;
				int idx = e.hash & (this.table.length - 1);
				
				Entry<E> prev = this.table[idx], p = prev;
				while (p != null) {
					Entry<E> next = p.next;
					if (e == p) {
						if (p == prev)
							this.table[idx] = next;
						else
							prev.next = next;
						
						this.size--;
						break;
					}
				}
			}
		}
	}
	
	public E intern(E value) {
		int hash = value.hashCode();
		this.clearOldEntries();
		Entry<E>[] table = this.table;
		int len = table.length;
		
		int idx = hash & (len - 1);
		for (Entry<E> node = table[idx]; node != null; node = node.next) {
			if (hash == node.hash) {
				E current = node.get();
				//Found value
				if (current == value || value.equals(current))
					return current;
			}
		}
		
		//Intern
		table[idx] = new Entry<>(hash, value, table[idx], this.queue);
		
		if (++size > threshold)
			this.resize(table.length * 2);
		
		return value;
	}
	
	private static class Entry<E> extends WeakReference<E> {
		final int hash;
		volatile Entry<E> next;
		
		public Entry(int hash, E value, Entry<E> next, ReferenceQueue<? super E> queue) {
			super(value, queue);
			this.hash = hash;
			this.next = next;
		}
		
	}
}
