package com.mindlin.jsast.impl.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.AnyTypeTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.GenericParameterTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.LiteralTypeTree;
import com.mindlin.jsast.tree.type.MemberTypeTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.TypeTreeVisitor;
import com.mindlin.jsast.type.IndexInfo;
import com.mindlin.jsast.type.IntrinsicType;
import com.mindlin.jsast.type.LiteralType;
import com.mindlin.jsast.type.ObjectTypeImpl;
import com.mindlin.jsast.type.ParameterInfo;
import com.mindlin.jsast.type.Signature;
import com.mindlin.jsast.type.SignatureImpl;
import com.mindlin.jsast.type.TupleType;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.TypeMember;
import com.mindlin.jsast.type.TypeParameter;
import com.mindlin.jsast.type.TypeParameter.RebindableTypeParameter;
import com.mindlin.jsast.type.UnaryType;

public class TypeExpressionResolver implements TypeTreeVisitor<Type, TypeContext> {
	
	public List<Type> map(List<TypeTree> nodes, TypeContext d) {
		List<Type> result = new ArrayList<>(nodes.size());
		for (TypeTree node : nodes)
			result.add(node.accept(this, d));
		return result;
	}
	
	/**
	 * Map function type => Signature
	 * @param fn
	 * @param ctx
	 * @return
	 */
	public Signature mapFunctionType(FunctionTypeTree fn, TypeContext ctx) {
		
		RecursiveTypeContext localCtx = RecursiveTypeContext.inheritStatic(ctx);
		
		boolean requireSecondPass = false;//If second-pass resolution is needed
		List<TypeParameter> generics = new ArrayList<>(fn.getGenerics().size());
		
		for (GenericParameterTree generic : fn.getGenerics()) {
			String name = generic.getName().getName();
			
			TypeParameter unbound;
			if (generic.getDefault() == null && generic.getSupertype() == null) {
				unbound = new TypeParameter(null, null);//No binding necessary (orphan type parameter, no constraint, no default)
			} else {
				unbound = TypeParameter.unbound();
				requireSecondPass = true;//We have to pass through again, to finish binding
			}
			
			//Put unbound in local context
			localCtx.put(name, unbound);
			generics.add(unbound);
		}
		
		//Finish 
		if (requireSecondPass) {
			Iterator<GenericParameterTree> astIter = fn.getGenerics().iterator();
			Iterator<TypeParameter> mappedIter = generics.iterator();
			while (astIter.hasNext()) {
				GenericParameterTree ast = astIter.next();
				TypeParameter unbound = mappedIter.next();
				
				Type constraint = ast.getSupertype() == null ? null : ast.getSupertype().accept(this, localCtx);
				Type defaultValue = ast.getDefault() == null ? null : ast.getDefault().accept(this, localCtx);
				
				//Bind if needed
				if (constraint != null && defaultValue != null)
					((RebindableTypeParameter) unbound).rebind(constraint, defaultValue);
			}
		}
		
		List<ParameterInfo> parameters = new ArrayList<>(fn.getParameters().size());
		for (ParameterTree param : fn.getParameters())
			parameters.add(new ParameterInfo(param.getModifiers(), param.getIdentifier(), param.isRest(), param.isOptional(), param.getType().accept(this, localCtx), param.getInitializer()));
		
		//TODO: null return type => implicit void?
		Type returnType = fn.getReturnType() == null ? null : fn.getReturnType().accept(this, localCtx);
		
		return new SignatureImpl(generics, parameters, returnType);
	}
	
	@Override
	public Type visitAnyType(AnyTypeTree node, TypeContext d) {
		return IntrinsicType.ANY;
	}
	
	@Override
	public Type visitArrayType(ArrayTypeTree node, TypeContext d) {
		Type base = node.getBaseType().accept(this, d);
		return new UnaryType(Type.Kind.ARRAY, base);
	}
	
	@Override
	public Type visitFunctionType(FunctionTypeTree node, TypeContext d) {
		Signature signature = mapFunctionType(node, d);
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Type visitIdentifierType(IdentifierTypeTree node, TypeContext ctx) {
		List<Type> generics;
		//Resolve generics list
		if (node.getGenerics() != null && !node.getGenerics().isEmpty()) {
			generics = new ArrayList<>(node.getGenerics().size());
			for (TypeTree generic : node.getGenerics())
				generics.add(generic.accept(this, ctx));//TODO: map by hand
		} else {
			generics = Collections.emptyList();
		}
		
		//Delegate to TypeContext for resolution
		return ctx.getType(node.getIdentifier().toString(), generics);
	}
	
	@Override
	public Type visitIndexType(IndexSignatureTree node, TypeContext d) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Type visitInterfaceType(ObjectTypeTree node, TypeContext ctx) {
		RebindableTypeParameter thisTP = TypeParameter.unbound();
		Set<Signature> callSignatures = new HashSet<>();
		Set<Signature> constructSignatures = new HashSet<>();
		Set<TypeMember> properties = new HashSet<>();
		Set<IndexInfo> indices = new HashSet<>();
		
		TypeContext localCtx = ctx;//TODO: push context
		
		for (InterfacePropertyTree prop : node.getDeclaredProperties()) {
			if (prop.getType().getKind() == Tree.Kind.INDEX_TYPE) {
				//Index signature
				IndexSignatureTree decl = (IndexSignatureTree) prop.getType();
				
				Type keyType = decl.getIndexType().accept(this, localCtx);
				Type valueType = decl.getReturnType().accept(this, localCtx);
				
				IndexInfo index = new IndexInfo(prop.isReadonly(), keyType, valueType);
				indices.add(index);
			} else if (prop.getKey() == null) {
				//Call signature
				Signature signature = this.mapFunctionType((FunctionTypeTree) prop.getType(), localCtx);
				callSignatures.add(signature);
			} else {
				//Property
				ObjectPropertyKeyTree _key = prop.getKey();
				if (_key.getKind() != Tree.Kind.IDENTIFIER)//TODO: fix
					throw new UnsupportedOperationException("Can't deal with non-string keys ATM");
				String key = ((IdentifierTree) _key).getName();
				
				Type value = prop.getType() == null ? null : prop.getType().accept(this, localCtx);
				
				TypeMember member = new TypeMember(!prop.isOptional(), prop.isReadonly(), LiteralType.of(key), value);
				properties.add(member);
			}
		}
		
		Type result = new ObjectTypeImpl(Collections.emptyList(), thisTP, properties, callSignatures, constructSignatures, indices);
		
		//Bind 'this' parameter (if present)
		if (thisTP != null)
			thisTP.rebind(result);
		
		return result;
	}
	
	@Override
	public Type visitIntersectionType(CompositeTypeTree node, TypeContext d) {
		return TypeCalculator.intersection(d, map(node.getConstituents(), d));
	}
	
	@Override
	public Type visitLiteralType(LiteralTypeTree<?> node, TypeContext d) {
		Object value = node.getValue().getValue();
		switch (node.getValue().getKind()) {
			case STRING_LITERAL:
				return new LiteralType<>(Type.Kind.STRING_LITERAL, value);
			case NUMERIC_LITERAL:
				return new LiteralType<>(Type.Kind.NUMBER_LITERAL, value);
			case BOOLEAN_LITERAL:
				return new LiteralType<>(Type.Kind.BOOLEAN_LITERAL, value);
			case NULL_LITERAL:
				return IntrinsicType.NULL;//Is this reachable?
			default:
				throw new IllegalArgumentException("Unexpected literal: " + node.getValue());
		}
	}

	@Override
	public Type visitMemberType(MemberTypeTree node, TypeContext d) {
		Type base = node.getBaseType().accept(this, d);
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Type visitSpecialType(SpecialTypeTree node, TypeContext d) {
		switch (node.getType()) {
			case BOOLEAN:
				return IntrinsicType.BOOLEAN;
			case NEVER:
				return IntrinsicType.NEVER;
			case NULL:
				return IntrinsicType.NULL;
			case NUMBER:
				return IntrinsicType.NUMBER;
			case STRING:
				return IntrinsicType.STRING;
			case UNDEFINED:
				return IntrinsicType.UNDEFINED;
			case VOID:
				return IntrinsicType.VOID;
			default:
				throw new IllegalArgumentException("Unknown special type: " + node.getType());
		}
	}
	
	@Override
	public TupleType visitTupleType(TupleTypeTree node, TypeContext d) {
		return new TupleType(map(node.getSlotTypes(), d));
	}
	
	@Override
	public Type visitUnionType(CompositeTypeTree node, TypeContext d) {
		return TypeCalculator.union(d, false, map(node.getConstituents(), d));
	}
	
}
