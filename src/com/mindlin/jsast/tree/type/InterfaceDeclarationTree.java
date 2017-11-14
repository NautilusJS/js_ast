package com.mindlin.jsast.tree.type;

import java.util.List;
import java.util.Set;

import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.Tree;

public interface InterfaceDeclarationTree extends TypeTree {
	/**
	 * Generic parameters
	 * @return generic parameters, else null if not generic type
	 */
	List<GenericParameterTree> getGenerics();
	
	/**
	 * 
	 * @return
	 */
	List<InterfacePropertyTree> getDeclaredProperties();
	
	Set<?> getDeclaredCallSignatures();
	Set<?> getDeclaredConstructorSignatures();
	Set<IndexSignatureTree> declaredIndices();
	
	TypeTree resolvedBaseContructor();
	
	List<TypeTree> resolvedBaseTypes();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_DECLARATION;
	}

	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitInterfaceType(this, data);
	}
}
