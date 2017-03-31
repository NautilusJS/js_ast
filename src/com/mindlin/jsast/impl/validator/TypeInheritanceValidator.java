package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;

/**
 * Computes type inheritance calculations
 */
public class TypeInheritanceValidator {
	public static boolean canExtend(TypeTree base, TypeTree child) {
		switch (base.getKind()) {
			case ANY_TYPE:
				return true;
			case VOID_TYPE:
				
			case TYPE_UNION: {
				UnionTypeTree union = (UnionTypeTree) base;
				//TODO fix generic param binding
				return canExtend(union.getLeftType(), child) || canExtend(union.getRightType(), child);
			}
			case TYPE_INTERSECTION: {
				IntersectionTypeTree intersection = (IntersectionTypeTree) base;
				return canExtend(intersection.getLeftType(), child) && canExtend(intersection.getRightType(), child);
			}
			case ARRAY_TYPE: {
				if (child.getKind() != Tree.Kind.ARRAY_TYPE)
					return false;
				return canExtend(((ArrayTypeTree)base).getBaseType(), ((ArrayTypeTree)child).getBaseType());
			}
			
			default:
				throw new IllegalArgumentException("Unsupported type: " + base.getKind());
		}
	}
}
