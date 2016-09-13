package com.mindlin.jsast.impl.tree;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public abstract class AbstractTree implements Tree {
	public static String writeJSON(Object value) {
		if (value == null)
			return "null";
		StringBuilder sb = new StringBuilder();
		Class<?> type = value.getClass();
		if (type.equals(String.class))
			sb.append('"').append(value).append('"');
		else if (type.equals(Character.TYPE))
			sb.append('\'').append(value).append('\'');
		else if (AbstractTree.class.isAssignableFrom(type))
			return ((AbstractTree)value).toJSON();
		else if (Collection.class.isAssignableFrom(type)) {
			sb.append('[');
			for (Object o : ((Collection<?>)value))
				sb.append(writeJSON(o)).append(',');
			if (!((Collection<?>)value).isEmpty())
				sb.setLength(sb.length() - 1);
			sb.append(']');
		} else
			return value.toString();
		return sb.toString();
	}
	protected final long start, end;
	public AbstractTree(long start, long end) {
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
		//Use reflection to build string. Class-specific overrides will definitely be faster.
		StringBuilder sb = new StringBuilder();
		String treeType = getClass().getSimpleName();
		//TODO test
		if (treeType.endsWith("Impl"))
			treeType = treeType.substring(0, treeType.length() - 4);//Remove 'Impl' at the end of the string
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
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String treeType = getClass().getSimpleName();
		if (treeType.endsWith("Impl"))
			treeType = treeType.substring(0, treeType.length() - 4);//Remove 'Impl' at the end of the string
		sb.append("type:\"").append(treeType).append("\",");
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
			sb.append(f.getName()).append(':');
			sb.append(AbstractTree.writeJSON(value));
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}
}
