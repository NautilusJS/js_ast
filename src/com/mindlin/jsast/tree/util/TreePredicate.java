package com.mindlin.jsast.tree.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Predicate;

import com.mindlin.jsast.tree.Tree;

@FunctionalInterface
public interface TreePredicate extends Predicate<TreePath<? extends Tree>> {
	public static TreePredicate matchKind(Tree.Kind kind) {
		return node -> node.current().getKind() == kind;
	}
	
	public static TreePredicate matchKind(Tree.Kind...kinds) {
		EnumSet<Tree.Kind> _kinds = EnumSet.copyOf(Arrays.asList(kinds));
		return node -> _kinds.contains(node.current().getKind());
	}
	
	@Override
	boolean test(TreePath<? extends Tree> node);
	
	default boolean follow(TreePath<? extends Tree> node) {
		return true;
	}
}
