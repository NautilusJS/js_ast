package com.mindlin.jsast.tree.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.mindlin.jsast.tree.Tree;

public class TreePath<T extends Tree> {
	protected final TreePath<?> parent;
	protected final T current;
	
	public TreePath(T root) {
		this(null, root);
	}
	
	public TreePath(TreePath<?> parent, T current) {
		this.parent = parent;
		this.current = current;
	}
	
	public T current() {
		return this.current;
	}
	
	public TreePath<?> parent() {
		return this.parent;
	}
	
	public Iterator<TreePath<?>> parentIterator() {
		return new Iterator<TreePath<?>>() {
			TreePath<?> next = TreePath.this;
			
			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public TreePath<?> next() throws NoSuchElementException {
				if (!hasNext())
					throw new NoSuchElementException();
				TreePath<?> result = this.next;
				this.next = result.parent;
				return result;
			}
		};
	}
}
