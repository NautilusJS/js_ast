package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.TreeVisitor;

/**
 * Visitor for type trees
 * 
 * @author mailmindlin
 *
 * @param <R>
 *            Return type
 * @param <D>
 *            Data (context) type
 * @see TreeVisitor
 */
public interface TypeTreeVisitor<R, D> {
	R visitAnyType(AnyTypeTree node, D d);
	
	R visitArrayType(ArrayTypeTree node, D d);
	
	R visitFunctionType(FunctionTypeTree node, D d);
	
	R visitGenericRefType(GenericRefTypeTree node, D d);
	
	R visitGenericType(GenericParameterTree node, D d);
	
	R visitIdentifierType(IdentifierTypeTree node, D d);
	
	R visitIndexType(IndexSignatureTree node, D d);
	
	R visitInterfaceType(InterfaceTypeTree node, D d);
	
	R visitIntersectionType(CompositeTypeTree node, D d);
	
	R visitMemberType(MemberTypeTree node, D d);
	
	R visitSpecialType(SpecialTypeTree node, D d);
	
	R visitTupleType(TupleTypeTree node, D d);
	
	R visitUnionType(CompositeTypeTree node, D d);
}
