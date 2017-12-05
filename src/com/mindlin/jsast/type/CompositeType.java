package com.mindlin.jsast.type;

import java.util.Collection;
import java.util.Map;

/**
 * Composite type, such as an intersection or union.
 * @author mailmindlin
 *
 */
public class CompositeType implements Type {
	protected final Type.Kind kind;
	protected final Collection<Type> constituents;
	
	protected Map<String, Type> propertyCache;
	protected Type resolvedBase;
	
	/**
	 * Create composite type
	 * @param kind
	 * @param constituents Collection of constituents. Should be a type that preserves order.
	 */
	public CompositeType(Type.Kind kind, Collection<Type> constituents) {
		this.kind = kind;
		this.constituents = constituents;
	}
	
	public Collection<Type> getConstituents() {
		return this.constituents;
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
