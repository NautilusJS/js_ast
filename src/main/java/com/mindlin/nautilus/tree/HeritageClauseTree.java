package com.mindlin.nautilus.tree;

import java.util.List;

import com.mindlin.nautilus.tree.Tree.Kind;

/**
 * Extends/implements clause
 * @author mailmindlin
 */
public interface HeritageClauseTree extends Tree, UnvisitableTree {
	List<HeritageExpressionTree> getTypes();
}
