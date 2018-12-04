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
	R visitArrayType(ArrayTypeTree node, D d);
	
	R visitConditionalType(ConditionalTypeTree node, D d);
	
	R visitConstructorType(ConstructorTypeTree node, D d);
	
	R visitFunctionType(FunctionTypeTree node, D d);
	
	R visitIdentifierType(IdentifierTypeTree node, D d);
	
	R visitInterfaceType(ObjectTypeTree node, D d);
	
	R visitIntersectionType(CompositeTypeTree node, D d);
	
	R visitUnaryType(UnaryTypeTree node, D d);
	
	R visitLiteralType(LiteralTypeTree<?> node, D d);
	
	R visitMappedType(MappedTypeTree node, D d);
	
	R visitMemberType(MemberTypeTree node, D d);
	
	R visitSpecialType(SpecialTypeTree node, D d);
	
	R visitTupleType(TupleTypeTree node, D d);
	
	R visitUnionType(CompositeTypeTree node, D d);
}
