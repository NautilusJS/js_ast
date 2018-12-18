package com.mindlin.jsast.impl.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.mindlin.jsast.tree.Modifiers.AccessModifier;
import com.mindlin.jsast.type.CompositeType;
import com.mindlin.jsast.type.IndexInfo;
import com.mindlin.jsast.type.IntrinsicType;
import com.mindlin.jsast.type.LiteralType;
import com.mindlin.jsast.type.ObjectType;
import com.mindlin.jsast.type.ParameterInfo;
import com.mindlin.jsast.type.Signature;
import com.mindlin.jsast.type.TupleType;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.Type.Kind;
import com.mindlin.jsast.type.TypeMember;
import com.mindlin.jsast.type.TypeParameter;
import com.mindlin.jsast.type.TypeVariable;
import com.mindlin.jsast.type.UnaryType;

public class TypeCalculator {
	public static boolean STRICT_NULL_CHECKS = false;
	//Utility methods
	public static <T> boolean matchesAny(Collection<? extends T> values, Predicate<? super T> predicate) {
		for (T value : values)
			if (predicate.test(value))
				return true;
		return false;
	}
	
	public static <T> boolean matchesAll(Collection<? extends T> values, Predicate<? super T> predicate) {
		for (T value : values)
			if (!predicate.test(value))
				return false;
		return true;
	}
	
	public static Type mergeObjectDeclarations(TypeContext ctx, Type t1, Type t2) {
		//TODO: finish
		throw new UnsupportedOperationException();
	}
	
	protected static boolean isNullable(TypeContext ctx, Type type) {
		//TODO: finish
		return false;
	}
	
	/**
	 * Restrict a set of signatures to those which could have been called.
	 * @param ctx
	 * @param candidates
	 * @param providedGenerics
	 * @param providedArguments
	 * @return
	 */
	public static Set<Signature> restrictSignatures(TypeContext ctx, Collection<? extends Signature> candidates, List<Type> providedGenerics, List<Type> providedArguments) {
		Set<Signature> result = new LinkedHashSet<>();
		for (Signature option : candidates) {
			Signature bound = TypeBinder.bindSignature(ctx, option, providedGenerics, providedArguments);
			if (bound != null)
				result.add(bound);
		}
		
		return result;
	}
	
	/**
	 * Restrict a set of IndexInfo's to those which could match the type
	 * @param ctx
	 * @param options
	 * @param indexType
	 * @return
	 */
	public static Collection<IndexInfo> restrictIndex(TypeContext ctx, Collection<IndexInfo> options, Type indexType) {
		Set<IndexInfo> candidates = new HashSet<>();
		IndexInfo bestFit = null;
		//Find candidates
		for (IndexInfo option : options)
			if (isAssignableTo(ctx, option.getKeyType(), indexType))//TODO: fix (e.g., foo[string] is superset of foo[number]
				candidates.add(option);
		//TODO finish
		return candidates;
	}
	
	public static Type simpleResolve(TypeContext context, Type type) {
		if (context == null)
			return type;
		return context.resolveAliases(type);
	}
	
	
	
	// ===== Composite Type Constructors =====
	
	public static Type union(TypeContext ctx, boolean reduceSubtypes, Type...types) {
		return union(ctx, reduceSubtypes, Arrays.asList(types));
	}
	
	public static Type union(TypeContext ctx, boolean reduceSubtypes, Collection<? extends Type> types) {
		if (types.size() == 0)
			return IntrinsicType.NEVER;
		else if (types.size() == 1)
			return types.iterator().next();//Get first
		
		CompositeType result = CompositeType.union(new LinkedHashSet<>());
		addTypesToUnion(ctx, reduceSubtypes, result, types);
		//TODO: remove redundant literals (string | "foo" => string)
		
		if (result.getConstituents().contains(IntrinsicType.ANY))
			return IntrinsicType.ANY;
		if (result.getConstituents().size() == 1)
			return result.getConstituents().iterator().next();
		
		return result;
	}
	
	protected static void addTypesToUnion(TypeContext ctx, boolean reduceSubtypes, CompositeType union, Collection<? extends Type> types) {
		for (Type type : types) {
			if (type.getKind() == Type.Kind.UNION) {
				addTypesToUnion(ctx, reduceSubtypes, union, ((CompositeType) type).getConstituents());
				continue;
			}
			
			//TODO: can we do reduceSubtypes more efficiently (this is O(n^2))
			if (reduceSubtypes && isSubtypeOfAny(ctx, type, union.getConstituents()))
				continue;
			
			//TODO: can we put literal reduction here too?
			
			union.getConstituents().add(type);
		}
	}
	
	public static Type intersection(TypeContext ctx, Type...types) {
		return intersection(ctx, Arrays.asList(types));
	}
	
	public static Type intersection(TypeContext ctx, Collection<? extends Type> types) {
		if (types.size() == 0)
			throw new UnsupportedOperationException();//TODO: return empty object literal
		else if (types.size() == 1)
			return types.iterator().next();//Get first
		
		CompositeType result = CompositeType.intersection(new LinkedHashSet<>());
		addTypesToIntersection(ctx, result, types);
		
		return result;
	}
	
	protected static void addTypesToIntersection(TypeContext ctx, CompositeType intersection, Collection<? extends Type> types) {
		for (Type type : types) {
			if (type.getKind() == Type.Kind.INTERSECTION) {
				addTypesToIntersection(ctx, intersection, ((CompositeType) type).getConstituents());
				continue;
			}
			
			intersection.getConstituents().add(type);
		}
	}
	
	/**
	 * Remove subtypes from union/intersection
	 * @param ctx
	 * @param type
	 */
	protected static void removeSubtypes(TypeContext ctx, CompositeType type) {
		Collection<Type> constituents = type.getConstituents();
		if (constituents.size() == 0)
			return;
		for (Iterator<Type> i = constituents.iterator(); i.hasNext(); )
			if (isSubtypeOfAny(ctx, i.next(), constituents))
				i.remove();
	}
	
	// ===== Object Type Helpers =====
	
	/**
	 * Get the effective constraint on a TypeVariable.
	 * @param ctx Context
	 * @param var Variable to get constraint of
	 * @return constraint (not null)
	 */
	public static Type getEffectiveConstraint(TypeContext ctx, TypeVariable var) {
		for (Type constraint = var.getConstraint(); constraint != null; constraint = ((TypeVariable) constraint).getConstraint())
			if (constraint.getKind() != Type.Kind.VARIABLE)
				return constraint;
		//TODO: return empty object literal here
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Find an apparent call signature, if present.
	 * @param ctx Context to resolve in
	 * @param type Type to get apparent call signature of
	 * @param generalized Whether to only offer generalized (non-specialized) call signatures
	 * @param predicate Filter for call signatures
	 * @return
	 */
	public static Iterator<Signature> apparentCallSignatures(TypeContext ctx, Type type, boolean construct, boolean generalized) {
		if (type.getKind() == Type.Kind.INTRINSIC)
			return Collections.emptyIterator();
		
		if (type.getKind() == Type.Kind.VARIABLE)
			return apparentCallSignatures(ctx, getEffectiveConstraint(ctx, (TypeVariable) type), construct, generalized);
		
		if (type.getKind() == Type.Kind.OBJECT) {
			Set<Signature> hit = new HashSet<>();
			ObjectType objType = (ObjectType) type;
		}
		
		return null;//TODO: finish
	}
	
	/**
	 * Get the numerical value of a type. Gives a little bit more oomph that saying {@code +x} is a Number.
	 * <p>
	 * Uses the following rules in order to determine the type of {@code +x}:
	 * <ul>
	 * <li>If x is a numeric literal or a enum value, +x === x</li>
	 * <li>If x is a string literal, +x is the parsed value of the number of the string (else NaN if not parsable)</li>
	 * <li>If x is a object type with an apparent property for the well-known Symbol.toPrimitive, {@code +x === x[Symbol.toPrimitive]('number')}</li>
	 * <li>+x is a number</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The result of this method should be a subtype of Number.
	 * </p>
	 * @param ctx
	 * @param type
	 * @return type of number value. Is a subtype of Number.
	 */
	public static Type getNumberValue(TypeContext ctx, Type type) {
		if (type.getKind() == Type.Kind.NUMBER_LITERAL)
			return type;
		//TODO: support other rules
		return IntrinsicType.NUMBER;
	}
	
	/**
	 * Find the apparent member of a type.
	 * @param ctx Context to resolve in
	 * @param type Type to get member of
	 * @param key Key type. Usually string/number/union thereof, but given ES6's Map type, can be anything.
	 * @return resolved member
	 */
	public static TypeMember apparentMember(TypeContext ctx, Type type, Type key) {
		System.out.println("Resolve " + key + " on " + type);
		// The apparent members of the primitive type Number and all enum types
		// are the apparent members of the global interface type 'Number'
		if (type == IntrinsicType.NUMBER)
			return apparentMember(ctx, ctx.getType("Number"), key);
		
		// The apparent members of the primitive type Boolean are the apparent
		// members of the global interface type 'Boolean'.
		if (type == IntrinsicType.BOOLEAN || type.getKind() == Kind.BOOLEAN_LITERAL)
			return apparentMember(ctx, ctx.getType("Boolean"), key);
		
		// The apparent members of the primitive type String and all string
		// literal types are the apparent members of the global interface type
		// 'String'.
		if (type == IntrinsicType.STRING || type.getKind() == Kind.STRING_LITERAL) {
			//Number index => char literal
			if (type.getKind() == Kind.STRING_LITERAL && key.getKind() == Type.Kind.NUMBER_LITERAL) {
				double k = (double) ((LiteralType<?>) key).getValue();
				@SuppressWarnings("unchecked")
				String v = ((LiteralType<String>) type).getValue();
				
				if (Double.isNaN(k) || k < 0 || k >= v.length())
					return new TypeMember(true, true, key, IntrinsicType.UNDEFINED);
				
				int i = (int) k;
				if (Math.abs(k - (double) i) > 1e-9)
					return new TypeMember(true, true, key, IntrinsicType.UNDEFINED);
				
				return new TypeMember(true, true, key, LiteralType.of(v.substring(i, i + 1)));
			}
			
			return apparentMember(ctx, ctx.getType("String"), key);
		}
		
		//The apparent members of a type parameter are the apparent members of the constraint (section 3.6.1) of that type parameter.
		if (type.getKind() == Type.Kind.VARIABLE) {
			Type constraint = getEffectiveConstraint(ctx, (TypeVariable) type);
			return apparentMember(ctx, constraint, key);
		}
		
		
		// The apparent members of an object type T are the combination of the
		// following:
		if (type.getKind() == Type.Kind.OBJECT) {
			ObjectType typeObj = (ObjectType) type;
			//The declared and/or inherited members of T.
			for (TypeMember declaredMember : typeObj.declaredProperties())
				if (Objects.equals(declaredMember.getName(), key))//TODO: fix for index-type equivalence
					return declaredMember;
			//TODO: climb the inheritance chain
			
			// The properties of the global interface type 'Object' that aren't
			// hidden by properties with the same name in T.
			Type baseObjectType = ctx.getType("Object");
			if (type != baseObjectType) {
				TypeMember result = apparentMember(ctx, baseObjectType, key);
				if (result != null)
					return result;
			}
			
			// If T has one or more call or construct signatures, the properties
			// of the global interface type 'Function' that aren't hidden by
			// properties with the same name in T.
			if ((!typeObj.declaredCallSignatures().isEmpty() || !typeObj.declaredConstructSignatures().isEmpty())) {
				Type baseFnType = ctx.getType("Function");
				if (type != baseFnType) {
					TypeMember result = apparentMember(ctx, baseFnType, key);
					if (result != null)
						return result;
				}
			}
			
			//Now let's look through indices
			//Search through index signatures
			Collection<IndexInfo> indices = TypeCalculator.restrictIndex(ctx, typeObj.declaredIndexInfo(), key);
			if (!indices.isEmpty()) {
				//TODO: return index result
				throw new UnsupportedOperationException();
			}
			
			return null;
		}
		
		
		//The apparent members of a union type U are determined as follows:
		if (type.getKind() == Type.Kind.UNION) {
			//First we check our cache, to see if we solved this problem already
			TypeMember result = ((CompositeType) type).getResolvedProperty(key);
			if (result != null)
				return result;
			
			// When all constituent types of U have an apparent property named
			// N, U has an apparent property named N of a union type of the
			// respective property types.
			boolean required = true;
			List<Type> constituentTypes = new ArrayList<>();
			for (Type constituent : ((CompositeType) type).getConstituents()) {
				TypeMember constituentProp = apparentMember(ctx, constituent, key);
				if (constituentProp == null)
					continue;
			}
			
			// When all constituent types of U have an apparent string index
			// signature, U has an apparent string index signature of a union
			// type of the respective string index signature types.
			
			
			// When all constituent types of U have an apparent numeric index
			// signature, U has an apparent numeric index signature of a union
			// type of the respective numeric index signature types.
			
			//TODO: finish
			throw new UnsupportedOperationException();
		}
		
		
		//The apparent members of an intersection type I are determined as follows:
		if (type.getKind() == Type.Kind.INTERSECTION) {
			//First we check our cache, to see if we solved this problem already
			TypeMember result = ((CompositeType) type).getResolvedProperty(key);
			if (result != null)
				return result;
			
			// When one of more constituent types of I have an apparent property
			// named N, I has an apparent property named N of an intersection
			// type of the respective property types.
			List<TypeMember> constituentMembers = new ArrayList<>();
			for (Type constituent : ((CompositeType) type).getConstituents()) {
				TypeMember constituentProp = apparentMember(ctx, constituent, key);
				if (constituentProp != null)
					constituentMembers.add(constituentProp);
			}
			
			if (!constituentMembers.isEmpty()) {
				if (constituentMembers.size() == 1) {
					result = constituentMembers.get(0);
				} else {
					//TODO: figure out how access modifiers apply here
					boolean required = false, readonly = true;
					List<Type> constituentTypes = new ArrayList<>(constituentMembers.size());
					for (TypeMember constituentMember : constituentMembers) {
						required |= constituentMember.isRequired();
						readonly &= constituentMember.isReadonly();
						constituentTypes.add(constituentMember.getType());
					}
					
					result = new TypeMember(required, readonly, key, intersection(ctx, constituentTypes));
				}
				
				// (Optional): Cache
				((CompositeType) type).putResolvedProperty(key, result);
				return result;
			}
			
			//TODO: moar checking here?
			throw new UnsupportedOperationException();
		}
		
		return null;
	}
	
	// ===== Type Relation Checking =====
	
	public static boolean isSubtypeOfAny(TypeContext ctx, Type child, Collection<Type> types) {
		for (Type type : types) {
			if (type == child)
				continue;
			if (isSubtype(ctx, child, type))
				return true;
			//TODO: class derivation check here
		}
		return false;
	}
	
	/**
	 * Lightweight identity check. Doesn't go as far as comparing object types.
	 * @param ctx
	 * @param a
	 * @param b
	 * @return optional with present value if determined identical/not identical, else empty optional
	 */
	protected static Optional<Boolean> superficialIdentityCheck(TypeContext ctx, Type a, Type b) {
		if (Objects.equals(a, b))
			return Optional.of(true);
		
		if (a.getKind() == Type.Kind.INTRINSIC
				|| b.getKind() == Type.Kind.INTRINSIC
				|| a.getKind() == Type.Kind.STRING_LITERAL
				|| b.getKind() == Type.Kind.STRING_LITERAL
				|| a.getKind() == Type.Kind.NUMBER_LITERAL
				|| b.getKind() == Type.Kind.NUMBER_LITERAL
				|| a.getKind() == Type.Kind.BOOLEAN_LITERAL
				|| b.getKind() == Type.Kind.BOOLEAN_LITERAL) {
			//One intrinsics/literals, but not the same
			return Optional.of(false);
		}
		
		if (a.getKind() == b.getKind()) {
			switch (a.getKind()) {
				case KEYOF:
				case ARRAY:
					return superficialIdentityCheck(ctx, ((UnaryType) a).getBaseType(), ((UnaryType) b).getBaseType());
				case TUPLE: {
					TupleType aT = (TupleType) a, bT = (TupleType) b;
					if (aT.getSlots().size() != bT.getSlots().size())
						return Optional.of(false);
					for (Iterator<Type> aI = aT.getSlots().iterator(), bI = bT.getSlots().iterator(); aI.hasNext(); ) {
						//TODO: ensure we don't have problems with circular types
						Optional<Boolean> cmp = superficialIdentityCheck(ctx, aI.next(), bI.next());
						if (!cmp.orElse(false))
							return cmp;
					}
					return Optional.of(true);
				}
				default:
					break;
			}
		}
		
		//TODO: more checks here?
		return Optional.empty();
	}
	
	/**
	 * Type identity check
	 * @param ctx
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean areIdentical(TypeContext ctx, Type a, Type b) {
		if (Objects.equals(a, b))
			return true;
		
		//Both references
		if (a.getKind() == b.getKind()) {
			switch (a.getKind()) {
				case REFERENCE: {
					//Check if referring to same thing
					//TODO finish
					
					throw new UnsupportedOperationException();
				}
				case INTERSECTION:
				case UNION: {
					//Iff identical sets of constituents
					Collection<Type> constituentsA = ((CompositeType) a).getConstituents();
					Collection<Type> constituentsB = ((CompositeType) b).getConstituents();
					
					outer:
					for (Type constituentA : constituentsA) {
						for (Type constituentB : constituentsB)
							if (areIdentical(ctx, constituentA, constituentB))
								continue outer;
						return false;
					}
					return true;
				}
				case INTRINSIC:
				case STRING_LITERAL:
				case NUMBER_LITERAL:
				case BOOLEAN_LITERAL:
					return Objects.equals(a, b);//LiteralType equals
				default:
					if (Objects.equals(a, b))
						return true;
			}
		}
		
		if (isObjectType(ctx, a) && isObjectType(ctx, b)) {
			//TODO: finish
			throw new UnsupportedOperationException();
		}
		
		//See if resolving helps things
		Type resolvedA = simpleResolve(ctx, a);
		Type resolvedB = simpleResolve(ctx, b);
		if (a != resolvedA || b != resolvedB)
			return areIdentical(ctx, resolvedA, resolvedB);
		
		return false;
	}
	
	public static boolean isObjectType(TypeContext ctx, Type type) {
		//TODO is this correct?
		return type.getKind() == Type.Kind.OBJECT;
	}
	
	/**
	 * Utility method used for signature comatibility checks in {@link #isCompatible(TypeContext, Type, Type, boolean)}.
	 * Binds a signature using Any as the type argument for all type parameters.
	 * TODO: relax signature to 
	 * @param unbound unbound signature
	 * @return bound signature (best-effort returns the same object if nothing was changed)
	 */
	protected static Signature bindWithAny(Signature unbound) {
		List<TypeParameter> genericParams = unbound.getTypeParameters();
		if (genericParams == null || genericParams.isEmpty())
			return unbound;//No binding needed
		
		//Make map of type parameter => any
		Map<TypeVariable, Type> bindings = new HashMap<>();
		for (TypeParameter param : genericParams)
			bindings.put(param, IntrinsicType.ANY);
		
		return TypeBinder.bind(TypeBinder.withoutGenerics(unbound), bindings);
	}
	
	private static boolean callSignaturesCompatible(TypeContext ctx, Signature boundParentCS, Signature boundChildCS, boolean parentHasRestParam, boolean parentHasVoidReturn) {
		List<? extends ParameterInfo> parentParams = boundParentCS.getParameters();
		// N is a non-specialized call or construct signature in child of the
		// same kind (call or construct) as M
		
		// M has a rest parameter or the number of non-optional parameters
		// in N is less than or equal to the total number of parameters in
		// M
		if (!parentHasRestParam && boundChildCS.minArgumentCount() > parentParams.size())
			return false;
		
		
		// for parameter positions that are present in both signatures:
		List<? extends ParameterInfo> childParams = boundChildCS.getParameters();
		boolean childHasRestParam = !childParams.isEmpty() && childParams.get(childParams.size() - 1).isRest();
		
		if (parentHasRestParam || childHasRestParam)
			//TODO: fix for rest params
			throw new UnsupportedOperationException("Rest parameters not done yet");
		
		//Number of parameter positions present in both signatures
		int numSharedParams = Math.max(parentParams.size(), childParams.size());
		
		for (int i = 0; i < numSharedParams; i++) {
			// each parameter type in N is a subtype or supertype of the
			// corresponding parameter type in M, and
			ParameterInfo parentParam = parentParams.get(i);
			ParameterInfo childParam = childParams.get(i);
			
			//TODO: additional isCompatible mode for this (so we don't redo a lot of work)?
			if (!(isSubtype(ctx, childParam.getDeclaredType(), parentParam.getDeclaredType()) || isSubtype(ctx, parentParam.getDeclaredType(), childParam.getDeclaredType())))
				return false;
		}
		
		
		//the result type of M is Void, or the result type of N is a subtype of that of M.
		if (!(parentHasVoidReturn || isSubtype(ctx, boundChildCS.getReturnType(), boundParentCS.getReturnType())))
			return false;
		
		
		// We found a match for boundParentCS!
		return true;
	}
	
	private static boolean callSignaturesCompatible(TypeContext ctx, Collection<Signature> rawParentCSs, Type child, boolean construct) {
		Set<Signature> boundChildCSCache = new HashSet<>();
		Iterator<Signature> rawChildCSDiscovery = apparentCallSignatures(ctx, child, construct, true);
		
		outer:
		for (Signature rawParentCS : rawParentCSs) {
			Signature boundParentCS = bindWithAny(rawParentCS);
			List<? extends ParameterInfo> parentParams = boundParentCS.getParameters();
			Type parentRT = boundParentCS.getReturnType();
			boolean parentHasRestParam = !parentParams.isEmpty() && parentParams.get(parentParams.size() - 1).isRest();
			boolean parentHasVoidReturn = superficialIdentityCheck(ctx, parentRT, IntrinsicType.VOID).orElse(false);
			
			
			if (matchesAny(boundChildCSCache, boundChildCS -> callSignaturesCompatible(ctx, boundParentCS, boundChildCS, parentHasRestParam, parentHasVoidReturn)))
				continue;
			
			while (rawChildCSDiscovery.hasNext()) {
				Signature rawChildCS = rawChildCSDiscovery.next();
				Signature boundChildCS = bindWithAny(rawChildCS);
				if (!boundChildCSCache.add(boundChildCS))
					continue;
				
				if (callSignaturesCompatible(ctx, boundParentCS, boundChildCS, parentHasRestParam, parentHasVoidReturn))
					continue outer;
			}
			
			// We found no match for boundParentCS, so the sets of call/construct signatures AREN'T compatible
			return false;
		}
		
		//For each parent CS, we found a compatible match
		return true;
	}
	
	protected static void resolveProperties(TypeContext ctx, Type type) {
		if (type.getKind() == Type.Kind.OBJECT) {
			//TODO: finish
		} else if (type.getKind() == Type.Kind.INTERSECTION) {
			
		} else if (type.getKind() == Type.Kind.UNION) {
			
		}
	}
	
	protected static boolean isSimpleTypeCompatible(TypeContext ctx, Type lhs, Type rhs, Relation relation) {
		//T => LHS, S => RHS
		
		//L is the Any type
		if (lhs == IntrinsicType.ANY || rhs == IntrinsicType.NEVER)
			return true;
		//ASSIGNMENT: R is the Any type
		if (rhs == IntrinsicType.ANY)
			return true;
		
		//R is the Undefined type
		if (rhs == IntrinsicType.UNDEFINED)
			return true;
		
		if (lhs == IntrinsicType.NEVER)
			return false;
		
		return false;
	}
	
	/**
	 * Merged subtype/assignable check.
	 * @param ctx
	 * @param lhs
	 * @param rhs
	 * @param assignment Whether we calculate rhs <strong>assignable to</strong> lhs (as opposed to rhs <strong>supertype of</strong>lhs)
	 * @return
	 */
	protected static boolean isCompatible(TypeContext ctx, Type lhs, Type rhs, Relation relation) {
		//L is parent, R is child
		//S and T are identical types
		if (Objects.equals(lhs, rhs))
			return true;
		
		if (relation != Relation.IDENTITIY && isSimpleTypeCompatible(ctx, lhs, rhs, relation))
			return true;
		
		//R is the Null type and L is not the Undefined type
		//TODO: toggle rule via strict null check option
		if (rhs == IntrinsicType.NULL && lhs != IntrinsicType.UNDEFINED)
			return true;//TODO: do we have to resolve rhs a bit more?
		
		
		//R is an enum type and L is the primitive type Number.
		if (rhs.getKind() == Type.Kind.ENUM && lhs == IntrinsicType.NUMBER)
			return true;
		//ASSIGNMENT: L is an enum type and R is the primitive type Number.
		if (lhs.getKind() == Type.Kind.ENUM && rhs == IntrinsicType.NUMBER)
			return true;
		
		
		//R is a string literal type and L is the primitive type String.
		if (rhs.getKind() == Type.Kind.STRING_LITERAL && lhs == IntrinsicType.STRING)
			return true;
		//Extension: R is a number literal type and L is the primitive type Number
		if (rhs.getKind() == Type.Kind.NUMBER_LITERAL && lhs == IntrinsicType.NUMBER)
			return true;
		//Extension: R is a boolean literal type and L is the primitive type Boolean
		if (rhs.getKind() == Type.Kind.BOOLEAN_LITERAL && lhs == IntrinsicType.BOOLEAN)
			return true;
		
		
		//R is a union type and each constituent type of R is [relation] to L.
		if (rhs.getKind() == Type.Kind.UNION)
			if (matchesAll(((CompositeType) rhs).getConstituents(), constituent -> isCompatible(ctx, lhs, constituent, relation)))
				return true;
		
		//R is an intersection type and at least one constituent type of R is a [relation] to L.
		if (rhs.getKind() == Type.Kind.INTERSECTION)
			if (matchesAny(((CompositeType) rhs).getConstituents(), constituent -> isCompatible(ctx, lhs, constituent, relation)))
				return true;
		
		//L is a union type and R is a [relation] to at least one constituent type of L.
		if (lhs.getKind() == Type.Kind.UNION)
			if (matchesAny(((CompositeType) lhs).getConstituents(), constituent -> isCompatible(ctx, constituent, rhs, relation)))
				return true;
		
		//L is an intersection type and R is a [relation] to each constituent type of L.
		if (lhs.getKind() == Type.Kind.INTERSECTION)
			if (matchesAll(((CompositeType) lhs).getConstituents(), constituent -> isCompatible(ctx, constituent, rhs, relation)))
				return true;
		
		
		//R is a type parameter and the constraint of R is a [relation] to L.
		if (rhs.getKind() == Type.Kind.VARIABLE)
			//TODO: Is the assumption that if the constraint of R ISN'T a subtype/assignable to of L, R can't be a subtype of L correct?
			return isCompatible(ctx, lhs, getEffectiveConstraint(ctx, (TypeVariable) rhs), relation);
		
		
		// R is an object type, an intersection type, an enum type, or the
		// Number, Boolean, or String primitive type, L is an object type, and
		// for each member M in T, one of the following is true:
		//TODO: check predicate correctness here
		if (isObjectType(ctx, lhs) && (isObjectType(ctx, rhs) || rhs.getKind() == Type.Kind.INTERSECTION || rhs.getKind() == Type.Kind.ENUM || rhs.getKind() == Type.Kind.INTRINSIC)) {
			ObjectType lhsObj = (ObjectType) lhs;
			
			// M is a property
			for (TypeMember lhsProp : lhsObj.declaredProperties()) {//lhsProp is M
				// S has an apparent property N where:
				// M and N have the same name,
				
				TypeMember rhsProp = apparentMember(ctx, rhs, lhsProp.getName());
				
				if (rhsProp == null) {
					//Couldn't find a property with the same name on RHS
					System.out.println("Couldn't find " + lhsProp.getName() + " (" + lhsProp.isRequired() + ")");
					if (relation == Relation.ASSIGNABLE && !lhsProp.isRequired())
						continue;
					
					return false;
				}
				
				// if M is a required property, N is also a required property
				if (lhsProp.isRequired() && !rhsProp.isRequired())
					return false;
				
				// the type of N is a subtype of/assignable to that of M
				if (!isCompatible(ctx, lhsProp.getType(), rhsProp.getType(), relation))
					return false;
				
				// One of the folowing must be true:
				AccessModifier lhsAccess = lhsProp.getAccess(), rhsAccess = rhsProp.getAccess();
				// M and N are both public
				if ((lhsAccess == AccessModifier.PUBLIC || lhsAccess == null) && (rhsAccess == AccessModifier.PUBLIC || rhsAccess == null))
					continue;

				// M and N originate in the same declaration, and are either both private or both protected
				if (lhsAccess == rhsAccess && (lhsAccess == AccessModifier.PRIVATE || lhsAccess == AccessModifier.PROTECTED)) {
					//TODO: finish
					throw new UnsupportedOperationException();
				}

				// lhsProp is protected and rhsProp is declared in a class derived from the
				// class in which lhsProp is declared.
				if (lhsAccess == AccessModifier.PROTECTED) {
					//TODO: finish
					throw new UnsupportedOperationException();
				}
				
				return false;
			}
			
			
			// M is a non-specialized call or construct signature and S has an
			// apparent call or construct signature N where, when M and N are
			// instantiated using type Any as the type argument for all type
			// parameters declared by M and N (if any),
			
			//TODO: resolve child call signatures
			if (!callSignaturesCompatible(ctx, lhsObj.declaredCallSignatures(), rhs, false))
				return false;
			
			if (!callSignaturesCompatible(ctx, lhsObj.declaredConstructSignatures(), rhs, true))
				return false;
			
			// M is a string index signature of type U, and U is the Any type or
			// S has an apparent string index signature of a type that is a
			// subtype of U.

			// M is a numeric index signature of type U, and U is the Any type
			// or S has an apparent string or numeric index signature of a type
			// that is a subtype of U.
			
			//Note: we go a bit past the TS 2.6 spec here in order to support types with more complex indices (e.g., the ES6 Map type)
			for (IndexInfo lhsIndex : lhsObj.declaredIndexInfo()) {
				if (lhsIndex.getKeyType() == IntrinsicType.STRING) {
					if (lhsIndex.getValueType() == IntrinsicType.ANY)
						continue;
				} else if (lhsIndex.getKeyType() == IntrinsicType.NUMBER) {
					if (lhsIndex.getValueType() == IntrinsicType.ANY)
						continue;
				}
				
				TypeMember rhsIndex = apparentMember(ctx, rhs, lhsIndex.getKeyType());
				if (rhsIndex == null)
					return false;
				
				if (!isCompatible(ctx, lhsIndex.getValueType(), rhsIndex.getType(), relation))
					return false;
			}
			
			//Object types were the same
			return true;
		}
		
		return false;
	}
	
	public static boolean isSubtype(TypeContext ctx, Type child, Type parent) {
		return isCompatible(ctx, parent, child, Relation.SUBTYPE);
	}
	
	public static boolean isAssignableTo(TypeContext ctx, Type lhs, Type rhs) {
		//For x: T = y: S
		return isCompatible(ctx, lhs, rhs, Relation.ASSIGNABLE);
	}
	
	protected static enum Relation {
		IDENTITIY,
		SUBTYPE,
		ASSIGNABLE;
	}
}
