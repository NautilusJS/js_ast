package com.mindlin.jsast.tree;

import java.util.ArrayList;
import java.util.List;

public class Modifiers {
	public static final int FLAG_PUBLIC    = 1 << 0;
	public static final int FLAG_PRIVATE   = 1 << 1;
	public static final int FLAG_PROTECTED = 1 << 2;
	public static final int FLAG_STATIC    = 1 << 3;
	public static final int FLAG_READONLY  = 1 << 4;
	//Skip a few so we can stay the same as java.lang.reflect.Modifier values
	public static final int FLAG_ABSTRACT  = 1 << 10;
	/**
	 * Flagged as optional ('?')
	 */
	public static final int FLAG_OPTIONAL  = 1 << 25;
	/**
	 * Flagged as 'definite assignment' ('!')
	 */
	public static final int FLAG_DEFINITE  = 1 << 26;
	
	//TODO: better name?
	public static final Modifiers NONE = Modifiers.wrap(0);
	public static final Modifiers PUBLIC = Modifiers.wrap(FLAG_PUBLIC);
	public static final Modifiers PRIVATE = Modifiers.wrap(FLAG_PRIVATE);
	public static final Modifiers PROTECTED = Modifiers.wrap(FLAG_PROTECTED);
	public static final Modifiers STATIC = Modifiers.wrap(FLAG_STATIC);
	public static final Modifiers READONLY = Modifiers.wrap(FLAG_READONLY);
	public static final Modifiers ABSTRACT = Modifiers.wrap(FLAG_ABSTRACT);
	public static final Modifiers OPTIONAL = Modifiers.wrap(FLAG_OPTIONAL);
	public static final Modifiers DEFINITE = Modifiers.wrap(FLAG_DEFINITE);
	
	public static final Modifiers MASK_VISIBILITY = Modifiers.wrap(FLAG_PUBLIC | FLAG_PRIVATE | FLAG_PROTECTED);
	
	public static Modifiers union(Modifiers...accessModifiers) {
		int flags = 0;
		for (int i = 0; i < accessModifiers.length; i++)
			flags |= accessModifiers[i].getFlags();
		
		return Modifiers.wrap(flags);
	}
	
	public static Modifiers intersection(Modifiers...accessModifiers) {
		int flags = 0;
		for (int i = 0; i < accessModifiers.length; i++)
			flags |= accessModifiers[i].getFlags();
		
		return Modifiers.wrap(flags);
	}
	
	public static Modifiers wrap(int flags) {
		//TODO: cache?
		return new Modifiers(flags);
	}
	
	public static Modifiers create(boolean isReadonly, AccessModifier visibility) {
		return Modifiers.create(false, false, isReadonly, visibility);
	}
	
	public static Modifiers create(boolean isStatic, boolean isAbstract, boolean isReadonly, AccessModifier visibility) {
		int flags = 0;
		if (isStatic)
			flags |= Modifiers.FLAG_STATIC;
		if (isAbstract)
			flags |= Modifiers.FLAG_ABSTRACT;
		if (isReadonly)
			flags |= Modifiers.FLAG_READONLY;
		
		if (visibility == AccessModifier.PUBLIC)
			flags |= Modifiers.FLAG_PUBLIC;
		else if (visibility == AccessModifier.PROTECTED)
			flags |= Modifiers.FLAG_PROTECTED;
		else if (visibility == AccessModifier.PRIVATE)
			flags |= Modifiers.FLAG_PRIVATE;
		
		return Modifiers.wrap(flags);
	}
	
	protected final int flags;
	
	protected Modifiers(int flags) {
		this.flags = flags;
	}
	
	public boolean isPublic() {
		return (this.flags & Modifiers.FLAG_PUBLIC) != 0;
	}
	
	public boolean isPrivate() {
		return (this.flags & Modifiers.FLAG_PRIVATE) != 0;
	}
	
	public boolean isProtected() {
		return (this.flags & Modifiers.FLAG_PROTECTED) != 0;
	}
	
	public boolean isStatic() {
		return (this.flags & Modifiers.FLAG_STATIC) != 0;
	}
	
	public boolean isAbstract() {
		return (this.flags & Modifiers.FLAG_ABSTRACT) != 0;
	}
	
	public boolean isReadonly() {
		return (this.flags & Modifiers.FLAG_READONLY) != 0;
	}
	
	public boolean isOptional() {
		return (this.flags & Modifiers.FLAG_OPTIONAL) != 0;
	}
	
	public boolean isDefinite() {
		return (this.flags & Modifiers.FLAG_DEFINITE) != 0;
	}
	
	public AccessModifier getAccess() {
		if (this.isPublic())
			return AccessModifier.PUBLIC;
		else if (this.isPrivate())
			return AccessModifier.PRIVATE;
		else if (this.isProtected())
			return AccessModifier.PROTECTED;
		
		return null;
	}
	
	public int getFlags() {
		return this.flags;
	}
	
	/**
	 * Get if any flags are set
	 * @return
	 */
	public boolean any() {
		return this.flags != 0;
	}
	
	public Modifiers combine(Modifiers other) {
		return Modifiers.wrap(this.flags | other.flags);
	}
	
	public Modifiers subtract(Modifiers other) {
		return Modifiers.wrap(this.flags & ~other.flags);
	}
	
	@Override
	public int hashCode() {
		return this.flags;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Modifiers) && (this.flags == ((Modifiers) obj).flags);
	}
	
	@Override
	public String toString() {
		List<String> labels = new ArrayList<>();
		
		if (this.isPublic())
			labels.add("public");
		if (this.isPrivate())
			labels.add("private");
		if (this.isProtected())
			labels.add("protected");
		if (this.isStatic())
			labels.add("static");
		if (this.isAbstract())
			labels.add("abstract");
		if (this.isReadonly())
			labels.add("readonly");
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String label : labels) {
			if (!first)
				sb.append(" ");
			first = false;
			
			sb.append(label);
		}
		
		return sb.toString();
	}
	
	public static enum AccessModifier {
		PUBLIC,
		PROTECTED,
		PRIVATE,
	}
}