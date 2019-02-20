package com.mindlin.nautilus.tree.type;

import java.util.List;

import com.mindlin.nautilus.tree.DeclarationStatementTree;
import com.mindlin.nautilus.tree.HeritageClauseTree;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.NamedDeclarationTree;
import com.mindlin.nautilus.tree.StatementTreeVisitor;
import com.mindlin.nautilus.tree.Tree;

public interface InterfaceDeclarationTree extends NamedDeclarationTree, DeclarationStatementTree {
	
	@Override
	IdentifierTree getName();
	
	List<TypeParameterDeclarationTree> getTypeParameters();
	
	List<HeritageClauseTree> getHeritage();
	
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
