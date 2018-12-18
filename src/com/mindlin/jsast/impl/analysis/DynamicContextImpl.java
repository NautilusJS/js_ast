package com.mindlin.jsast.impl.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindlin.jsast.exception.TSTypeException;
import com.mindlin.jsast.impl.util.RecursiveMap;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.type.GenericType;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.TypeParameter;
import com.mindlin.jsast.type.TypeVariable;

public class DynamicContextImpl implements DynamicContext {
	protected final int depth;
	protected final DynamicContextImpl parent;
	protected final UIDGenerator ugen;
	
	protected final RecursiveMap<String, Type> typeLookup;
	protected final RecursiveMap<String, VariableInfo> varScope;
	protected final RecursiveMap<String, VariableInfo> localScope;
	protected List<ASTAnnotation> annotations;
	
	protected transient DynamicContextImpl snapshot = null;
	
	protected VariableInfo thisVar, superVar, argumentsVar;
	
	public DynamicContextImpl() {
		this.depth = 0;
		this.parent = null;
		this.ugen = new UIDGenerator();
		
		this.typeLookup = new RecursiveMap<>();
		this.varScope = new RecursiveMap<>();
		this.localScope = this.varScope;
		this.annotations = new ArrayList<>();
		
		this.thisVar = null;
		this.superVar = null;
		this.argumentsVar = null;
	}
	
	protected DynamicContextImpl(DynamicContextImpl parent, boolean pushGenerics, boolean pushVarScope, boolean pushLocalScope, VariableInfo thisVar, VariableInfo superVar, VariableInfo argumentsVar) {
		this.parent = parent;
		this.depth = parent.depth + 1;
		this.ugen = parent.ugen;
		
		this.typeLookup = pushGenerics ? new RecursiveMap<>(parent.typeLookup) : parent.typeLookup;
		if (pushVarScope) {
			//Note that we push on top of localScope
			this.localScope = this.varScope = new RecursiveMap<>(parent.localScope);
		} else {
			this.varScope = parent.varScope;
			this.localScope = pushLocalScope ? new RecursiveMap<>(parent.localScope) : parent.localScope;
		}
		
		this.thisVar = thisVar;
		this.superVar = superVar;
		this.argumentsVar = argumentsVar;
	}
	
	@Override
	public DynamicContextImpl getParent() {
		return this.parent;
	}
	
	@Override
	public Type getType(String name, List<Type> generics) {
		//First check aliases
		Type resolved = typeLookup.get(name);
		if (resolved != null) {
			if (resolved instanceof GenericType) {
				//Some binding required
				GenericType alias = (GenericType) resolved;
				
				int genericsProvided = generics == null ? 0 : generics.size();
				
				//For type aliases, at least, we have to 
				if (genericsProvided > alias.maxTypeParameterCount())
					throw new TSTypeException("Unable to bind generic alias (too many generic arguments: max " + alias.maxTypeParameterCount() + " / " + genericsProvided + " provided)");
				
				if (alias.maxTypeParameterCount() == 0)//No binding required
					return alias;
				
				//TODO type inference pls
				Map<TypeVariable, Type> genericMappings = new HashMap<>();
				for (int i = 0; i < generics.size(); i++) {
					TypeParameter generic = alias.getTypeParameters().get(i);
					Type value = generics.get(i);
					//TODO: assert supertype stuff
					genericMappings.put(generic, value);
				}
				
				//Bind generics
				return TypeBinder.bind(alias, genericMappings);
			} else {
				return resolved;
			}
		}
		
		// Now let's check other stuff...
		//TODO: lookup more stuff
		throw new UnsupportedOperationException("More checking needed");
	}

	@Override
	public VariableInfo getThis() {
		return this.thisVar;
	}
	
	@Override
	public Type thisType() {
		return getThis().getCurrentType();
	}

	@Override
	public VariableInfo getSuper() {
		return this.superVar;
	}

	@Override
	public VariableInfo getArguments() {
		return this.argumentsVar;
	}
	
	@Override
	public ReadonlyContext snapshot() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("DynamicContext#snapshot()");
	}

	@Override
	public void aliasType(String name, List<TypeParameter> params, Type value) {
		this.typeLookup.putLocal(name, null);//TODO: fix me
		throw new UnsupportedOperationException("Not finished");
	}

	@Override
	public void declare(String name, boolean isScoped, boolean isAssignable, Type type) {
		VariableInfo var = new SimpleVariableInfo(this, null, ugen.next(), isAssignable, type);
		
		RecursiveMap<String, VariableInfo> scope = isScoped ? this.localScope : this.varScope;
		scope.putLocal(name, var);
	}

	@Override
	public void define(String name, boolean isScoped, boolean isAssignable, Type type) {
		VariableInfo var = new SimpleVariableInfo(this, null, ugen.next(), isAssignable, type);
		
		RecursiveMap<String, VariableInfo> scope = isScoped ? this.localScope : this.varScope;
		scope.putLocal(name, var);
		//TODO: check for scope stuff
	}

	@Override
	public void assign(VariableInfo variable, Type type) {
		if (variable.isMetaVar()) {
			//TODO: can we really retype this?
			throw new UnsupportedOperationException();
		} else if (variable instanceof SimpleVariableInfo) {
			SimpleVariableInfo sv = (SimpleVariableInfo) variable;
			sv.currentType = type;//TODO: make a stack
		} else {
			throw new IllegalArgumentException("Can't set var");
		}
	}

	@Override
	public DynamicContext pushBlock() {
		return new DynamicContextImpl(this, false, false, true, this.thisVar, this.thisVar, this.argumentsVar);
	}

	@Override
	public DynamicContext pushLoop() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public DynamicContext pushClass(boolean hasGenerics) {
		return new DynamicContextImpl(this, hasGenerics, false, false, this.thisVar, this.superVar, this.argumentsVar);
	}

	@Override
	public DynamicContext pushFunction(boolean hasThis, boolean hasGenerics) {
//		VariableInfo thisVar = hasThis ? this.thisVar : new ThisVariableInfo(ugen.next());
//		VariableInfo argumentsVar = new ArgumentsVariableInfo(ugen.next());
		//TODO: fin
		
		return new DynamicContextImpl(this, hasGenerics, true, false, thisVar, this.superVar, argumentsVar);
	}

	@Override
	public VariableInfo getVar(String name) {
		return this.varScope.get(name);
	}

	@Override
	public List<ASTAnnotation> getAnnotations() {
		return this.annotations;
	}
	
	@Override
	public void annotate(ASTAnnotation.AnnotationLevel level, Tree node, String message) {
		this.annotations.add(new ASTAnnotation(level, node, message));
	}
	
	@Override
	public void defineGeneric(TypeParameterDeclarationTree generic) {
		// TODO Auto-generated method stub
	}

	@Override
	public Type resolveAliases(Type type) {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public Type superType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type argumentsType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStrict() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStrict(boolean strict) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<VariableInfo> getCfControlEdges() {
		// TODO Auto-generated method stub
		return null;
	}
}
