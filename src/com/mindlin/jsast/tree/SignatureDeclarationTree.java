package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.TypeElementTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface SignatureDeclarationTree extends NamedDeclarationTree {
	@Override
	PropertyName getName();
	
	/**
	 * Get generic parameters.
	 * @return generics
	 */
	List<TypeParameterDeclarationTree> getTypeParameters();

	/**
	 * Get function parameters.
	 * @return parameters
	 */
	List<ParameterTree> getParameters();
	
	/**
	 * Get <strong>declared</strong> function return type.
	 * <p>
	 * Note: the type provided by this method is <strong>wrong</strong> if this function is:
	 * <ul>
	 * 	<li>
	 * 		A generator (in which case, the *actual* return type is {@code Generator<getReturnType()>}).
	 * 		See {@link #isGenerator()}.
	 * 	</li>
	 * 	<li>
	 * 		async (in which case, the *actual* return type is {@code Promise<getReturnType()>}).
	 * 		See {@link #isAsync()}.
	 * 	</li>
	 * </ul>
	 * </p>
	 * @return declared return type
	 */
	TypeTree getReturnType();
	
	public static interface CallSignatureTree extends SignatureDeclarationTree, TypeElementTree {
		@Override
		default Kind getKind() {
			return Tree.Kind.CALL_SIGNATURE;
		}
		
		@Override
		default <R, D> R accept(TypeElementVisitor<R, D> visitor, D data) {
			return visitor.visitCallSignature(this, data);
		}
	}
	
	public static interface ConstructSignatureTree extends SignatureDeclarationTree, TypeElementTree {
		@Override
		default Kind getKind() {
			return Tree.Kind.CONSTRUCT_SIGNATURE;
		}
		
		@Override
		default <R, D> R accept(TypeElementVisitor<R, D> visitor, D data) {
			return visitor.visitConstructSignature(this, data);
		}
	}
}
