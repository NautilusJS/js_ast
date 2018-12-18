package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.DeclarationStatementTree;
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.NamedDeclarationTree;
import com.mindlin.jsast.tree.StatementTreeVisitor;
import com.mindlin.jsast.tree.Tree;

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
