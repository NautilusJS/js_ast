package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.type.IndexSignatureTree;

public interface ClassElementVisitor<R, D> {
	R visitConstructorDeclaration(ConstructorDeclarationTree node, D context);
	
	R visitIndexSignature(IndexSignatureTree node, D context);
	
	R visitMethodDeclaration(MethodDeclarationTree node, D context);
	
	R visitPropertyDeclaration(PropertyDeclarationTree node, D context);
}
