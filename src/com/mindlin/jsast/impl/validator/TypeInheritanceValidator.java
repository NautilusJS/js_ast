package com.mindlin.jsast.impl.validator;

import java.util.List;

import com.mindlin.jsast.impl.tree.BinaryTypeTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.InterfaceTypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;

/**
 * Computes type inheritance calculations
 */
public class TypeInheritanceValidator {
	
	public static InterfaceTypeTree reduceInterface(TypeContext context, List<IdentifierTree> supertypes, List<InterfacePropertyTree> baseProps) {
		//We can pretend that interface inheritance is the same thing as an intersection of the base with all of its supertypes
		
		throw new UnsupportedOperationException();
	}
	/**
	 * Resolve {@code type} until 
	 * @param context
	 * @param type
	 * @return
	 */
	public static TypeTree resolve(TypeContext context, TypeTree type) {
		//TODO finish
		return type;
	}
	/**
	 * Return if {@code child} is a subtype of {@code base}.
	 * @param context Context to use when resolving types referenced by {@code base} and {@code child}
	 * @param base Base type
	 * @param child Possible subtype of {@code base}
	 * @return if {@code child} is a subtype of {@code base}
	 * @see #isAssignableFrom(TypeContext, TypeTree, TypeTree)
	 */
	public static boolean isSubtype(TypeContext context, TypeTree base, TypeTree child) {
		if (base.getKind() == Kind.ANY_TYPE)
			return true;
		
		SpecialType baseType = null;
		if (base.getKind() == Kind.SPECIAL_TYPE)
			baseType = ((SpecialTypeTree) base).getType();
		
		switch (child.getKind()) {
			case SPECIAL_TYPE: {
				SpecialType childType = ((SpecialTypeTree) child).getType();
				
				if (childType == baseType)
					return true;
				
				switch (childType) {
					case NULL:
						return baseType != SpecialType.UNDEFINED;
					case UNDEFINED:
						return true;
					default:
						break;
				}
				
				break;
			}
			case TYPE_UNION:
				return isSubtype(context, base, ((UnionTypeTree)child).getLeftType()) && isSubtype(context, base, ((UnionTypeTree)child).getRightType());
			case TYPE_INTERSECTION:
				return isSubtype(context, base, ((IntersectionTypeTree)child).getRightType()) || isSubtype(context, base, ((IntersectionTypeTree) child).getRightType());
			default:
				throw new UnsupportedOperationException();
		}
		switch (base.getKind()) {
			case ANY_TYPE:
				return true;
			case SPECIAL_TYPE: {
				if (child.getKind() == Kind.SPECIAL_TYPE) {
					SpecialType childType = ((SpecialTypeTree) child).getType();
					if (baseType == childType)
						return true;
					
					if (childType == SpecialType.UNDEFINED)
						return true;
					if (childType == SpecialType.NULL && baseType != SpecialType.UNDEFINED)
						return true;
				}
				return false;
			}
			case TYPE_UNION: {
				UnionTypeTree union = (UnionTypeTree) base;
				//TODO fix generic param binding
				return isSubtype(context, union.getLeftType(), child) || isSubtype(context, union.getRightType(), child);
			}
			case TYPE_INTERSECTION: {
				IntersectionTypeTree intersection = (IntersectionTypeTree) base;
				return isSubtype(context, intersection.getLeftType(), child) && isSubtype(context, intersection.getRightType(), child);
			}
			case ARRAY_TYPE: {
				if (child.getKind() != Tree.Kind.ARRAY_TYPE)
					return false;
				return isSubtype(context, ((ArrayTypeTree)base).getBaseType(), ((ArrayTypeTree)child).getBaseType());
			}
			default:
				throw new IllegalArgumentException("Unsupported type: " + base.getKind());
		}
	}
	
	public static boolean isAssignableFrom(TypeContext context, TypeTree assignee, TypeTree value) {
		//T is assignee, S is value
		assignee = TypeInheritanceValidator.resolve(context, assignee);
		value = TypeInheritanceValidator.resolve(context, value);
		
		if (assignee.getKind() == Kind.ANY_TYPE || value.getKind() == Kind.ANY_TYPE)
			return true;
		
		//Look this up for later
		SpecialType assigneeST = (assignee.getKind() == Kind.SPECIAL_TYPE) ? ((SpecialTypeTree) assignee).getType() : null;
		
		if (value.getKind() == Kind.SPECIAL_TYPE) {
			SpecialType valueST = ((SpecialTypeTree) value).getType();
			switch (valueST) {
				case UNDEFINED:
					return true;
				case NULL:
					return assigneeST != SpecialType.UNDEFINED;
				default:
					//Partial shortcut for isEquivalent()
					if (valueST == assigneeST)
						return true;
			}
		}
		
		if (isEquivalent(context, assignee, value))
			return true;
		
		switch (value.getKind()) {
			case LITERAL_TYPE:
			default:
				break;
		}
		throw new UnsupportedOperationException();
	}
	
	public static boolean isEquivalent(TypeContext context, TypeTree a, TypeTree b) {
		if (a == b)//Almost never happens, but cheap to check
			return true;
		
		//TODO fix for identifier-aliases
		if (a.getKind() != b.getKind())
			return false;
		
		switch (a.getKind()) {
			case IDENTIFIER_TYPE: {
				String nameA = ((IdentifierTypeTree) a).getIdentifier().getName();
				String nameB = ((IdentifierTypeTree) b).getIdentifier().getName();
				//TODO check generics
				return nameA.equals(nameB);
			}
			case SPECIAL_TYPE:
				//This should hold up pretty well.
				return ((SpecialTypeTree) a).getType() == ((SpecialTypeTree) b).getType();
			case TYPE_UNION: {
				UnionTypeTree unionA = (UnionTypeTree) a, unionB = (UnionTypeTree) b;
				if (isEquivalent(context, unionA.getLeftType(), unionB.getLeftType()))
					return isEquivalent(context, unionA.getRightType(), unionB.getRightType());
				else
					return isEquivalent(context, unionA.getLeftType(), unionB.getRightType())
							&& isEquivalent(context, unionA.getRightType(), unionB.getLeftType());
			}
			case TYPE_INTERSECTION: {
				IntersectionTypeTree intersectionA = (IntersectionTypeTree) a, intersectionB = (IntersectionTypeTree) b;
				if (isEquivalent(context, intersectionA.getLeftType(), intersectionB.getLeftType()))
					return isEquivalent(context, intersectionA.getRightType(), intersectionB.getRightType());
				else
					return isEquivalent(context, intersectionA.getLeftType(), intersectionB.getRightType())
							&& isEquivalent(context, intersectionA.getRightType(), intersectionB.getLeftType());
			}
			default:
				throw new IllegalArgumentException("Unknown type kind: " + a.getKind());
		}
	}
	
	public static TypeTree computeIndexType(TypeTree baseType, TypeTree indexType) {
		return null;
	}
	
	public static TypeTree computeExpressionType(ASTContext context, ExpressionTree expression) {
		switch (expression.getKind()) {
			case IDENTIFIER:
				return context.getVariableImplicitType((IdentifierTree) expression);
			default:
				break;
		}
		return null;
	}
	
	public static TypeTree union(ASTContext context, TypeTree a, TypeTree b) {
		return new BinaryTypeTree(-1, -1, true, a, Kind.TYPE_UNION, b);
	}
	
	public static TypeTree reduce(ASTContext context, TypeTree type) {
		if (type.getKind() == Kind.TYPE_UNION) {
			
		}
		return type;
	}
}
