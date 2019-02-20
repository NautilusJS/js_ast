package com.mindlin.jsast.tree;

import java.util.ArrayList;
import java.util.List;

import com.mindlin.jsast.impl.util.ObjectCache;

public class Modifiers implements Comparable<Modifiers> {
	protected static final ObjectCache<Modifiers> CACHE = new ObjectCache<>();
	
	public static final long FLAG_PUBLIC    = 1L << 0;
	public static final long FLAG_PRIVATE   = 1L << 1;
	public static final long FLAG_PROTECTED = 1L << 2;
	public static final long FLAG_STATIC    = 1L << 3;
	public static final long FLAG_CONST     = 1L << 4;
	//Skip a few so we can stay the same as java.lang.reflect.Modifier values
	public static final long FLAG_ABSTRACT  = 1L << 10;
	public static final long FLAG_STRICT    = 1L << 11;
	// JS Function modifiers (these don't map to Java)
	/**
	 * Generator function
	 */
	public static final long FLAG_GENERATOR = 1L << 25;
	/**
	 * Async function
	 */
	public static final long FLAG_ASYNC     = 1L << 26;
	public static final long FLAG_GETTER    = 1L << 27;
	public static final long FLAG_SETTER    = 1L << 28;
	// Typescript qualifiers
	public static final long FLAG_READONLY  = 1L << 29;
	/**
	 * Flagged as optional ('?')
	 */
	public static final long FLAG_OPTIONAL  = 1L << 30;
	/**
	 * Flagged as 'definite assignment' ('!')
	 */
	public static final long FLAG_DEFINITE  = 1L << 31;
	/**
	 * Declare statement
	 */
	public static final long FLAG_DECLARE   = 1L << 32;
	//Note: Update hashCode() if any more flags are added
	
	//TODO: better name?
	public static final Modifiers NONE = Modifiers.wrap(0);
	public static final Modifiers PUBLIC = Modifiers.wrap(FLAG_PUBLIC);
	public static final Modifiers PRIVATE = Modifiers.wrap(FLAG_PRIVATE);
	public static final Modifiers PROTECTED = Modifiers.wrap(FLAG_PROTECTED);
	public static final Modifiers STATIC = Modifiers.wrap(FLAG_STATIC);
	public static final Modifiers CONST = Modifiers.wrap(FLAG_CONST);
	public static final Modifiers ABSTRACT = Modifiers.wrap(FLAG_ABSTRACT);
	public static final Modifiers STRICT = Modifiers.wrap(FLAG_STRICT);
	public static final Modifiers GENERATOR = Modifiers.wrap(FLAG_GENERATOR);
	public static final Modifiers ASYNC = Modifiers.wrap(FLAG_ASYNC);
	public static final Modifiers GETTER = Modifiers.wrap(FLAG_GETTER);
	public static final Modifiers SETTER = Modifiers.wrap(FLAG_SETTER);
	public static final Modifiers READONLY = Modifiers.wrap(FLAG_READONLY);
	public static final Modifiers OPTIONAL = Modifiers.wrap(FLAG_OPTIONAL);
	public static final Modifiers DEFINITE = Modifiers.wrap(FLAG_DEFINITE);
	public static final Modifiers DECLARE = Modifiers.wrap(FLAG_DECLARE);
	
	/**
	 * Visibility modifiers
	 */
	public static final Modifiers MASK_VISIBILITY = Modifiers.wrap(FLAG_PUBLIC | FLAG_PRIVATE | FLAG_PROTECTED);
	public static final Modifiers MASK_CLASS = Modifiers.wrap(MASK_VISIBILITY.getFlags() | FLAG_ABSTRACT);
	/**
	 * Syntatically-postfix modifiers
	 */
	public static final Modifiers MASK_POSTFIX = Modifiers.wrap(FLAG_OPTIONAL | FLAG_DEFINITE);
	
	public static Modifiers union(Modifiers...accessModifiers) {
		int flags = 0;
		for (int i = 0; i < accessModifiers.length; i++)
			flags |= accessModifiers[i].getFlags();
		
		return Modifiers.wrap(flags);
	}
	
	public static Modifiers intersection(Modifiers...accessModifiers) {
		int flags = 0;
		for (int i = 0; i < accessModifiers.length; i++)
			flags &= accessModifiers[i].getFlags();
		
		return Modifiers.wrap(flags);
	}
	
	public static Modifiers wrap(long flags) {
		return CACHE.intern(new Modifiers(flags));
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
	
	protected final long flags;
	
	protected Modifiers(long flags) {
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
	
	public boolean isConst() {
		return (this.flags & Modifiers.FLAG_CONST) != 0;
	}
	
	public boolean isAbstract() {
		return (this.flags & Modifiers.FLAG_ABSTRACT) != 0;
	}
	
	public boolean isGenerator() {
		return (this.flags & Modifiers.FLAG_GENERATOR) != 0;
	}
	
	public boolean isAsync() {
		return (this.flags & Modifiers.FLAG_ASYNC) != 0;
	}
	
	public boolean isGetter() {
		return (this.flags & Modifiers.FLAG_GETTER) != 0;
	}
	
	public boolean isSetter() {
		return (this.flags & Modifiers.FLAG_SETTER) != 0;
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
	
	public boolean isDeclare() {
		return (this.flags & Modifiers.FLAG_DECLARE) != 0;
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
	
	public long getFlags() {
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
	
	public Modifiers filter(Modifiers other) {
		return Modifiers.wrap(this.flags & other.flags);
	}
	
	@Override
	public int hashCode() {
		// We have fewer than 2**32 bits that we care about
		int hash = (int) this.flags & 0b11111;//Bits 0-4 (PUBLIC/PRIVATE/PROTECTED/STATIC/CONST)
		hash |= (int) (this.flags >> 10) & 0b11;//Bits 10-11 (ABSTRACT/STRICT)
		hash |= (int) (this.flags >> 25) & 0b11111111;//Bits 25-32 (GENERATOR/ASYNC/GETTER/SETTER/READONLY/OPTIONAL/DEFINITE/DECLARE)
		return hash;
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
		if (this.isConst())
			labels.add("const");
		if (this.isReadonly())
			labels.add("readonly");
		if (this.isGetter())
			labels.add("get");
		if (this.isSetter())
			labels.add("set");
		if (this.isGenerator())
			labels.add("generator");
		if (this.isAsync())
			labels.add("async");
		if (this.isOptional())
			labels.add("optional");
		if (this.isDefinite())
			labels.add("definite");
		if (this.isDeclare())
			labels.add("declare");
		
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

	@Override
	public int compareTo(Modifiers other) {
		return Long.compare(this.flags, other.flags);
	}
	
	public static enum AccessModifier {
		PUBLIC,
		PROTECTED,
		PRIVATE,
	}
}