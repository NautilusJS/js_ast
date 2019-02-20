package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.SignatureDeclarationTree.CallSignatureTree;
import com.mindlin.nautilus.tree.SignatureDeclarationTree.ConstructSignatureTree;
import com.mindlin.nautilus.tree.type.IndexSignatureTree;

public interface TypeElementVisitor<R, D> {
	R visitCallSignature(CallSignatureTree node, D context);
	
	R visitConstructSignature(ConstructSignatureTree node, D context);
	
	R visitIndexSignature(IndexSignatureTree node, D context);
	
	R visitMethodSignature(MethodSignatureTree node, D context);
	
	R visitPropertySignature(PropertySignatureTree node, D context);
}
