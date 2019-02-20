package com.mindlin.nautilus.transform;

import java.util.List;

import com.mindlin.nautilus.impl.util.Pair;
import com.mindlin.nautilus.impl.util.Scope;
import com.mindlin.nautilus.tree.Tree;
import com.mindlin.nautilus.tree.type.TypeTree;

public interface VariableContext {
	public static interface VariableInfo {
		TypeTree getDeclaredType();
		TypeTree getAssignedType();
		List<Pair<Scope, Tree>> getAssignments();
	}
}
