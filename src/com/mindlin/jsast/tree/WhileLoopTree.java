package com.mindlin.jsast.tree;

/**
 * While loop node.
 * @see DoWhileLoopTree for do...while loops
 * @author mailmindlin
 */
public interface WhileLoopTree extends ConditionalLoopTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.WHILE_LOOP;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitWhileLoop(this, data);
	}
}