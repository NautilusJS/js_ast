package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.Tree.Kind;

/**
 * Extends/implements clause
 * @author mailmindlin
 */
public interface HeritageClauseTree extends Tree, UnvisitableTree {
	List<HeritageExpressionTree> getTypes();
}
