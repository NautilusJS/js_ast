package com.mindlin.jsast.tree;

/**
 * While loop node.
 * @see DoWhileLoopTree for {@code do...while} loops
 * @author mailmindlin
 */
public interface WhileLoopTree extends ConditionalLoopTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.WHILE_LOOP;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitWhileLoop(this, data);
	}
}