package com.mindlin.jsast.tree.type;

public interface TypeTreeVisitor<R, D> {
	R visitAnyType(AnyTypeTree node, D d);
	R visitArrayType(ArrayTypeTree node, D d);
	R visitFunctionType(FunctionTypeTree node, D d);
	R visitGenericRefType(GenericRefTypeTree node, D d);
	R visitGenericType(GenericTypeTree node, D d);
	R visitIdentifierType(IdentifierTypeTree node, D d);
	R visitIndexType(IndexTypeTree node, D d);
	R visitInterfaceType(InterfaceTypeTree node, D d);
	R visitIntersectionType(BinaryTypeTree node, D d);
	R visitMemberType(MemberTypeTree node, D d);
	R visitParameterType(ParameterTypeTree node, D d);
	R visitSpecialType(SpecialTypeTree node, D d);
	R visitTupleType(TupleTypeTree node, D d);
	R visitUnionType(BinaryTypeTree node, D d);
}
