package com.mindlin.jsast.impl.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.mindlin.jsast.impl.util.RecursiveMap;
import com.mindlin.jsast.type.CompositeType;
import com.mindlin.jsast.type.IndexInfo;
import com.mindlin.jsast.type.ObjectType;
import com.mindlin.jsast.type.ParameterInfo;
import com.mindlin.jsast.type.Signature;
import com.mindlin.jsast.type.SignatureImpl;
import com.mindlin.jsast.type.TupleType;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.TypeMember;
import com.mindlin.jsast.type.TypeParameter;
import com.mindlin.jsast.type.TypeVariable;
import com.mindlin.jsast.type.UnaryType;

public class TypeBinder {
	/**
	 * Remove the generics list from a signature.
	 * @param signature
	 * @return
	 */
	public static Signature withoutGenerics(Signature signature) {
		return new SignatureImpl(Collections.emptyList(), signature.getParameters(), signature.getReturnType());
	}
	
	/**
	 * Utility method for binding a bunch of things.
	 * @param unbound unbound types
	 * @param genericMappings mappings to apply
	 * @param result result collection to put bound types in. Returns if binding was done.
	 * @return bound types (best-effort returns the same object if nothing was changed)
	 */
	protected static <C extends Collection<Type>> C bindAll(C unbound, Map<TypeVariable, Type> genericMappings, C result) {
		if (unbound == null)
			return null;
		
		//TODO: maybe lazy construct result list, depending on chance of binding
		boolean modified = false;
		for(Type t : unbound) {
			Type bound = bind(t, genericMappings);
			result.add(bound);
			if (!modified)
				modified |= !Objects.equals(t, bound);
		}
		
		return modified ? result : unbound;
	}
	

	
	/**
	 * Bind a signature for the given arguments.
	 * 
	 * @param ctx
	 *            context to bind signature in
	 * @param signature
	 *            signature to bind
	 * @param providedGenerics
	 *            explicit generics list
	 * @param providedArguments
	 *            explicit arguments list
	 * @return bound signature, else null if not valid target
	 */
	protected static Signature bindSignature(TypeContext ctx, Signature signature, List<Type> providedGenerics, List<Type> providedArguments) {
		//Not enough arguments (exclude)
		//TODO: fix for spread arguments
		if (providedArguments.size() < signature.minArgumentCount())
			return null;//TODO: fix for rest param (may be num args - 1)?
		
		//Too many generic parameters provided (exclude)
		if (providedGenerics.size() > signature.maxTypeParameterCount())
			return null;
		
		if (signature.maxTypeParameterCount() == 0) {
			if (providedArguments.isEmpty())
				//Trivial case (no type parameters nor arguments)
				return signature;//TODO: remove some parameters?
			
			//Assert invocation ability
			Iterator<ParameterInfo> params = signature.getParameters().iterator();
			Iterator<Type> arguments = providedArguments.iterator();
			while (arguments.hasNext()) {
				ParameterInfo param = params.next();
				Type argument = arguments.next();
				
				//TODO: contextual type arguments here?
				if (!TypeCalculator.isAssignableTo(ctx, param.getDeclaredType(), argument))
					return null;
			}
			
			//TODO: rest parameters
			
			return signature;
		}
		

		// === Bind generics ===
		//Constraint check provided generics
		Map<TypeVariable, Type> genericMap = new HashMap<>();
		for (int i = 0; i < providedGenerics.size(); i++) {
			Type provided = providedGenerics.get(i);
			TypeParameter param = signature.getTypeParameters().get(i);
			if (param.getConstraint() != null && !TypeCalculator.isSubtype(ctx, provided, param.getConstraint()))
				return null;//Constraint failed
			genericMap.put(param, provided);
		}
		
		List<Type> boundGenerics = new ArrayList<>(signature.getTypeParameters().size());
		boundGenerics.addAll(providedGenerics);
		
		//Infer generics not explicitly provided
		//TODO: how do we do this?
		for (TypeParameter param : signature.getTypeParameters().subList(providedGenerics.size(), signature.getTypeParameters().size())) {
			Type value = null;
			if (param.getDefault() != null)
				value = param.getDefault();
			else if (param.getConstraint() != null) {
				
			}
			//TODO: finish
			throw new UnsupportedOperationException();
		}
		
		//TODO: finish
		throw new UnsupportedOperationException("Not finished");
	}

	
	/**
	 * Bind generics list.
	 * This is hard because the generic parameters might be referencing eachother, requiring multiple passes.
	 * @param oldGenerics
	 * @param mappings
	 * @return
	 */
	public static List<TypeParameter> bind(List<TypeParameter> oldGenerics, Map<TypeVariable, Type> mappings) {
		boolean modified = false;
		List<TypeParameter> newGenerics;
		RecursiveMap<TypeVariable, Type> localGPMappings = new RecursiveMap<>(mappings);
		while (true) {
			boolean genericsModifiedLP = false;
			newGenerics = new ArrayList<>(oldGenerics.size());
			
			for (TypeParameter oldParam : oldGenerics) {
				Type oldConstraint = oldParam.getConstraint();
				Type newConstraint = oldConstraint == null ? null : bind(oldConstraint, localGPMappings);
				
				Type oldDefault = oldParam.getDefault();
				Type newDefault = oldDefault == null ? null : bind(oldDefault, localGPMappings);
				
				TypeParameter newParam = oldParam;
				if (oldConstraint != newConstraint || oldDefault != newDefault) {
					newParam = new TypeParameter(newConstraint, newDefault);
					genericsModifiedLP = true;
					localGPMappings.putLocal(oldParam, newParam);
				}
				
				newGenerics.add(newParam);
			}
			
			modified |= genericsModifiedLP;
			
			if (!genericsModifiedLP)
				break;
			else
				oldGenerics = newGenerics;
		}
		
		return modified ? newGenerics : oldGenerics;
	}
	
	public static Map<TypeVariable, Type> buildMappings(List<TypeParameter> oldGP, List<TypeParameter> newGP) {
		Map<TypeVariable, Type> result = new HashMap<>();
		//Build map of old generics list -> new generics list 
		for (Iterator<TypeParameter> i = oldGP.iterator(), j = newGP.iterator(); i.hasNext(); ) {
			TypeParameter oldP = i.next(), newP = j.next();
			if (newP != oldP)
				result.put(oldP, newP);
		}
		return result;
	}
	
	/**
	 * Bind an IndexInfo with the given TypeVariable mappings
	 * @param index index to bind
	 * @param mappings mappings to apply
	 * @return bound index (best-effort returns the same object if nothing was changed)
	 */
	public static IndexInfo bind(IndexInfo index, Map<TypeVariable, Type> mappings) {
		Type oldKey = index.getKeyType();
		Type newKey = bind(oldKey, mappings);
		
		Type oldValue = index.getValueType();
		Type newValue = bind(oldValue, mappings);
		
		if (newKey != oldKey || newValue != oldValue)
			return new IndexInfo(index.isReadonly(), newKey, newValue);
		return index;
	}
	
	/**
	 * Bind a Signature with the given TypeVariable mappings.
	 * <p>
	 * <strong>NOTE:</strong> this method binds the type parameter list for the
	 * signature. This is the wrong method to be calling if you're trying to
	 * specialize a signature.
	 * </p>
	 * 
	 * @param signature
	 *            signature to bind
	 * @param genericMappings
	 *            mappings to apply
	 * @return bound signature (best-effort returns the same object if nothing
	 *         was changed)
	 */
	public static Signature bind(Signature signature, Map<TypeVariable, Type> genericMappings) {
		boolean modified = false;//If we updated the signature at all
		
		//Bind type parameter list
		List<TypeParameter> newGenerics = bind(signature.getTypeParameters(), genericMappings);
		modified |= signature.getTypeParameters() != newGenerics;
		
		// We possibly replaced some of our generic parameters, so
		// figure out what additional mappings to use (if applicable)
		Map<TypeVariable, Type> localGM;
		if (modified)
			localGM = new RecursiveMap<>(genericMappings, buildMappings(signature.getTypeParameters(), newGenerics));
		else
			localGM = genericMappings;
		
		
		//Bind parameter list
		List<ParameterInfo> newParams = new ArrayList<>(signature.getParameters().size());
		for (ParameterInfo oldParam : signature.getParameters()) {
			Type oldType = oldParam.getDeclaredType();
			Type newType = oldType == null ? null : bind(oldType, localGM);
			
			ParameterInfo newParam = oldParam;
			if (newType != oldType) {
				newParam = new ParameterInfo(oldParam.getModifiers(), oldParam.getIdentifier(), oldParam.isRest(), newType, oldParam.getInitializer());
				modified = true;
			}
			
			newParams.add(newParam);
		}
		
		//Bind return type
		Type oldRT = signature.getReturnType();
		Type newRT = oldRT == null ? null : bind(oldRT, localGM);
		modified |= oldRT != newRT;
		
		if (modified)
			return new SignatureImpl(newGenerics, newParams, newRT);
		else
			return signature;
	}
	
	/**
	 * Bind a given type with the provided TypeVariable mappings
	 * @param type type to bind
	 * @param genericMappings mappings to apply
	 * @return bound type (best-effort returns the same object if nothing was changed)
	 */
	public static Type bind(Type type, Map<TypeVariable, Type> genericMappings) {
		if (type == null)//TODO should this just throw an exception?
			return null;
		
		switch (type.getKind()) {
			case ARRAY:
			case KEYOF: {
				Type base = ((UnaryType) type).getBaseType();
				Type boundBase = bind(base, genericMappings);
				if (boundBase != base)
					return new UnaryType(type.getKind(), boundBase);
				return type;
			}
			case INTERSECTION:
			case UNION: {
				Collection<Type> constituents = ((CompositeType)type).getConstituents();
				Collection<Type> boundConstituents = bindAll(constituents, genericMappings, new ArrayList<>(constituents.size()));
				
				if (boundConstituents != constituents)
					return type.getKind() == Type.Kind.UNION ? TypeCalculator.union(null, false, boundConstituents) : TypeCalculator.intersection(null, boundConstituents);
				return type;
			}
			case OBJECT: {
				ObjectType obj = (ObjectType) type;
				
				//Bind type parameters
				List<TypeParameter> boundTypeParameters = bind(obj.getTypeParameters(), genericMappings);
				
				Map<TypeVariable, Type> localGM = genericMappings;
				if (boundTypeParameters != obj.getTypeParameters())
					localGM = new RecursiveMap<>(localGM, buildMappings(obj.getTypeParameters(), boundTypeParameters));
				
				//Bind construct signatures
				Set<Signature> boundConstructSignatures = new HashSet<>();
				for (Signature constructSignature : obj.declaredConstructSignatures())
					boundConstructSignatures.add(bind(constructSignature, localGM));
				
				//Bind call signatures
				Set<Signature> boundCallSignatures = new HashSet<>();
				for (Signature callSignature : obj.declaredCallSignatures())
					boundCallSignatures.add(bind(callSignature, localGM));
				
				//Bind index signatures
				Set<IndexInfo> boundIndexSignatures = new HashSet<>();
				for (IndexInfo index : obj.declaredIndexInfo())
					boundIndexSignatures.add(bind(index, localGM));
				
				//Bind property signatures
				Set<TypeMember> boundPropertySignatures = new HashSet<>();
				for (TypeMember oldProp : obj.declaredProperties()) {
					Type oldType = oldProp.getType();
					Type newType = oldType == null ? null : bind(oldType, localGM);
					
					TypeMember newProp = oldProp;
					if (newType != oldType)
						newProp = new TypeMember(oldProp.isRequired(), oldProp.isReadonly(), oldProp.getName(), newType);
					
					boundPropertySignatures.add(newProp);
				}
				
				//TODO: finish
				throw new UnsupportedOperationException();
			}
			case INTRINSIC:
			case BOOLEAN_LITERAL:
			case NUMBER_LITERAL:
			case STRING_LITERAL:
				//No binding requried
				return type;
			case TUPLE: {
				List<Type> slots = ((TupleType) type).getSlots();
				List<Type> boundSlots = bindAll(slots, genericMappings, new ArrayList<>());
				return boundSlots == slots ? type : new TupleType(boundSlots);
			}
			case VARIABLE: {
				TypeVariable tv = (TypeVariable) type;
				Type result = genericMappings.get(tv);
				if (result != null)
					return result;
				Type constraint = tv.getConstraint();
				if (constraint != null)
					constraint = bind(constraint, genericMappings);
				//TODO: finish (incl. mapping default value)
				if (constraint != tv.getConstraint())
					throw new UnsupportedOperationException("Unfinished");
				return tv;
			}
			case REFERENCE:
			default:
				break;
			
		}
		
		throw new UnsupportedOperationException();
	}
}
