package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.DeclarationStatementTree;
import com.mindlin.jsast.tree.NamedDeclarationTree;
import com.mindlin.jsast.tree.StatementTreeVisitor;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

public interface InterfaceDeclarationTree extends NamedDeclarationTree, DeclarationStatementTree {
	
	List<TypeParameterDeclarationTree> getTypeParameters();
	
	List<TypeTree> getSupertypes();
	
	//TODO: why not just use an ObjectTypeTree here?
	List<TypeElementTree> getDeclaredMembers();
	
	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_DECLARATION;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitInterfaceDeclaration(this, data);
	}
}
