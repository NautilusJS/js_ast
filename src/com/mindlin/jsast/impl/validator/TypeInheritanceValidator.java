package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.impl.tree.BinaryTypeTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.UnionTypeTree;

/**
 * Computes type inheritance calculations
 */
public class TypeInheritanceValidator {
	public static boolean canExtend(ASTContext context, TypeTree base, TypeTree child) {
		switch (base.getKind()) {
			case SPECIAL_TYPE: {
				SpecialType type = ((SpecialTypeTree) base).getType();
				if (child.getKind() == Kind.SPECIAL_TYPE) {
					SpecialType childType = ((SpecialTypeTree) child).getType();
					if (type == childType)
						return true;
					
					switch (type) {
						case ANY:
							return true;
							
						case UNDEFINED:
						case NEVER:
							return false;
					}
				}
				switch (type) {
					case ANY:
						return true;
					case VOID:
					case NEVER:
						return child.getKind() == Kind.SPECIAL_TYPE && ((SpecialTypeTree) child).getType() == SpecialType.NEVER;
				}
				return true;
			}
			case TYPE_UNION: {
				UnionTypeTree union = (UnionTypeTree) base;
				//TODO fix generic param binding
				return canExtend(context, union.getLeftType(), child) || canExtend(context, union.getRightType(), child);
			}
			case TYPE_INTERSECTION: {
				IntersectionTypeTree intersection = (IntersectionTypeTree) base;
				return canExtend(context, intersection.getLeftType(), child) && canExtend(context, intersection.getRightType(), child);
			}
			case ARRAY_TYPE: {
				if (child.getKind() != Tree.Kind.ARRAY_TYPE)
					return false;
				return canExtend(context, ((ArrayTypeTree)base).getBaseType(), ((ArrayTypeTree)child).getBaseType());
			}
			default:
				throw new IllegalArgumentException("Unsupported type: " + base.getKind());
		}
	}
	
	public static TypeTree computeMemberType(TypeContext context, TypeTree baseType, String memberName) {
		switch (baseType.getKind()) {
			case IDENTIFIER_TYPE:
				baseType = context.resolve((IdentifierTypeTree)baseType);
				break;
		}
		
		return null;
	}
	
	public static TypeTree computeIndexType(TypeTree baseType, TypeTree indexType) {
		return null;
	}
	
	public static TypeTree computeExpressionType(ASTContext context, ExpressionTree expression) {
		switch (expression.getKind()) {
			case IDENTIFIER:
				return context.getVariableImplicitType((IdentifierTree) expression);
			
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
