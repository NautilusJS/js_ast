package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeElementTree;

public interface MethodSignatureTree extends DecoratableTree, FunctionTree, TypeElementTree {
	@Override
	default StatementTree getBody() {
		return null;
	}
	
	@Override
	default Kind getKind() {
		return Kind.METHOD_SIGNATURE;
	}
}
