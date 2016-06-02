package com.mindlin.jsast.impl.tree;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public abstract class AbstractTree implements Tree {
	protected final long start, end;
	protected AbstractTree(long start, long end) {
		this.start = start;
		this.end = end;
	}
	@Override
	public long getStart() {
		return this.start;
	}

	@Override
	public long getEnd() {
		return this.end;
	}

	@Override
	public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		//Use reflection to build string. Class-specific overrides may be faster.
		StringBuilder sb = new StringBuilder();
		String treeType = getClass().getSimpleName();
		//TODO test
		treeType = treeType.substring(0,treeType.length() - 4);//Remove 'Impl' at the end of the string
		sb.append(treeType).append('{');
		//TODO combine loops
		Set<Field> fields = new LinkedHashSet<>();
		Class<?> clazz = getClass();
		do {
			for (Field f : clazz.getDeclaredFields())
				if ((f.getModifiers() & Modifier.PROTECTED) != 0)
					fields.add(f);
		} while ((clazz = clazz.getSuperclass()) != null);
		for (Field f : fields) {
			Class<?> type = f.getType();
			Object value;
			try {
				value = f.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			sb.append(f.getName()).append('=');
			if (type.equals(String.class))
				sb.append('"').append(value).append('"');
			else if (type.equals(Character.TYPE))
				sb.append('\'').append(value).append('\'');
			else
				sb.append(value);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}
}
