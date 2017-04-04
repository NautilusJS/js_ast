package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;

/**
 * Computes type inheritance calculations
 */
public class TypeInheritanceValidator {
	public static boolean canExtend(ASTContext context, TypeTree base, TypeTree child) {
		switch (base.getKind()) {
			case ANY_TYPE:
				return true;
			case VOID_TYPE:
				return child.getKind() == Tree.Kind.VOID_TYPE;
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
	
	public static TypeTree computeExpressionType(ASTContext context, ExpressionTree expression) {
		switch (expression.getKind()) {
			case IDENTIFIER:
				return context.getVariableImplicitType((IdentifierTree) expression);
			
		}
		return null;
	}
}
