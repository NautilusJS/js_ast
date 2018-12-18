package com.mindlin.jsast.impl.tree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.tree.Tree;

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
		else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class) || Number.class.isAssignableFrom(type))
			return value.toString();
		else if (AbstractTree.class.isAssignableFrom(type))
			return ((AbstractTree)value).toJSON();
		else if (Collection.class.isAssignableFrom(type)) {
			sb.append('[');
			for (Object o : ((Collection<?>)value))
				sb.append(writeJSON(o)).append(',');
			if (!((Collection<?>)value).isEmpty())
				sb.setLength(sb.length() - 1);
			sb.append(']');
		} else {
			sb.append('"').append(value.toString()).append('"');
		}
		return sb.toString();
	}
	
	protected final SourceRange range;
	/**
	 * Cache our hash here, so we don't cascade every time
	 */
	private transient int hash;
	
	public AbstractTree(SourceRange pos) {
		this.range = pos;
	}
	
	public AbstractTree(SourcePosition start, SourcePosition end) {
		this(new SourceRange(start, end));
	}
	
	@Override
	public SourcePosition getStart() {
		return this.range.getStart();
	}
	
	@Override
	public SourcePosition getEnd() {
		return this.range.getEnd();
	}
	
	@Override
	public SourceRange getRange() {
		return range;
	}
	
	@Override
	public String toString() {
		return toJSON();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		
		if (other == null || !(other instanceof Tree))
			return false;
		
		if (!this.equivalentTo((Tree) other))
			return false;
		
		if (this.getStart() != ((Tree)other).getStart() || this.getEnd() != ((Tree)other).getEnd())
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int h = this.hash;
		if (h == 0)
			this.hash = h = this.hash();
		return h;
	}
	
	/**
	 * Compute a hash code for this object from scratch. The result of this
	 * method (may?) be cached elsewhere
	 * <p>
	 * This method should be overridden by (almost) all children of this class.
	 * </p>
	 * 
	 * @return hash code computed from scratch
	 */
	protected int hash() {
		return Objects.hashCode(getKind());
	}
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String treeType = getClass().getSimpleName();
		if (treeType.endsWith("Impl"))
			treeType = treeType.substring(0, treeType.length() - 4);//Remove 'Impl' at the end of the string
		sb.append("\"class\":\"").append(treeType).append("\",");
		
		Set<String> getterNames = new HashSet<>();
		//Blacklist getClass(), getStart(), getEnd()
		getterNames.add("getClass");
		getterNames.add("getStart");
		getterNames.add("getEnd");
		Class<?> clazz = getClass();
		do {
			for (Method getter : clazz.getMethods()) {
				if (getter.getParameterCount() > 0 || !getterNames.add(getter.getName()) || !(getter.getName().startsWith("get") || getter.getName().startsWith("is")))
					continue;
				Object value;
				try {
					value = getter.invoke(this, new Object[0]);
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
					continue;
				}
				String name = getter.getName();
				if (name.startsWith("get"))
					name = name.substring(3);
				else if (name.startsWith("is"))
					name = name.substring(2);
				sb.append('"' + name.substring(0,1).toLowerCase()).append(name.substring(1)).append("\":");
				sb.append(AbstractTree.writeJSON(value));
				sb.append(',');
			}
		} while ((clazz = clazz.getSuperclass()) != null);
		sb.setLength(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}
}
