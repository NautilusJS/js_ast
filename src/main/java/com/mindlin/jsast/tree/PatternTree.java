package com.mindlin.jsast.tree;

/**
 * A generic super-interface for trees representing ES6 destructuring operators.
 * 
 * @see ArrayPatternTree
 * @see ObjectPatternTree
 * @see AssignmentPatternTree
 * 
 * @author mailmindlin
 */
public interface PatternTree extends Tree, VariableDeclarationOrPatternTree, DeclarationName {
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((PatternTreeVisitor<R, D>) visitor, data);
	}
	
	<R, D> R accept(PatternTreeVisitor<R, D> visitor, D data);
}
