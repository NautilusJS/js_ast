package com.mindlin.jsast.type;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Composite type, such as an intersection or union.
 * 
 * @author mailmindlin
 *
 */
public class CompositeType implements Type {
	public static CompositeType intersection(Type... types) {
		return intersection(Arrays.asList(types));
	}
	
	public static CompositeType intersection(Collection<Type> types) {
		return new CompositeType(Type.Kind.INTERSECTION, types);
	}
	
	public static CompositeType union(Type...types) {
		return union(Arrays.asList(types));
	}
	
	public static CompositeType union(Collection<Type> types) {
		return new CompositeType(Type.Kind.UNION, types);
	}
	
	protected final Type.Kind kind;
	protected final Collection<Type> constituents;
	
	protected Map<Type, TypeMember> propertyCache;
	protected Type resolvedBase;
	
	/**
	 * Create composite type
	 * 
	 * @param kind
	 * @param constituents
	 *            Collection of constituents. Should be a type that preserves
	 *            order.
	 */
	public CompositeType(Type.Kind kind, Collection<Type> constituents) {
		this.kind = kind;
		this.constituents = constituents;
	}
	
	public CompositeType(CompositeType base) {
		this.kind = base.kind;
		this.constituents = new LinkedHashSet<>(base.constituents);
	}
	
	public Collection<Type> getConstituents() {
		return this.constituents;
	}
	
	public CompositeType frozen() {
		return new CompositeType(this.kind, Collections.unmodifiableCollection(new LinkedHashSet<>(this.constituents)));
	}
	
	/**
	 * Attempts to update with a resolved property. Please be sure that all calls to this method are correct, as it doesn't verify anything.
	 * If this CompositeType is frozen, this method does nothing and returns false, rather than throwing an exception or something.
	 * @param name Apparent property name
	 * @param value Apparent property value
	 * @return resolved property was stored
	 */
	public boolean putResolvedProperty(Type key, TypeMember value) {
		if (this.propertyCache == null)
			this.propertyCache = new HashMap<>();
		this.propertyCache.put(key, value);
		return true;
	}
	
	public TypeMember getResolvedProperty(Type key) {
		if (this.propertyCache == null)
			return null;
		return propertyCache.get(key);
	}
	
	@Override
	public Kind getKind() {
		return this.kind;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append('{');
		boolean first = true;
		for (Type constituent : constituents) {
			if (!first)
				sb.append(this.kind == Kind.UNION ? " | " : " & ");
			first = false;
			sb.append(constituent);
		}
		
		sb.append('}');
		return sb.toString();
	}
}
